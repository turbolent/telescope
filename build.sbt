organization := "com.turbolent"
name := "wikidata-ontology"
version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-feature", "-Xfatal-warnings")

resolvers += "turbolent" at "https://raw.githubusercontent.com/turbolent/mvn-repo/master/"

libraryDependencies ++= Seq(
  "com.turbolent" %% "question-parser" % "1.0-SNAPSHOT",
  "com.turbolent" %% "question-compiler" % "1.0-SNAPSHOT",
  "com.turbolent" % "number-parser" % "0.1-SNAPSHOT",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

publishArtifact in (Test, packageBin) := true

publishMavenStyle := true

publishTo := {
  val repositoryPath = System.getProperty("repositoryPath")
  if (repositoryPath == null) None
  else Some("internal.repo" at file(repositoryPath).toURI.toURL.toString)
}

