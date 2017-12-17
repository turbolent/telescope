scala_library(name = 'question-server',
  dependencies = [
    "src/scala/com/turbolent/questionParser:question-parser",
    "src/scala/com/turbolent/questionCompiler:question-compiler",
    "src/scala/com/turbolent/wikidataOntology:wikidata-ontology",
    "3rdparty/jvm:json4s-jackson",
    "3rdparty/jvm:spacy-thrift-scala",
    "3rdparty/jvm:finagle-http",
    "3rdparty/jvm:twitter-server",
  ],
  sources=rglobs('*.scala'),
)
