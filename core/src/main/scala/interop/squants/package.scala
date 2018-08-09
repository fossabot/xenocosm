package interop

package object squants {
  object instances extends space.LengthInstances
                      with mass.MassInstances
                      with energy.PowerInstances
                      with thermal.TemperatureInstances
}
