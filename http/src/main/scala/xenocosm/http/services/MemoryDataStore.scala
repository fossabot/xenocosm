package xenocosm.http.services

import java.util.UUID
import scala.collection.mutable.{Map => MutMap}
import cats.effect.IO

import xenocosm.data.{ForeignID, Identity, Trader}

final class MemoryDataStore extends DataStore {
  import cats.implicits._
  import ForeignID.instances._

  private val identities:MutMap[UUID, Identity] = MutMap.empty[UUID, Identity]
  private val traders:MutMap[UUID, Trader] = MutMap.empty[UUID, Trader]

  def createIdentity(identity:Identity):IO[Unit] = IO.pure {
    identities += (identity.uuid -> identity)
    ()
  }

  def deleteIdentity(identity:Identity):IO[Unit] = IO.pure {
    identities -= identity.uuid
    ()
  }

  def selectIdentity(uuid:UUID):IO[Option[Identity]] =
    IO.pure(identities.get(uuid))

  def selectIdentity(ref:ForeignID):IO[Option[Identity]] =
    IO.pure(identities.find({ case (_, indentity) => indentity.ref.exists(_ === ref) }).map(_._2))

  def updateIdentity(identity:Identity):IO[Unit] = IO.pure {
    identities += (identity.uuid -> identity)
    ()
  }

  def createTrader(trader:Trader):IO[Unit] = IO.pure {
    traders += (trader.uuid -> trader)
    ()
  }

  def deleteTrader(trader:Trader):IO[Unit] = IO.pure {
    traders -= trader.uuid
    ()
  }

  def selectTrader(traderID:UUID):IO[Option[Trader]] =
    IO.pure(traders.get(traderID))

  def updateTrader(trader:Trader):IO[Unit] = IO.pure {
    traders += (trader.uuid -> trader)
    ()
  }
}
