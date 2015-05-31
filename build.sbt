name := "jawn-akka-stream"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Ydead-code",
  "-Ywarn-numeric-widen",
  "-Xfatal-warnings",
  "-encoding", "UTF-8"
)

scalacOptions in Test ++= Seq(
  "-Yrangepos"
)

resolvers ++= Seq(
  "Scalaz Bintray" at "https://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Seq(
  "org.spire-math" %% "jawn-ast" % "0.8.0",
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-RC3" % "provided",
  "com.typesafe.akka" % "akka-http-experimental_2.11" % "1.0-RC3",
  "com.typesafe.akka" % "akka-http-core-experimental_2.11" % "1.0-RC3",
  "com.typesafe.akka" % "akka-http-testkit-experimental_2.11" % "1.0-RC3" % "test",
  "org.specs2" %% "specs2-core" % "3.6" % "test",
  "org.specs2" %% "specs2-scalacheck" % "3.6" % "test",
  "com.typesafe.akka" % "akka-stream-testkit-experimental_2.11" % "1.0-RC3"
)

compile <<= (compile in Compile) dependsOn scapegoat
