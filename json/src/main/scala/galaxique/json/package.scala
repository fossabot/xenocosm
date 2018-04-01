package galaxique

package object json {
  object galaxy extends GalaxyJson
  object morganKeenan extends MorganKeenanJson
  object planet extends PlanetJson
  object point3 extends Point3Json
  object star extends StarJson
  object universe extends UniverseJson

  object implicits extends All
}
