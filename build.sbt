scalaVersion := "2.13.1"

val catsVersion     = "2.1.0"
val http4sVersion   = "0.21.0-M6"
val specs2Version   = "4.8.3"
val logbackVersion  = "1.2.3"
val circeVersion    = "0.12.3"
val wiremockVersion = "2.25.1"

lazy val root = (project in file("."))
  .settings(
    organization := "com.minosiatns",
    name := "youtube-extra",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      "org.typelevel"          %% "cats-core"                  % catsVersion,
      "org.http4s"             %% "http4s-blaze-client"        % http4sVersion,
      "org.http4s"             %% "http4s-circe"               % http4sVersion,
      "org.http4s"             %% "http4s-dsl"                 % http4sVersion,
      "io.circe"               %% "circe-core"                 % circeVersion,
      "io.circe"               %% "circe-generic"              % circeVersion,
      "io.circe"               %% "circe-parser"               % circeVersion,
      "org.specs2"             %% "specs2-core"                % specs2Version % "test",
      "com.codecommit"         %% "cats-effect-testing-specs2" % "0.3.0",
      "com.github.tomakehurst" % "wiremock-jre8"               % wiremockVersion % "test",
      "ch.qos.logback"         % "logback-classic"             % logbackVersion
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0")
  )
