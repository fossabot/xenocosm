package xenocosm.http.services

import java.util.UUID
import scala.collection.mutable.{Map => MutMap}
import cats.effect.IO

import xenocosm.data.{ForeignID, Identity, Trader}

final class MemoryDataStore extends DataStore {
  import cats.implicits._
  import ForeignID.instances._

  private val identities:MutMap[UUID, Identity] = MutMap.empty[UUID, Identity]
  private val traders:MutMap[(UUID, UUID), Trader] = MutMap.empty[(UUID, UUID), Trader]

  def createIdentity(identity:Identity):IO[Unit] = IO.pure {
    identities += (identity.uuid -> identity)
    ()
  }

  def deleteIdentity(identityID:UUID):IO[Unit] = IO.pure {
    identities -= identityID
    ()
  }

  def selectIdentity(uuid:UUID):IO[Option[Identity]] =
    IO.pure(identities.get(uuid))

  def selectIdentity(ref:ForeignID):IO[Option[Identity]] =
    IO.pure(identities.find({ case (_, identity) => identity.ref.exists(_ === ref) }).map(_._2))

  def updateIdentity(identity:Identity):IO[Unit] = IO.pure {
    identities += (identity.uuid -> identity)
    ()
  }

  def listTraders(identity:Identity):IO[List[Trader]] = IO.pure {
    traders.filterKeys(_._1 === identity.uuid).values.toList
  }

  def createTrader(identity:Identity, trader:Trader):IO[Unit] = IO.pure {
    traders += ((identity.uuid, trader.uuid) -> trader)
    ()
  }

  def deleteTrader(identityID:UUID, traderID:UUID):IO[Unit] = IO.pure {
    traders -= ((identityID, traderID))
    ()
  }

  def selectTrader(identityID:UUID, traderID:UUID):IO[Option[Trader]] =
    IO.pure(traders.get((identityID, traderID)))

  def updateTrader(identity:Identity, trader:Trader):IO[Unit] = IO.pure {
    traders += ((identity.uuid, trader.uuid) -> trader)
    ()
  }
}
