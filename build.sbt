import NativePackagerHelper._
import microsites.ExtraMdFileConfig

organization in ThisBuild := "com.robotsnowfall"
scalaVersion in ThisBuild := "2.12.5"

lazy val versions = new {
  val cats       = "1.1.0"
  val circe      = "0.9.2"
  val economancy = "0.0-20180320T024007"
  val galaxique  = "0.0-245ab536ce204516d2764a3a555241094b9c096c"
  val http4s     = "0.18.3"
  val logback    = "1.2.3"
  val pseudoglot = "0.0-88bd905302831628441b54fb5efbe9850d917af6"
  val pureconfig = "0.9.0"
  val scalacheck = "1.13.5"
  val scalatest  = "3.0.5"
  val spire      = "0.15.0"
}

lazy val commonDependencies = Seq(
  "org.typelevel"         %% "cats-core"           % versions.cats,
  "io.circe"              %% "circe-core"          % versions.circe,
  "io.circe"              %% "circe-generic"       % versions.circe,
  "io.circe"              %% "circe-parser"        % versions.circe,
  "org.http4s"            %% "http4s-dsl"          % versions.http4s,
  "org.http4s"            %% "http4s-blaze-server" % versions.http4s,
  "org.http4s"            %% "http4s-blaze-client" % versions.http4s,
  "com.github.pureconfig" %% "pureconfig"          % versions.pureconfig,
  "com.github.pureconfig" %% "pureconfig-squants"  % versions.pureconfig,
  "ch.qos.logback"         % "logback-classic"     % versions.logback,
  "com.robotsnowfall"     %% "economancy-core"     % versions.economancy,
  "com.robotsnowfall"     %% "economancy-json"     % versions.economancy,
  "com.robotsnowfall"     %% "galaxique-core"      % versions.galaxique,
  "com.robotsnowfall"     %% "galaxique-json"      % versions.galaxique,
  "com.robotsnowfall"     %% "pseudoglot-core"     % versions.pseudoglot,
  "com.robotsnowfall"     %% "pseudoglot-json"     % versions.pseudoglot,

  "org.typelevel"         %% "cats-laws"           % versions.cats       % Test,
  "org.typelevel"         %% "spire-laws"          % versions.spire      % Test,
  "org.scalacheck"        %% "scalacheck"          % versions.scalacheck % Test,
  "org.scalatest"         %% "scalatest"           % versions.scalatest  % Test
)

lazy val xenocosm = project.in(file("."))
  .enablePlugins(GitVersioning, BuildInfoPlugin)
  .enablePlugins(JavaAppPackaging)
  .settings(name := "xenocosm")
  .settings(xenocosmSettings)

lazy val commonSettings = Seq(
  libraryDependencies ++= commonDependencies,
  parallelExecution in Test := false,
  scalacOptions ++= commonScalacOptions
) ++ consoleScalacOptions

lazy val xenocosmSettings = commonSettings ++ warnUnusedImport ++ scoverageSettings ++ gitSettings ++ buildInfoSettings

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
  "-Xlint:-unused,_"
)

lazy val warnUnusedImport = Seq(
  scalacOptions ++= Seq("-Ywarn-unused-import"),
  scalacOptions in (Compile, console) ~= {_.filterNot("-Ywarn-unused-import" == _)},
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value
)

lazy val consoleScalacOptions = Seq(
  initialCommands in console := """
    |import cats.implicits._
    |import io.circe.syntax._
    |import spire.random.rng.BurtleRot2
    |
    |val seed = BurtleRot2.randomSeed
    |val gen = BurtleRot2.fromSeed(seed)
    |""".stripMargin
)

lazy val scoverageSettings = Seq(
  coverageMinimum := 80,
  coverageFailOnMinimum := true,
  coverageHighlighting := true
)

lazy val gitSettings = Seq(
  git.useGitDescribe := true,
  git.baseVersion := "0.0.0",
  coverageHighlighting := true
)

lazy val micrositeSettings = Seq(
  micrositeName := "Xenocosm",
  micrositeDescription := "procedural space trader",
  micrositeBaseUrl := "/xenocosm",
  micrositeTwitterCreator := "@robotsnowfall",
  micrositeGithubOwner := "robotsnowfall",
  micrositeGithubRepo := "xenocosm",
  micrositeGithubLinks := true,
  micrositePalette := Map(
    "brand-primary" ->   "#7EBB16",
    "brand-secondary" -> "#475A2B",
    "brand-tertiary" ->  "#475A2B",
    "gray-dark" ->       "#49494B",
    "gray" ->            "#7B7B7E",
    "gray-light" ->      "#E9F9CE",
    "gray-lighter" ->    "#F4F3F4",
    "white-color" ->     "#FFFFFF"),
  micrositeExtraMdFiles := Map(
    file("README.md") -> ExtraMdFileConfig(
      "index.md",
      "home",
      Map("title" -> "Home", "section" -> "home", "position" -> "0")
    )
  ),
  micrositeDocumentationUrl := "/xenocosm/api/xenocosm/index.html",
  micrositePushSiteWith := GitHub4s,
  micrositeGithubToken := Option(System.getenv().get("GITHUB_TOKEN"))
)

lazy val docsMappingsAPIDir = settingKey[String]("Name of subdirectory in site target directory for api docs")

lazy val unidocSettings = Seq(
  autoAPIMappings := true,
  docsMappingsAPIDir := "api",
  fork in (ScalaUnidoc, unidoc) := true,
  addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), docsMappingsAPIDir),
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject,
  fork in (ScalaUnidoc, unidoc) := true,
  scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
    "-Xfatal-warnings",
    "-groups",
    "-doc-source-url", "https://github.com/robotsnowfall/xenocosm/tree/master€{FILE_PATH}.scala",
    "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath
  )
)

lazy val buildInfoSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](name, version),
  buildInfoPackage := "xenocosm",
  buildInfoObject := "XenocosmBuild"
)

lazy val distSettings = Seq(
  topLevelDirectory := None,
  packageName in Universal := "xenocosm"
)

addCommandAlias("validate", ";clean;scalastyle;coverage;compile;test;coverageReport")
