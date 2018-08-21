import microsites.ExtraMdFileConfig

inThisBuild(Seq(
  organization in ThisBuild := "com.robotsnowfall",
  scalaVersion := "2.12.6"
))

lazy val versions = new {
  val cats       = "1.2.0"
  val circe      = "0.9.3"
  val cryptobits = "1.2"
  val discipline = "0.10.0"
  val fastparse  = "1.0.0"
  val http4s     = "0.18.16"
  val logback    = "1.2.3"
  val monocle    = "1.5.1-cats"
  val pureconfig = "0.9.1"
  val scalacheck = "1.14.0"
  val scalatest  = "3.0.5"
  val spire      = "0.16.0"
  val squants    = "1.3.0"
}

lazy val commonDependencies = Seq(
  "org.typelevel"              %% "cats-core"     % versions.cats,
  "org.typelevel"              %% "spire"         % versions.spire,
  "org.typelevel"              %% "squants"       % versions.squants,
  "com.github.julien-truffaut" %% "monocle-core"  % versions.monocle,
  "com.github.julien-truffaut" %% "monocle-macro" % versions.monocle,

  "org.typelevel"              %% "cats-laws"   % versions.cats       % Test,
  "org.typelevel"              %% "spire-laws"  % versions.spire      % Test,
  "com.github.julien-truffaut" %% "monocle-law" % versions.monocle    % Test,
  "org.scalacheck"             %% "scalacheck"  % versions.scalacheck % Test,
  "org.scalatest"              %% "scalatest"   % versions.scalatest  % Test
)

lazy val core = project.in(file("core"))
  .enablePlugins(BuildInfoPlugin, GitVersioning)
  .dependsOn(testkit % Test)
  .settings(moduleName := "xenocosm-core")
  .settings(xenocosmSettings ++ gitSettings ++ buildInfoSettings)
  .settings(libraryDependencies ++= Seq(
    "com.lihaoyi" %% "fastparse" % versions.fastparse
  ))

lazy val json = project.in(file("json"))
  .dependsOn(core, testkit % Test)
  .settings(moduleName := "xenocosm-json")
  .settings(xenocosmSettings)
  .settings(libraryDependencies ++= Seq(
    "io.circe" %% "circe-core"    % versions.circe,
    "io.circe" %% "circe-generic" % versions.circe,
    "io.circe" %% "circe-parser"  % versions.circe
  ))

lazy val http = project.in(file("http"))
  .enablePlugins(JavaServerAppPackaging, DockerPlugin)
  .dependsOn(core, json, testkit % Test)
  .settings(moduleName := "xenocosm-http")
  .settings(xenocosmSettings ++ dockerSettings)
  .settings(Seq(
    packageName in Docker := "xenocosm-http",
    mainClass in Compile := Some("xenocosm.http.Main")
  ))
  .settings(libraryDependencies ++= Seq(
    "org.http4s"            %% "http4s-dsl"          % versions.http4s,
    "org.http4s"            %% "http4s-blaze-server" % versions.http4s,
    "org.http4s"            %% "http4s-blaze-client" % versions.http4s,
    "org.http4s"            %% "http4s-circe"        % versions.http4s,
    "org.reactormonk"       %% "cryptobits"          % versions.cryptobits,
    "com.github.pureconfig" %% "pureconfig"          % versions.pureconfig,
    "com.github.pureconfig" %% "pureconfig-http4s"   % versions.pureconfig,
    "com.github.pureconfig" %% "pureconfig-squants"  % versions.pureconfig,
    "ch.qos.logback"         % "logback-classic"     % versions.logback,

    "org.http4s"            %% "http4s-circe"        % versions.http4s     % Test
  ))

lazy val docs = project.in(file("docs"))
  .enablePlugins(MicrositesPlugin, ScalaUnidocPlugin, TutPlugin)
  .dependsOn(core, json, http)
  .settings(moduleName := "xenocosm-docs")
  .settings(xenocosmSettings ++ micrositeSettings ++ unidocSettings)

lazy val testkit = project.in(file("testkit"))
  .settings(moduleName := "xenocosm-testkit")
  .settings(xenocosmSettings)
  .settings(Seq(
    libraryDependencies ++= Seq(
      "org.typelevel"  %% "discipline" % versions.discipline,
      "org.scalacheck" %% "scalacheck" % versions.scalacheck,
      "org.scalatest"  %% "scalatest"  % versions.scalatest
    )
  ))

lazy val commonSettings = Seq(
  libraryDependencies ++= commonDependencies,
  parallelExecution in Test := false,
  scalacOptions ++= commonScalacOptions
)

lazy val xenocosmSettings = commonSettings ++ warnUnusedImport ++ scoverageSettings

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

lazy val scoverageSettings = Seq(
  coverageMinimum := 80,
  coverageFailOnMinimum := true,
  coverageHighlighting := true
)

lazy val gitSettings = Seq(
  git.useGitDescribe := true,
  git.baseVersion := "0.0"
)

lazy val micrositeSettings = Seq(
  micrositeName := "Xenocosm",
  micrositeDescription := "procedural space trader",
  micrositeBaseUrl := "/xenocosm",
  micrositeTwitterCreator := "@robotsnowfall",
  micrositeGithubOwner := "robotsnowfall",
  micrositeGithubRepo := "xenocosm",
  micrositeGithubLinks := true,
  micrositeGitterChannel := false,
  micrositePalette := Map(
    "brand-primary"   -> "#0B1A25",
    "brand-secondary" -> "#1A475C",
    "brand-tertiary"  -> "#4B788F",
    "gray-dark"       -> "#49494B",
    "gray"            -> "#7B7B7E",
    "gray-light"      -> "#E3E2E3",
    "gray-lighter"    -> "#F4F3F4",
    "white-color"     -> "#FFFFFF"),
  micrositeExtraMdFiles := Map(
    file("README.md") -> ExtraMdFileConfig("docs/index.md", "docs", Map.empty[String, String])
  ),
  micrositeDocumentationUrl := "/xenocosm/docs/index.html"
)

lazy val docsMappingsAPIDir = settingKey[String]("Name of subdirectory in site target directory for api docs")

lazy val unidocSettings = Seq(
  autoAPIMappings := true,
  docsMappingsAPIDir := "api",
  addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), docsMappingsAPIDir),
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject -- inProjects(testkit),
  ghpagesNoJekyll := false,
  fork in tut := true,
  fork in (ScalaUnidoc, unidoc) := true,
  scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
    "-Xfatal-warnings",
    "-groups",
    "-doc-source-url", "https://github.com/robotsnowfall/xenocosm/tree/master€{FILE_PATH}.scala",
    "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath,
    "-doc-root-content", (resourceDirectory.in(Compile).value / "rootdoc.txt").getAbsolutePath
  ),
  scalacOptions in Tut ~= (_.filterNot(Set("-Ywarn-unused-import", "-Ywarn-dead-code"))),
  includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.yml" | "*.md" | "*.svg",
  includeFilter in Jekyll := (includeFilter in makeSite).value
)

lazy val buildInfoSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](name, version),
  buildInfoPackage := "xenocosm"
)

lazy val dockerSettings = Seq(
  dockerBaseImage := "openjdk:8-jre-alpine",
  dockerExposedPorts := Seq(8080),
  dockerLabels := Map(
    "maintainer" -> "doug.hurst@protonmail.com"
  ),
  dockerRepository := Some("index.docker.io"),
  dockerUsername := Some("robotsnowfall"),
  dockerUpdateLatest := true
)

addCommandAlias("validateCore", ";coverage;core/compile;core/test;core/coverageReport")
addCommandAlias("validateJson", ";coverage;json/compile;json/test;json/coverageReport")
addCommandAlias("validateHttp", ";coverage;http/compile;http/test;http/coverageReport")
addCommandAlias("validate", ";clean;scalastyle;validateCore;validateJson;validateHttp;coverageAggregate")
