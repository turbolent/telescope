package com.turbolent.questionServer

import java.nio.file.{Path, Paths}

import com.twitter.finagle.Httpx
import com.twitter.finagle.httpx.filter.ExceptionFilter
import com.twitter.util.Await
import scopt.Read
import com.twitter.finagle.httpx.path.{Root, /}
import com.twitter.finagle.httpx.{Request, Method}
import com.twitter.finagle.httpx.service.RoutingService


object QuestionServer extends App {

  def getService(configuration: Configuration) = {
    val parseService = new ParseService(configuration)

    ExceptionFilter andThen RoutingService.byMethodAndPathObject[Request] {
      case (Method.Get, Root / "parse") => parseService
    }
  }

  def serve(configuration: Configuration) =
    Httpx.serve(":" + configuration.port,
      getService(configuration))

  implicit val pathRead: Read[Path] =
    Read.reads[Path] { Paths.get(_) }

  val argsParser =
    new scopt.OptionParser[Configuration]("question-server") {
      def optPath(name: String, description: String) =
        opt[Path](name) required() text description valueName "<path>"

      opt[Int]('p', "port") text "HTTP Port" valueName "<port>" action {
        (port, config) => config.copy(port = port)
      }

      optPath("lemmatizer-model", "lemmatizer model") action {
        (path, config) => config.copy(lemmatizerModelPath = path)
      }

      optPath("tagger-model", "POS tagger model") action {
        (path, config) => config.copy(taggerModelPath = path)
      }
    }

  for (configuration <- argsParser.parse(args, Configuration()))
    Await.ready(serve(configuration))
}
