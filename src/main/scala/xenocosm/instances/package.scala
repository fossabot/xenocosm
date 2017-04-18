package xenocosm

package object instances {
  object interop extends FansiInstances.Instances
    with JavaInstances.Instances
    with SquantsInstances.LengthInstances
    with SquantsInstances.MassInstances
    with SquantsInstances.PowerInstances
    with SquantsInstances.TemperatureInstances
}
