organization := "com.typesafe.akka.samples"
name := "MM-akka-backend"

scalaVersion := "2.12.2"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.16"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.3",
  "com.typesafe.akka" %% "akka-stream" % "2.5.3",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.3" % Test
)



licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))

