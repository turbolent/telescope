package com.turbolent.questionServer

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.logging.Level.INFO
import com.twitter.logging.Logger
import com.twitter.util.Future
import org.apache.jena.query.Query
import org.json4s.{Formats, FullTypeHints}
import org.json4s.jackson.Serialization


class QuestionService(tagger: Tagger) extends Service[Request, Response] {

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

  def apply(req: Request): Future[Response] = {
    val steps: QuestionStep[Unit, Seq[Query]] =
      GetSentenceStep
        .compose(tokenizeSentence)
        .compose(ParseQuestionStep)
        .compose(CompileQuestionStep)
        .compose(CompileQueriesStep)
    val sentence = GetSentenceStep.getSentence(req).getOrElse("")
    steps(req, (), new QuestionResponse).flatMap {
      case (_, response) =>
        log.info("successful: " + sentence)
        respond(req, Status.Ok, response)
    } rescue {
      case QuestionError(status, content) =>
        if (!sentence.isEmpty)
          log.info("failed: " + sentence)
        respond(req, status, content)
    }
  }
}