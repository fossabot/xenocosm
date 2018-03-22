package xenocosm
package data

sealed abstract class Currency(val symbol:String)
case object SpaceBuck extends Currency("\u2605")
