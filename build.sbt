ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

import Dependencies._

testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a"))

lazy val root = (project in file("."))
  .settings(
    name := "Prog-Scala",
    crossPaths := false,
    fork in Test := true,
    parallelExecution := true,
    parallelExecution in Test := false,
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= deps0,
    javacOptions  ++= Seq(
      "-Xlint:unchecked", "-Xlint:deprecation", "-Xdiags:verbose"),
    scalacOptions ++= Seq(
      "-encoding", "UTF-8", 
      "-deprecation", "-unchecked", "-feature", "-Xlint",
      "-Ywarn-infer-any", 
      "-language:experimental.macros")
  )

// Uncomment the following for publishing to Sonatype.
// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for more detail.

// ThisBuild / description := "Some descripiton about your project."
// ThisBuild / licenses    := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
// ThisBuild / homepage    := Some(url("https://github.com/example/project"))
// ThisBuild / scmInfo := Some(
//   ScmInfo(
//     url("https://github.com/your-account/your-project"),
//     "scm:git@github.com:your-account/your-project.git"
//   )
// )
// ThisBuild / developers := List(
//   Developer(
//     id    = "Your identifier",
//     name  = "Your Name",
//     email = "your@email",
//     url   = url("http://your.url")
//   )
// )
// ThisBuild / pomIncludeRepository := { _ => false }
// ThisBuild / publishTo := {
//   val nexus = "https://oss.sonatype.org/"
//   if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
//   else Some("releases" at nexus + "service/local/staging/deploy/maven2")
// }
// ThisBuild / publishMavenStyle := true
