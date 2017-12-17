package com.turbolent.questionServer

import com.twitter.app.App
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.filter.{ExceptionFilter, LoggingFilter}
import com.twitter.finagle.http.path.{/, Root}
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.logging.{ConsoleHandler, FileHandler, Logger, Logging}
import com.twitter.util.Await
import com.turbolent.spacyThrift.SpacyThriftClient


object QuestionServer extends App with Logging {

  val portFlag =
    flag("port", 8080, "HTTP port")

  val defaultSpacyThriftHostname = "localhost"
  val spacyThriftHostnameFlag =
    flag("spacy-thrift-hostname", defaultSpacyThriftHostname, "spacy-thrift service hostname")

  val defaultSpacyThriftPort = 9090
  val spacyThriftPortFlag =
    flag("spacy-thrift-port", defaultSpacyThriftPort, "spacy-thrift service port")

  val defaultLog = "/dev/stderr"
  val parseLogFlag =
    flag("parse-log", defaultLog, "parsing log")
  val accessLogFlag =
    flag("access-log", defaultLog, "access log")

  def addLogHandler(logger: Logger, path: String) {
    logger.addHandler(path match {
      case `defaultLog` => new ConsoleHandler()
      case _ => FileHandler(path)()
    })
  }

  def getService(tagger: Tagger,
                 parseLog: String = defaultLog,
                 accessLog: String = defaultLog): Service[Request, Response] =
  {
    val parseService = new QuestionService(tagger)

    val routingService =
      RoutingService.byMethodAndPathObject[Request] {
        case (Method.Get, Root / "parse") => parseService
      }

    addLogHandler(parseService.log, parseLog)
    addLogHandler(LoggingFilter.log, accessLog)

    LoggingFilter
        .andThen(ExceptionFilter)
        .andThen(routingService)
  }

  def main() {
    val spacyThriftHostname = spacyThriftHostnameFlag()
    val spacyThriftPort = spacyThriftPortFlag()

    val spacyThriftClient = new SpacyThriftClient(spacyThriftHostname, spacyThriftPort)
    val tagger = new SpacyTagger(spacyThriftClient)

    val service = getService(tagger,
      parseLog = parseLogFlag(),
      accessLog = accessLogFlag())
    val server = Http.serve(":" + portFlag(), service)
    onExit {
      server.close()
    }
    Await.ready(server)
  }

}
