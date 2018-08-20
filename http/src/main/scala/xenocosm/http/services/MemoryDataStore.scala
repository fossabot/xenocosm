package xenocosm.http.services

import java.util.UUID
import scala.collection.mutable.{Map => MutMap}
import cats.effect.IO

import xenocosm.data.{ForeignID, Identity, Trader}

final class MemoryDataStore extends DataStore {
  private val identities:MutMap[UUID, Identity] = MutMap.empty[UUID, Identity]
  private val traders:MutMap[UUID, Trader] = MutMap.empty[UUID, Trader]

  def createIdentity(identity:Identity):IO[Unit] = {
    identities += (identity.uuid -> identity)
    IO.unit
  }

  def deleteIdentity(identity:Identity):IO[Unit] = {
    identities -= identity.uuid
    IO.unit
  }

  def selectIdentity(uuid:UUID):IO[Option[Identity]] =
    IO.pure(identities.get(uuid))

  def selectIdentity(ref:ForeignID):IO[Option[Identity]] =
    IO.pure(None)

  def updateIdentity(identity:Identity):IO[Unit] = {
    identities += (identity.uuid -> identity)
    IO.unit
  }

  def createTrader(trader:Trader):IO[Unit] = {
    traders += (trader.uuid -> trader)
    IO.unit
  }

  def deleteTrader(trader:Trader):IO[Unit] = {
    traders -= trader.uuid
    IO.unit
  }

  def selectTrader(traderID:UUID):IO[Option[Trader]] =
    IO.pure(traders.get(traderID))

  def updateTrader(trader:Trader):IO[Unit] = {
    traders += (trader.uuid -> trader)
    IO.unit
  }
}
