import Common._

ThisBuild / organization := "org.lyghtning"
ThisBuild / scalaVersion := Versions.Scala
ThisBuild / idePackagePrefix := Some("org.lyghtning")

lazy val commonDependencies = Seq(
  "org.scalatest" %% "scalatest" % Versions.ScalaTest % "test"
)

lazy val crawly = (project in file("./crawly"))
  .settings(
    name := "scarlet",
    version := "0.1",
    assembly / mainClass := Some("Main"),
    assembly / assemblyJarName := "crawly.jar",
    assembly / assemblyMergeStrategy := { _: String => MergeStrategy.last }
  )
  .settings(
    libraryDependencies ++= commonDependencies ++ Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % Versions.Akka,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % Versions.Akka % Test,
      "com.typesafe.akka" %% "akka-stream" % Versions.Akka,
      "com.typesafe.akka" %% "akka-http" % Versions.AkkaHttp,
      "io.spray" %%  "spray-json" % Versions.SprayJson,
      "org.jsoup" % "jsoup" % Versions.Jsoup
    )
  )
