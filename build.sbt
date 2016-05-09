organization := "com.turbolent"
name := "question-parser"
version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-feature", "-Xfatal-warnings")

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

publishMavenStyle := true

publishTo := {
  val repositoryPath = System.getProperty("repositoryPath")
  if (repositoryPath == null) None
  else Some("internal.repo" at file(repositoryPath).toURI.toURL.toString)
}