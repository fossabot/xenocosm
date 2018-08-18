package xenocosm

import spire.math.UInt
import spire.random.Dist
import squants.space.Length

import xenocosm.data.{CosmicLocation, Ship, Trader}

sealed trait XenocosmCommand
final case class CreateTrader(moves:UInt) extends XenocosmCommand
final case class MoveShip(moves:UInt, ship:Ship, loc:CosmicLocation) extends XenocosmCommand

object XenocosmCommand {
  import data.Trader.instances._

  private def checkMoves(remaining:UInt):Either[XenocosmError, Unit] =
    remaining match {
      case UInt.MinValue => Left(NoMovesRemaining)
      case _ => Right(())
    }

  private def checkDistance(from:CosmicLocation, to:CosmicLocation, max:Length):Either[XenocosmError, Unit] = {
    val distance = from distance to
    if (distance > max) {
      Left(TooFar(distance))
    } else {
      Right(())
    }
  }

  private def checkNavDistance(ship:Ship, to:CosmicLocation):Either[XenocosmError, Unit] =
    checkDistance(ship.loc, to, ship.maxNavDistance)

  private def checkTravelDistance(ship:Ship, to:CosmicLocation):Either[XenocosmError, Unit] =
    checkDistance(ship.loc, to, ship.maxTravelDistance)

  trait CreateTraderInstances {
    implicit val createTraderHasCommandHandler:CommandHandler[CreateTrader, TraderCreated] =
      CommandHandler(
        cmd => checkMoves(cmd.moves),
        cmd => Dist[Trader].map(x => TraderCreated(cmd.moves - UInt(1), x))
      )
  }

  trait MoveShipInstances {
    implicit val moveShipHasCommandHandler:CommandHandler[MoveShip, ShipMoved] =
      CommandHandler(
        cmd => for {
          _ <- checkMoves(cmd.moves)
          _ <- checkNavDistance(cmd.ship, cmd.loc)
          _ <- checkTravelDistance(cmd.ship, cmd.loc)
        } yield (),
        cmd => Dist.constant {
          val (ship, elapsedShip, elapsedUniverse) = cmd.ship.travelTo(cmd.loc)
          ShipMoved(cmd.moves - UInt(1), ship, elapsedShip, elapsedUniverse)
        }
      )
  }

  trait Instances
    extends CreateTraderInstances
       with MoveShipInstances

  object instances extends Instances
}
