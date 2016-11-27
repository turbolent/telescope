organization := "com.turbolent"
name := "question-compiler"
version := "1.1-SNAPSHOT"

scalaVersion := "2.12.0"

scalacOptions ++= Seq("-feature", "-Xfatal-warnings")

resolvers += "turbolent" at "https://raw.githubusercontent.com/turbolent/mvn-repo/master/"

libraryDependencies ++= Seq(
  "com.turbolent" %% "question-parser" % "1.0-SNAPSHOT",
  "org.apache.jena" % "jena-arq" % "3.1.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

publishMavenStyle := true

publishTo := {
  val repositoryPath = System.getProperty("repositoryPath")
  if (repositoryPath == null) None
  else Some("internal.repo" at file(repositoryPath).toURI.toURL.toString)
}

