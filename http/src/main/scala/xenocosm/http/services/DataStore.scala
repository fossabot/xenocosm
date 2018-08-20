package xenocosm.http
package services

import java.util.UUID
import cats.effect.IO

import xenocosm.data.{ForeignID, Identity, Trader}

trait DataStore {
  def createIdentity(identity:Identity):IO[Unit]
  def deleteIdentity(identity:Identity):IO[Unit]
  def selectIdentity(token:UUID):IO[Option[Identity]]
  def selectIdentity(ref:ForeignID):IO[Option[Identity]]
  def updateIdentity(identity:Identity):IO[Unit]

  def createTrader(trader:Trader):IO[Unit]
  def deleteTrader(trader:Trader):IO[Unit]
  def selectTrader(traderID:UUID):IO[Option[Trader]]
  def updateTrader(trader:Trader):IO[Unit]
}
