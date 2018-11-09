package pseudoglot
package data

final case class Magic(
  pulmonics:(Int, Int),
  vowels:(Int, Int),
  rules:(Int, Int),
  literal:Double,
  concat:Double,
  choose:Double
)

object Magic {
  implicit lazy val default:Magic = Magic((7, 21), (3, 9), (5, 10), 0.33, 0.32, 0.31)
}
