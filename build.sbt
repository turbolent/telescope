organization := "com.turbolent"
name := "question-server"
version := "1.0-SNAPSHOT"

scalaVersion := "2.12.1"

scalacOptions ++= Seq("-feature", "-Xfatal-warnings")

resolvers += "turbolent" at "https://raw.githubusercontent.com/turbolent/mvn-repo/master/"

libraryDependencies ++= Seq(
  "com.turbolent" %% "question-parser" % "1.0-SNAPSHOT",
  "com.turbolent" %% "question-compiler" % "1.1-SNAPSHOT",
  "com.turbolent" %% "wikidata-ontology" % "1.1-SNAPSHOT",
  "com.turbolent" %% "spacy-thrift-scala" % "0.1-SNAPSHOT",
  "org.json4s" %% "json4s-native" % "3.3.0",
  "com.twitter" %% "finagle-http" % "6.42.0",
  "com.twitter" %% "twitter-server" % "1.27.0",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

publishMavenStyle := true

publishTo := {
  val repositoryPath = System.getProperty("repositoryPath")
  if (repositoryPath == null) None
  else Some("internal.repo" at file(repositoryPath).toURI.toURL.toString)
}

