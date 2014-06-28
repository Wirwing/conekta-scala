name := "conekta-scala"

version := io.Source.fromFile("VERSION").mkString.trim

organization := "com.conekta"

scalaVersion := "2.10.3"

crossScalaVersions := Seq("2.9.1", "2.9.2")

scalacOptions ++= Seq("-unchecked", "-deprecation")

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "[4.1, 4.2)",
  "com.typesafe.play" % "play-json_2.10" % "2.2.1",
  "org.scalatest" % "scalatest_2.10" % "2.1.3" % "test",
  "junit" % "junit" % "4.10" % "test",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "org.slf4j" % "slf4j-api" % "1.7.1",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.1",
  "ch.qos.logback" % "logback-classic" % "1.0.3"
)

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { x => false }