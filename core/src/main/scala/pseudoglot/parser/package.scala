package pseudoglot

import data.{Phone, PhonotacticRule}

package object parser {
  type PhonotacticParser = Either[String, PhonotacticRule]
  type PhoneSeqParser = Either[String, Seq[Phone]]
}
