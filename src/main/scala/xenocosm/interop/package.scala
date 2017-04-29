package xenocosm

package object interop {
  object instances extends FansiInstances.Instances
                      with JavaInstances.Instances
                      with SquantsInstances.LengthInstances
                      with SquantsInstances.MassInstances
                      with SquantsInstances.PowerInstances
                      with SquantsInstances.TemperatureInstances

  object syntax extends SquantsSyntax.Syntax
}
