package xenocosm.http

import pureconfig.error.ConfigReaderFailures

final case class XenocosmConfig(http:XenocosmConfig.Http)

object XenocosmConfig {
  sealed trait Adt
  final case class Http(host:String, port:Int, secret:String) extends Adt

  def load:Either[ConfigReaderFailures, XenocosmConfig] = pureconfig.loadConfig[XenocosmConfig]
  def loadUnsafe:XenocosmConfig = load.toOption.get
}
