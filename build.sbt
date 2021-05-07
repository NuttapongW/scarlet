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
    resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases",
    assembly / mainClass := Some("Boot"),
    assembly / assemblyJarName := "crawly.jar",
    assembly / assemblyMergeStrategy := { _: String => MergeStrategy.last }
  )
  .settings(
    libraryDependencies ++= commonDependencies ++ Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % Versions.Akka,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % Versions.Akka % Test,
      "com.typesafe.akka" %% "akka-stream" % Versions.Akka,
      "com.typesafe.akka" %% "akka-http" % Versions.AkkaHttp
    )
  )
