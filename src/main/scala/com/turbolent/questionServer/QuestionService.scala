package com.turbolent.questionServer

import java.nio.file.Path

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.logging.Level.INFO
import com.twitter.logging.Logger
import com.twitter.util.Future
import org.json4s.FullTypeHints
import org.json4s.native.Serialization
import spacyThrift.client.SpacyThriftClient


class QuestionService(spacyThriftClient: SpacyThriftClient)
    extends Service[Request, Response]
{
  val log = Logger(classOf[QuestionService])
  log.setUseParentHandlers(false)
  log.setLevel(INFO)

  implicit val formats = {
    val typeHints = Serialization.formats(FullTypeHints(List(classOf[AnyRef])))
        .withTypeHintFieldName("$type")

    typeHints + new QuerySerializer
  }

  def respond(req: Request, status: Status, content: AnyRef) = {
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

  val tokenizeSentence = new TokenizeSentenceStep(spacyThriftClient)

  def apply(req: Request): Future[Response] = {
    val steps = GetSentenceStep
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