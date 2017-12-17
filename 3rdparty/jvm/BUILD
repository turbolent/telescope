jar_library(
  name='scala-parser-combinators',
  jars=[
    scala_jar(org='org.scala-lang.modules', name='scala-parser-combinators', rev="1.0.6"),
  ],
)

jar_library(
  name='scalatest',
  jars=[
    scala_jar(org='org.scalatest', name='scalatest', rev='3.0.4'),
  ],
)

jar_library(
  name='junit',
  jars=[
    jar(org='junit', name='junit', rev='4.12'),
  ],
  # junit is frequently used only for its annotations.
  scope='forced',
)

jar_library(
  name='jena',
  jars=[
    jar(org='org.apache.jena', name='jena-arq', rev='3.5.0'),
  ],
)
