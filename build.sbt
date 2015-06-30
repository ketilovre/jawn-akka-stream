name := "jawn-akka-stream"

organization := "com.ketilovre"

version := "0.1.0"

scalaVersion := "2.11.7"

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
  "Scalaz Bintray"    at "https://dl.bintray.com/scalaz/releases",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= {
  val jawnV = "0.8.0"
  val akkaStreamV = "1.0-RC4"
  val specsV = "3.6.2"
  Seq(
    // ----- Jawn -----
    "org.spire-math" %% "jawn-ast" % jawnV,
    // ----- Akka -----
    "com.typesafe.akka" %% "akka-stream-experimental"    % akkaStreamV % "provided",
    "com.typesafe.akka" %% "akka-http-experimental"      % akkaStreamV % "provided",
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamV % "provided",
    // ----- Tests -----
    "org.specs2"        %% "specs2-core"                    % specsV      % "test",
    "org.specs2"        %% "specs2-scalacheck"              % specsV      % "test",
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaStreamV % "test"
  )
}
