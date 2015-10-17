package com.turbolent.questionServer

import java.nio.file.{Path, Paths}

import com.twitter.finagle.Httpx
import com.twitter.finagle.httpx.filter.{LoggingFilter, ExceptionFilter}
import com.twitter.logging.{ConsoleHandler, FileHandler, Logging, Logger}
import com.twitter.app.{Flaggable, App}
import com.twitter.util.Await
import com.twitter.finagle.httpx.path.{Root, /}
import com.twitter.finagle.httpx.{Request, Method}
import com.twitter.finagle.httpx.service.RoutingService


object QuestionServer extends App with Logging {

  implicit val asPath = Flaggable.mandatory(Paths.get(_))

  val portFlag =
    flag("port", 8080, "HTTP port")

  val defaultTaggerModelPath = Paths.get("tagger-model")
  val taggerModelPathFlag =
    flag("tagger-model", defaultTaggerModelPath, "POS tagger model")

  val defaultLemmatizerModelPath = Paths.get("lemmatizer-model")
  val lemmatizerModelPathFlag =
    flag("lemmatizer-model", defaultLemmatizerModelPath, "lemmatizer model")

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

  def getService(taggerModelPath: Path = defaultTaggerModelPath,
                 lemmatizerModelPath: Path = defaultLemmatizerModelPath,
                 parseLog: String = defaultLog,
                 accessLog: String = defaultLog) =
  {
    val parseService = new QuestionService(taggerModelPath, lemmatizerModelPath)

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
    val service = getService(taggerModelPath = taggerModelPathFlag(),
      lemmatizerModelPath = lemmatizerModelPathFlag(),
      parseLog = parseLogFlag(),
      accessLog = accessLogFlag())
    val server = Httpx.serve(":" + portFlag(), service)
    onExit {
      server.close()
    }
    Await.ready(server)
  }

}
