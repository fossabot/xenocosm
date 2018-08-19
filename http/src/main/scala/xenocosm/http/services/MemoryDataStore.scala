package xenocosm.http.services

import java.util.UUID
import scala.collection.mutable.{Map => MutMap}
import cats.effect.IO

import xenocosm.data.Trader

final class MemoryDataStore extends DataStore {
  private val traders:MutMap[UUID, Trader] = MutMap.empty[UUID, Trader]

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
