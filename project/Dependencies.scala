import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"

  lazy val deps0 = Seq(
    "org.scala-lang.modules" %% "scala-async"     % "0.9.6",
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
    "org.scala-lang.modules" %% "scala-xml"       % "1.1.1",
    "com.typesafe.akka"      %% "akka-actor"      % "2.5.19",
    "com.typesafe.akka"      %% "akka-slf4j"      % "2.5.19",
    "ch.qos.logback"          % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "org.scalaz"             %% "scalaz-core"     % "7.2.27",
    "org.scalacheck"         %% "scalacheck"      % "1.14.0" % "test",
    scalaTest                                                % "test",
    "org.specs2"             %% "specs2-core"     % "4.3.6"   % "test",
    // JUnit is used for some Java interop. examples. A driver for JUnit:
    "junit" % "junit" % "4.11" % Test,

    "org.junit.jupiter" % "junit-jupiter-api" % "5.3.2" % Test,
    "org.junit.vintage" % "junit-vintage-engine" % "5.3.2" % Test,
    "org.junit.platform" % "junit-platform-runner" % "1.3.2" % Test,

    "com.novocode"      % "junit-interface" % "0.11"   % Test exclude ("junit", "junit-dep")
  )
}

