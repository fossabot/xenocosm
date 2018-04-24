package xenocosm

package object json {
  object cargo extends CargoJson
  object cosmicLocation extends CosmicLocationJson
  object ship extends ShipJson
  object shipModule extends ShipModuleJson
  object trader extends TraderJson

  object implicits extends All
}
