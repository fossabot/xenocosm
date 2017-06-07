package xenocosm
package app
package data

sealed trait XenocosmError extends Product with Serializable
case object KeyDoesNotExist extends XenocosmError
case object IOTimeout extends XenocosmError
case object KeyExists extends XenocosmError
case class UnexpectedError(underlying:Throwable) extends XenocosmError
