organization := "com.turbolent"
name := "question-parser"
version := "1.0-SNAPSHOT"

scalaVersion := "2.12.3"

scalacOptions ++= Seq(
  "-Xlint",
  "-feature",
  "-Xfatal-warnings",
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused"
)

resolvers += "turbolent" at "https://raw.githubusercontent.com/turbolent/mvn-repo/master/"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.turbolent" %% "xdotai-diff" % "1.3.0" % "test"
)

publishMavenStyle := true

publishTo := {
  val repositoryPath = System.getProperty("repositoryPath")
  if (repositoryPath == null) None
  else Some("internal.repo" at file(repositoryPath).toURI.toURL.toString)
}
