package xenocosm

package object universe {
  // 6.67408(31)×10−11 m3⋅kg−1⋅s−2
  val G:Double = 6.67408e-11

  object instances extends data.Galaxy.Instances
                      with data.HubbleSequence.Instances
                      with data.MorganKeenan.Instances
                      with data.Star.Instances
                      with data.StellarSystemBody.PlanetInstances
                      with data.StellarSystemBody.DwarfPlanetInstances
                      with data.StellarSystemBody.SmallBodyInstances
                      with data.Universe.Instances
}
