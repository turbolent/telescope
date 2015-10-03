package com.turbolent.questionServer

import java.nio.file.{Path, Paths}

import com.twitter.app.Flaggable
import com.twitter.finagle.Httpx
import com.twitter.finagle.httpx.filter.{LoggingFilter, ExceptionFilter}
import com.twitter.logging.{ConsoleHandler, FileHandler, Logger, Logging}
import com.twitter.app.App
import com.twitter.util.Await
import com.twitter.finagle.httpx.path.{Root, /}
import com.twitter.finagle.httpx.{Request, Method}
import com.twitter.finagle.httpx.service.RoutingService


object QuestionServer extends App with Logging {

  implicit val asPath = Flaggable.mandatory(Paths.get(_))

  val portFlag =
    flag("port", 8080, "HTTP port")
  val taggerModelPathFlag =
    flag("tagger-model", Paths.get("tagger-model"), "POS tagger model")
  val lemmatizerModelPathFlag =
    flag("lemmatizer-model", Paths.get("lemmatizer-model"), "lemmatizer model")
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

  def getService(taggerModelPath: Path, lemmatizerModelPath: Path) = {
    val parseService = new QuestionService(taggerModelPath, lemmatizerModelPath)

    val routingService =
      RoutingService.byMethodAndPathObject[Request] {
        case (Method.Get, Root / "parse") => parseService
      }

    addLogHandler(parseService.log, parseLogFlag())
    addLogHandler(LoggingFilter.log, accessLogFlag())

    LoggingFilter
        .andThen(ExceptionFilter)
        .andThen(routingService)
  }

  def main() {
    val server = Httpx.serve(":" + portFlag(),
      getService(taggerModelPathFlag(), lemmatizerModelPathFlag()))
    onExit {
      server.close()
    }
    Await.ready(server)
  }

}
