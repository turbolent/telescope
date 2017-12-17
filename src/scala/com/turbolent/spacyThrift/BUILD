
# "java" library to generate Scala
java_thrift_library(
    name="spacy-thrift",
    # default compiler does not gen Scala; Scrooge does
    compiler='scrooge', 
    language='scala',
    rpc_style='finagle',
    dependencies=[
        '3rdparty/jvm:scrooge-core',
        '3rdparty/jvm:finagle-thrift',
    ],
    sources=globs('*.thrift'),
)

scala_library(
    name="spacy-thrift-scala",
    dependencies=[
        ':spacy-thrift',
    ],
    sources=globs('*.scala'),
)
