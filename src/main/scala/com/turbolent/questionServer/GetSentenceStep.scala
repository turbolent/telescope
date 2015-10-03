package com.turbolent.questionServer

import com.twitter.finagle.httpx.{Request, Status}
import com.twitter.util.Future


object GetSentenceStep extends QuestionStep[Unit, String] {

  val sentenceParameter = "sentence"

  def getSentence(req: Request) =
    req.params.get(sentenceParameter)

  def apply(req: Request, input: Unit, response: QuestionResponse) =
    getSentence(req) map { sentence =>
      Future.value((sentence, response))
    } getOrElse {
      Future.exception(QuestionError(Status.BadRequest,
        response + ("error" -> s"Missing query parameter: $sentenceParameter")))
    }
}
