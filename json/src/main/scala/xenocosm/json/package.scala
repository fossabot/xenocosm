package xenocosm

package object json {
  object cargo extends CargoJson
  object cosmicLocation extends CosmicLocationJson
  object foreignID extends ForeignIDJson
  object identity extends IdentityJson
  object ship extends ShipJson
  object shipModule extends ShipModuleJson
  object trader extends TraderJson
  object xenocosmError extends XenocosmErrorJson
  object xenocosmEvent extends XenocosmEventJson

  object implicits extends All
}
