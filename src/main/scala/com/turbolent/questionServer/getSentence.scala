package com.turbolent.questionServer

import com.twitter.finagle.httpx.{Request, Status}
import com.twitter.util.Future


object getSentence extends ParseStep[Unit, String] {

  val sentenceParameter = "sentence"

  def apply(req: Request, input: Unit, response: ParseResponse) =
    req.params.get(sentenceParameter) map { sentence =>
      Future.value((sentence, response))
    } getOrElse {
      Future.exception(ParseError(Status.BadRequest,
        response + ("error" -> s"Missing query parameter: $sentenceParameter")))
    }
}
