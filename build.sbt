
lazy val xenocosm = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings)
  .settings(scoverageSettings)
  .settings(buildInfoSettings)

lazy val commonSettings = Seq(
  name := "xenocosm",
  organization := "com.robotsnowfall",
  scalaVersion := "2.12.2",
  resolvers += Resolver.sonatypeRepo("snapshots"),
  libraryDependencies ++= commonDependencies,
  parallelExecution in Test := false,
  scalacOptions ++= commonScalacOptions
) ++ consoleScalacOptions

lazy val versions = new {
  val cats       = "0.9.0"
  val fansi      = "0.2.3"
  val fastparse  = "0.4.2"
  val http4s     = "0.17.0-M2"
  val logback    = "1.2.3"
  val pureconfig = "0.7.0"
  val scalacheck = "1.13.5"
  val scalatest  = "3.0.3"
  val spire      = "0.14.1"
  val squants    = "1.3.0-SNAPSHOT"
}

lazy val commonDependencies = Seq(
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

lazy val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ypartial-unification",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfatal-warnings",
  "-Xfuture",
  "-Xlint"
)

lazy val consoleScalacOptions = Seq(
  scalacOptions in (Compile, console) ~= (_.filterNot("-Xlint" == _)),
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
  initialCommands in console := """
    |import cats.implicits._
    |import xenocosm.geometry.instances._
    |import xenocosm.geometry.syntax._
    |import xenocosm.interop.instances._
    |import xenocosm.interop.syntax._
    |import xenocosm.phonology.instances._
    |import xenocosm.phonology.syntax._
    |import xenocosm.universe.instances._
    |
    |val seed = spire.random.rng.BurtleRot2.randomSeed
    |val gen = spire.random.rng.BurtleRot2.fromSeed(seed)
    |""".stripMargin
)

lazy val scoverageSettings = Seq(
  coverageMinimum := 75,
  coverageFailOnMinimum := true,
  coverageHighlighting := false
)

lazy val buildInfoSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](name, version),
  buildInfoPackage := "xenocosm",
  buildInfoObject := "XenocosmBuild"
)
