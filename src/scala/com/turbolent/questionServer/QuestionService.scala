package com.turbolent.questionServer

import com.samstarling.prometheusfinagle.metrics.Telemetry
import com.turbolent.wikidataOntology.NumberParser
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.logging.Level.INFO
import com.twitter.logging.Logger
import com.twitter.util.Future
import org.apache.jena.query.Query
import org.json4s.{Formats, FullTypeHints}
import org.json4s.jackson.Serialization


class QuestionService(tagger: Tagger,
                      numberParser: NumberParser,
                      telemetry: Telemetry)
  extends Service[Request, Response] {

  val log = Logger(classOf[QuestionService])
  log.setUseParentHandlers(false)
  log.setLevel(INFO)

  private val parsesCounter =
    telemetry.counter("parses_total", "Number of parses", Seq("type"))

  private implicit val formats: Formats = {
    val typeHints = Serialization.formats(FullTypeHints(List(classOf[AnyRef])))
        .withTypeHintFieldName("$type")

    typeHints + new QuerySerializer
  }

  private def respond(req: Request, status: Status, content: AnyRef): Future[Response] = {
    val pretty = req.getBooleanParam("pretty")
    val response = Response(req.version, status)
    response.setContentTypeJson()
    response.contentString =
        if (pretty)
          Serialization.writePretty(content)
        else
          Serialization.write(content)
    Future.value(response)
  }

  private val tokenizeSentence = new TokenizeSentenceStep(tagger)
  private val compileQuestionStep = new CompileQuestionStep(numberParser)

  private val steps: QuestionStep[Unit, Seq[Query]] =
    GetSentenceStep
      .compose(tokenizeSentence)
      .compose(ParseQuestionStep)
      .compose(compileQuestionStep)
      .compose(CompileQueriesStep)

  private val resultParameter = "result"

  private def specifiesResults(req: Request): Boolean =
    req.params.contains(resultParameter)

  private def getResults(req: Request): Set[String] =
    (req.params.getAll(resultParameter) ++ Iterable[String]("error")).toSet

  private def filterResponse(req: Request, response: QuestionResponse): QuestionResponse =
    if (specifiesResults(req)) {
      val results = getResults(req)
      response.filter {
        case (name, _) =>
          results.contains(name)
      }
    } else
      response

  def apply(req: Request): Future[Response] = {
    val sentence = GetSentenceStep.getSentence(req).getOrElse("")
    steps(req, (), new QuestionResponse).flatMap {
      case (_, response) =>
        parsesCounter.labels("success").inc()
        log.info("successful: " + sentence)
        val filteredResponse = filterResponse(req, response)
        respond(req, Status.Ok, filteredResponse)
    } rescue {
      case QuestionError(status, response) =>
        parsesCounter.labels("failure").inc()
        if (!sentence.isEmpty)
          log.info("failed: " + sentence)
        val filteredResponse = filterResponse(req, response)
        respond(req, status, filteredResponse)
    }
  }
}