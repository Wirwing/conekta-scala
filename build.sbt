name := "conekta-scala"

version := io.Source.fromFile("VERSION").mkString.trim

organization := "com.github.wirwing"

scalaVersion := "2.10.3"

crossScalaVersions := Seq("2.10.3", "2.11.1")

scalacOptions ++= Seq("-unchecked", "-deprecation")

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "[4.1, 4.2)",
  "com.typesafe.play" %% "play-json" % "2.3.0",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "org.slf4j" % "slf4j-api" % "1.7.1",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.1",
  "ch.qos.logback" % "logback-classic" % "1.0.3",
  "org.scalatest" % "scalatest_2.10" % "2.1.3" % "test",
  "junit" % "junit" % "4.10" % "test"
)

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { x => false }

pomExtra := (
  <url>http://www.conekta.io</url>
  <licenses>
    <license>
      <name>MIT</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:Wirwing/conekta-scala.git</url>
    <connection>scm:git:git@github.com:Wirwing/conekta-scala.git</connection>
  </scm>
  <developers>
    <developer>
      <id>wirwing</id>
      <name>Irving Caro Fierros</name>
      <url>https://github.com/wirwing</url>
    </developer>
  </developers>)