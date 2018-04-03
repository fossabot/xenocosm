/**
  * <img src="/xenocosm/img/galaxique-128.png" />
  * Provides data types and typeclasses for procedurally-generated universes
  *
  * ==Overview==
  * As much as possible, all data types have an instance of `spire.random.Dist`
  * in the companion object.
  */
package object galaxique {
  // 6.67408(31)×10−11 m3⋅kg−1⋅s−2
  val G:Double = 6.67408e-11

  object implicits extends MorganKeenan.Instances
    with data.Point3.Instances
    with data.Universe.Instances
    with data.Galaxy.Instances
    with data.Star.Instances
    with data.Planet.Instances
    with SparseSpace3.Syntax
}
