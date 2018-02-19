package com.turbolent.questionServer

import com.turbolent.wikidataOntology.NumberParser
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.logging.Level.INFO
import com.twitter.logging.Logger
import com.twitter.util.Future
import org.apache.jena.query.Query
import org.json4s.{Formats, FullTypeHints}
import org.json4s.jackson.Serialization


class QuestionService(tagger: Tagger, numberParser: NumberParser) extends Service[Request, Response] {

  val log = Logger(classOf[QuestionService])
  log.setUseParentHandlers(false)
  log.setLevel(INFO)

  implicit val formats: Formats = {
    val typeHints = Serialization.formats(FullTypeHints(List(classOf[AnyRef])))
        .withTypeHintFieldName("$type")

    typeHints + new QuerySerializer
  }

  def respond(req: Request, status: Status, content: AnyRef): Future[Response] = {
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

  val tokenizeSentence = new TokenizeSentenceStep(tagger)
  val compileQuestionStep = new CompileQuestionStep(numberParser)

  val steps: QuestionStep[Unit, Seq[Query]] =
    GetSentenceStep
      .compose(tokenizeSentence)
      .compose(ParseQuestionStep)
      .compose(compileQuestionStep)
      .compose(CompileQueriesStep)

  val resultParameter = "result"

  def specifiesResults(req: Request): Boolean =
    req.params.contains(resultParameter)

  def getResults(req: Request): Set[String] =
    req.params.getAll(resultParameter).toSet

  def apply(req: Request): Future[Response] = {
    val sentence = GetSentenceStep.getSentence(req).getOrElse("")
    steps(req, (), new QuestionResponse).flatMap {
      case (_, response) =>
        log.info("successful: " + sentence)

        val filteredResponse = if (specifiesResults(req)) {
          val results = getResults(req)
          response.filter {
            case (name, _) =>
              results.contains(name)
          }
        } else
          response

        respond(req, Status.Ok, filteredResponse)
    } rescue {
      case QuestionError(status, content) =>
        if (!sentence.isEmpty)
          log.info("failed: " + sentence)
        respond(req, status, content)
    }
  }
}