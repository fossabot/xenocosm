package xenocosm.http
package services

import java.util.UUID
import cats.effect.IO

import xenocosm.data.Trader

trait DataStore {
  def createTrader(trader:Trader):IO[Unit]
  def deleteTrader(trader:Trader):IO[Unit]
  def selectTrader(traderID:UUID):IO[Option[Trader]]
  def updateTrader(trader:Trader):IO[Unit]
}
