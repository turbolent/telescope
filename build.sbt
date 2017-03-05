organization := "com.turbolent"
name := "question-parser"
version := "1.0-SNAPSHOT"

scalaVersion := "2.12.1"

scalacOptions ++= Seq("-feature", "-Xfatal-warnings")

resolvers += "turbolent" at "https://raw.githubusercontent.com/turbolent/mvn-repo/master/"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.5",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.turbolent" %% "xdotai-diff" % "1.3.0" % "test"
)

publishMavenStyle := true

publishTo := {
  val repositoryPath = System.getProperty("repositoryPath")
  if (repositoryPath == null) None
  else Some("internal.repo" at file(repositoryPath).toURI.toURL.toString)
}
