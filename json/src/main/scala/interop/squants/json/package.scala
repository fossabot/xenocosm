package interop.squants

package object json {
  object instances extends DensityJson
                      with LengthJson
                      with MassJson
                      with PowerJson
                      with TemperatureJson
                      with TimeJson
                      with VelocityJson
                      with VolumeFlowJson
                      with VolumeJson
}
