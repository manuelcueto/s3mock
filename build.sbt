name := "s3mock"

version := "0.2.5"

organization := "io.findify"

scalaVersion := "2.12.4"

crossScalaVersions := Seq("2.11.11", "2.12.4")

val akkaVersion = "2.5.11"

licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))

homepage := Some(url("https://github.com/findify/s3mock"))

resolvers ++= Seq(
  Resolver.bintrayRepo("ovotech", "maven"),
  Resolver.bintrayRepo("slamdata-inc", "maven-public"),
  "segence" at "https://dl.bintray.com/segence/maven-oss-releases/"
)

resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.bintrayRepo("slamdata-inc", "maven-public")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.ovoenergy" %% "algae" % "0.2.1",
  "io.kamon" %% "kamon-jmx-collector" % "0.1.6",
  "com.typesafe.akka" %% "akka-http" % "10.1.0",
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
  "org.scala-lang.modules" %% "scala-xml" % "1.1.0",
  "com.github.pathikrit" %% "better-files" % "3.4.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.294",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "test",
  "org.iq80.leveldb" % "leveldb" % "0.10",
  "org.typelevel" %% "cats-core" % "1.6.1",
  "com.lightbend.akka" %% "akka-stream-alpakka-s3" % "0.17" % "test"
)

parallelExecution in Test := false

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

pomExtra := (<scm>
      <url>git@github.com:findify/s3mock.git</url>
      <connection>scm:git:git@github.com:findify/s3mock.git</connection>
    </scm>
    <developers>
      <developer>
        <id>romangrebennikov</id>
        <name>Roman Grebennikov</name>
        <url>http://www.dfdx.me</url>
      </developer>
    </developers>)

enablePlugins(DockerPlugin)
assemblyJarName in assembly := "s3mock.jar"
mainClass in assembly := Some("io.findify.s3mock.Main")
test in assembly := {}

dockerfile in docker := new Dockerfile {
  from("openjdk:9.0.1-11-jre-slim")
  expose(8001)
  add(assembly.value, "/app/s3mock.jar")
  entryPoint(
    "java",
    "-Xmx128m",
    "-jar",
    "--add-modules",
    "java.xml.bind",
    "/app/s3mock.jar"
  )
}
imageNames in docker := Seq(
  ImageName(s"findify/s3mock:${version.value.replaceAll("\\+", "_")}"),
  ImageName(s"findify/s3mock:latest")
)

/*enablePlugins(JavaAppPackaging)

maintainer in Docker := "S3mock"
packageSummary in Docker := "S3Mock"
packageDescription := "Mock Service For S3"
dockerUpdateLatest := true
dockerExposedPorts := Seq(8001)
 */
