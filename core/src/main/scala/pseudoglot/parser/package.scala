package pseudoglot

import data.{Phones, PhonotacticRule}

package object parser {
  type PhonotacticParser = Either[String, PhonotacticRule]
  type PhonesParser = Either[String, Phones]
}
