/**
  * Provides data types and typeclasses for Xenocosm, a space trader
  *
  */
package object xenocosm {
  object instances
    extends data.Cargo.Instances
       with data.CosmicLocation.Instances
       with data.CosmicLocation.Syntax
       with data.ForeignID.Instances
       with data.Identity.Instances
       with data.Ship.Instances
       with data.ShipModule.Instances
       with data.Trader.Instances
}
