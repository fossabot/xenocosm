package xenocosm.http
package services

import java.util.UUID
import cats.effect.IO

import xenocosm.data.{ForeignID, Identity, Trader}

trait DataStore {
  def createIdentity(identity:Identity):IO[Unit]
  def deleteIdentity(identityID:UUID):IO[Unit]
  def selectIdentity(token:UUID):IO[Option[Identity]]
  def selectIdentity(ref:ForeignID):IO[Option[Identity]]
  def updateIdentity(identity:Identity):IO[Unit]

  def deleteIdentity(identity:Identity):IO[Unit] =
    deleteIdentity(identity.uuid)

  def listTraders(identity:Identity):IO[List[Trader]]
  def createTrader(identity:Identity, trader:Trader):IO[Unit]
  def deleteTrader(identityID:UUID, traderID:UUID):IO[Unit]
  def selectTrader(identityID:UUID, traderID:UUID):IO[Option[Trader]]
  def updateTrader(identity:Identity, trader:Trader):IO[Unit]

  /**
    * A helper to store a trader from the currently selected trader of an identity
   */
  def createTrader(identity:Identity):IO[Unit] =
    identity.trader match {
      case None => IO.unit
      case Some(trader) => createTrader(identity, trader)
    }

  def deleteTrader(identity:Identity, trader:Trader):IO[Unit] =
    deleteTrader(identity.uuid, trader.uuid)
}
