package com.turbolent.questionServer

import java.net.InetSocketAddress

import com.samstarling.prometheusfinagle.metrics.{MetricsService, Telemetry}
import com.turbolent.spacyThrift.SpacyThriftClient
import com.turbolent.wikidataOntology.NumberParser
import com.twitter.app.App
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.filter.{ExceptionFilter, LoggingFilter}
import com.twitter.finagle.http.path.{/, Root}
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.finagle.loadbalancer.perHostStats
import com.twitter.finagle.{Http, Service}
import com.twitter.logging.{ConsoleHandler, FileHandler, Logger, Logging}
import io.prometheus.client.CollectorRegistry


object QuestionServer extends App with Logging {

  perHostStats.parse("true")

  val portFlag =
    flag("port", 8080, "HTTP port")

  val defaultSpacyThriftHostname = "localhost"
  val spacyThriftHostnameFlag =
    flag("spacy-thrift-hostname", defaultSpacyThriftHostname, "spacy-thrift service hostname")

  val defaultSpacyThriftPort = 9090
  val spacyThriftPortFlag =
    flag("spacy-thrift-port", defaultSpacyThriftPort, "spacy-thrift service port")

  val defaultDucklingHostname = "localhost"
  val ducklingHostnameFlag =
    flag("duckling-hostname", defaultDucklingHostname, "Duckling service hostname")

  val defaultDucklingPort = 8000
  val ducklingPortFlag =
    flag("duckling-port", defaultDucklingPort, "Duckling service port")

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
                 numberParser: NumberParser,
                 parseLog: String = defaultLog,
                 accessLog: String = defaultLog): Service[Request, Response] =
  {
    val registry = CollectorRegistry.defaultRegistry
    val telemetry = new Telemetry(registry, "telescope")
    val parseService = new QuestionService(tagger, numberParser, telemetry)
    val metricsService = new MetricsService(registry)

    val routingService =
      RoutingService.byMethodAndPathObject[Request] {
        case (Method.Get, Root / "parse") => parseService
        case (Method.Get, Root / "metrics") => metricsService
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
    val ducklingHostname = ducklingHostnameFlag()
    val ducklingPort = ducklingPortFlag()
    val parseFlag = parseLogFlag()
    val accessLog = accessLogFlag()
    val port = portFlag()

    val spacyThriftClient = new SpacyThriftClient(spacyThriftHostname, spacyThriftPort)
    val ducklingClient = new DucklingClient(ducklingHostname, ducklingPort)
    val tagger = new SpacyTagger(spacyThriftClient)
    val numberParser = new DucklingNumberParser(ducklingClient)
    val service = getService(tagger, numberParser,
      parseLog = parseFlag,
      accessLog = accessLog)

    ServerBuilder()
      .stack(Http.server.withHttpStats)
      .name("question-server")
      .bindTo(new InetSocketAddress(port))
      .build(service)
  }
}
