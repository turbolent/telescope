package com.turbolent.questionServer

import com.twitter.app.App
import com.twitter.finagle.Http
import com.twitter.finagle.http.filter.{ExceptionFilter, LoggingFilter}
import com.twitter.finagle.http.path.{/, Root}
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.{Method, Request}
import com.twitter.logging.{ConsoleHandler, FileHandler, Logger, Logging}
import com.twitter.util.Await
import spacyThrift.client.SpacyThriftClient


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

  def getService(spacyThriftHostname: String = defaultSpacyThriftHostname,
                 spacyThriftPort: Int = defaultSpacyThriftPort,
                 parseLog: String = defaultLog,
                 accessLog: String = defaultLog) =
  {
    val spacyThriftClient = new SpacyThriftClient(spacyThriftHostname, spacyThriftPort)
    val parseService = new QuestionService(spacyThriftClient)

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
    val service = getService(spacyThriftHostname = spacyThriftHostnameFlag(),
      spacyThriftPort = spacyThriftPortFlag(),
      parseLog = parseLogFlag(),
      accessLog = accessLogFlag())
    val server = Http.serve(":" + portFlag(), service)
    onExit {
      server.close()
    }
    Await.ready(server)
  }

}
