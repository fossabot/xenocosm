organization := "com.robotsnowfall"
name := "xenocosm"
scalaVersion := "2.12.2"

val versions = new {
  val cats = "0.9.0"
  val fansi = "0.2.3"
  val fastparse = "0.4.2"
  val http4s = "0.17.0-M1"
  val logback = "1.1.3"
  val pureconfig = "0.7.0"
  val scalacheck = "1.13.5"
  val scalatest = "3.0.1"
  val spire = "0.14.1"
  val squants = "1.3.0-SNAPSHOT"
}

libraryDependencies ++= Seq(
  "org.typelevel"         %% "cats-core"           % versions.cats,
  "com.lihaoyi"           %% "fansi"               % versions.fansi,
  "com.lihaoyi"           %% "fastparse"           % versions.fastparse,
  "org.http4s"            %% "http4s-dsl"          % versions.http4s,
  "org.http4s"            %% "http4s-blaze-server" % versions.http4s,
  "org.http4s"            %% "http4s-blaze-client" % versions.http4s,
  "org.typelevel"         %% "spire"               % versions.spire,
  "org.typelevel"         %% "squants"             % versions.squants,
  "com.github.pureconfig" %% "pureconfig"          % versions.pureconfig,
  "com.github.pureconfig" %% "pureconfig-squants"  % versions.pureconfig,
  "ch.qos.logback"         % "logback-classic"     % versions.logback,
  "org.typelevel"         %% "cats-laws"           % versions.cats       % "test",
  "org.typelevel"         %% "spire-laws"          % versions.spire      % "test",
  "org.scalacheck"        %% "scalacheck"          % versions.scalacheck % "test",
  "org.scalatest"         %% "scalatest"           % versions.scalatest  % "test"
)

resolvers += Resolver.sonatypeRepo("snapshots")

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:experimental.macros",
  "-Xfatal-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused-import",
  "-Xfuture"
)

scalacOptions in (Compile, console) ~= {_.filterNot("-Ywarn-unused-import" == _)}

parallelExecution in Test := false

// scoverage settings
coverageEnabled := true
coverageMinimum := 75
coverageFailOnMinimum := true
coverageHighlighting := false

// sbt-buildinfo settings
enablePlugins(BuildInfoPlugin)
buildInfoKeys := Seq[BuildInfoKey](name, version)
buildInfoPackage := "xenocosm"
buildInfoObject := "XenocosmBuild"
