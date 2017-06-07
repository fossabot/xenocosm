package xenocosm
package app
package client

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import com.basho.riak.client.api.RiakClient
import com.basho.riak.client.api.commands.datatypes.{CounterUpdate, FetchMap, MapUpdate, UpdateMap}
import com.basho.riak.client.core.{RiakCluster, RiakNode}
import com.basho.riak.client.core.query.{Location, Namespace}
import fs2.{Strategy, Task}

import xenocosm.app.data._

/**
  * Simple wrapper around Basho's Riak Client
  */
class RiakClientWrapper(config:XenocosmConfig.RiakConfig)(implicit ec:ExecutionContext) {
  implicit val S:Strategy = Strategy.sequential

  private def makeNode(host:String, port:Int):RiakNode =
    new RiakNode.Builder()
      .withMinConnections(config.minConnections)
      .withMaxConnections(config.maxConnections)
      .withRemoteAddress(host)
      .withRemotePort(port)
      .build()

  private lazy val nodes:Seq[RiakNode] =
    config.nodes.map(x ⇒ makeNode(x.host, x.port))

  private lazy val cluster:RiakCluster =
    RiakCluster
      .builder(nodes.asJava)
      .withExecutionAttempts(config.executionAttempts)
      .build()

  private lazy val underlying:RiakClient = new RiakClient(cluster)

  def startup:Future[Unit] = Future(underlying.getRiakCluster.start())
  def shutdown:Future[Unit] = Future(underlying.shutdown().get()).map(_ ⇒ ())

  private lazy val markovNS:Namespace = new Namespace("markov", "markov")

  def updateMarkovModel(ngram:String, map:Map[String, Long]):Task[Unit] =
    Task.delay {
      val location = new Location(markovNS, ngram)
      val mapUpdate = map.foldLeft(new MapUpdate())({
        case (update, (word, count)) ⇒
          update.update(word, new CounterUpdate(count))
      })

      underlying.execute(new UpdateMap.Builder(location, mapUpdate).build)
      ()
    }

  def fetchModelFor(ngram:String):Task[Map[String, Long]] =
    Task.delay {
      val fetch = new FetchMap.Builder(new Location(markovNS, ngram)).build()
      underlying
        .execute(fetch)
        .getDatatype
        .view().asScala.toMap
        .map({
          case (word, maps) ⇒ word.toStringUtf8 → Long2long(maps.asScala.map(_.getAsCounter.view()).head)
        })
    }
}
