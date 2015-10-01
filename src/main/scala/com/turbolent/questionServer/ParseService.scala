package com.turbolent.questionServer

import com.turbolent.aptagger.Tagger
import com.turbolent.lemmatizer.Lemmatizer
import com.turbolent.questionParser.Token
import com.twitter.finagle.Service
import com.twitter.finagle.httpx.{Request, Response, Status}
import com.twitter.util.Future
import org.json4s.native.Serialization
import org.json4s.{FieldSerializer, FullTypeHints}


class ParseService(configuration: Configuration) extends Service[Request, Response] {

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

  implicit val tagger = Tagger.loadFrom(configuration.taggerModelPath)
  implicit val lemmatizer = Lemmatizer.loadFrom(configuration.lemmatizerModelPath)

  val tokenizeSentence = new TokenizeSentence
  val parseQuestion = new ParseQuestion

  def apply(req: Request): Future[Response] = {
    val steps = getSentence
        .compose(tokenizeSentence)
        .compose(parseQuestion)
        .compose(compileQuestion)
    steps(req, (), new ParseResponse).flatMap {
      case (_, response) =>
        respond(req, Status.Ok, response)
    } rescue {
      case ParseError(status, content) =>
        respond(req, status, content)
    }
  }
}