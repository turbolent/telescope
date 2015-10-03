package com.turbolent.questionServer

import java.nio.file.Path

import com.turbolent.aptagger.Tagger
import com.turbolent.lemmatizer.Lemmatizer
import com.turbolent.questionParser.Token
import com.twitter.finagle.Service
import com.twitter.finagle.httpx.{Request, Response, Status}
import com.twitter.logging.Level.INFO
import com.twitter.logging.Logger
import com.twitter.util.Future
import org.json4s.native.Serialization
import org.json4s.{FieldSerializer, FullTypeHints}


class QuestionService(taggerModelPath: Path, lemmatizerModelPath: Path)
    extends Service[Request, Response]
{
  val log = Logger(classOf[QuestionService])
  log.setUseParentHandlers(false)
  log.setLevel(INFO)

  implicit val formats = {
    val typeHints = Serialization.formats(FullTypeHints(List(classOf[AnyRef])))
        .withTypeHintFieldName("$type")

    val tokenSerializer =
      FieldSerializer[Token](FieldSerializer.ignore("lemmatizer") orElse
                             FieldSerializer.ignore("pos"))

    typeHints + tokenSerializer
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

  implicit val tagger = Tagger.loadFrom(taggerModelPath)
  implicit val lemmatizer = Lemmatizer.loadFrom(lemmatizerModelPath)

  val tokenizeSentence = new TokenizeSentenceStep
  val parseQuestion = new ParseQuestionStep

  def apply(req: Request): Future[Response] = {
    val steps = GetSentenceStep
        .compose(tokenizeSentence)
        .compose(parseQuestion)
        .compose(CompileQuestionStep)
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