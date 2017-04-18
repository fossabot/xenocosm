package xenocosm.syntax

import spire.math.UByte
import squants.thermal.Temperature

object SquantsSyntax {
  trait Syntax {
    implicit class ColorTemperature(underlying:Temperature) {

      /** Kelvin to RGB calculation
        *
        * http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
        */
      def rgb = {
        val t = underlying.toKelvinDegrees / 100d

        val r = if (t <= 66) {
          UByte.MaxValue
        } else {
          UByte(math.max(math.min(math.floor(329.698727446d * math.pow(t - 60d, -0.1332047592d)), 255d), 0d).toInt)
        }

        val g = if (t <= 66) {
          UByte(math.max(math.min(math.floor(99.4708025861d * math.log(t) - 161.1195681661d), 255d), 0d).toInt)
        } else {
          UByte(math.max(math.min(math.floor(288.1221695283 * math.pow(t - 60d, -0.0755148492)), 255d), 0d).toInt)
        }

        val b = if (t >= 66) {
          UByte.MaxValue
        } else if(t <= 19) {
          UByte.MinValue
        } else {
          UByte(math.max(math.min(math.floor(138.5177312231d * math.log(t - 10d) - 305.0447927307d), 255d), 0d).toInt)
        }

        (r, g, b)
      }
    }
  }
  object syntax extends Syntax
}
