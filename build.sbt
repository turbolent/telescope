organization := "com.turbolent"
name := "question-parser"
version := "1.0-SNAPSHOT"

scalaVersion := "2.12.0"

scalacOptions ++= Seq("-feature", "-Xfatal-warnings")

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  // TODO:
  // "ai.x" % "diff" % "1.2.0" % "test"
)

publishMavenStyle := true

publishTo := {
  val repositoryPath = System.getProperty("repositoryPath")
  if (repositoryPath == null) None
  else Some("internal.repo" at file(repositoryPath).toURI.toURL.toString)
}
