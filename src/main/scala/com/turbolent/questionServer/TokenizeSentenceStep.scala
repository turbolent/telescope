package com.turbolent.questionServer


import com.turbolent.questionParser.Token
import com.twitter.finagle.http.Request


class TokenizeSentenceStep(tagger: Tagger) extends QuestionStep[String, Seq[Token]] {

  def apply(req: Request, sentence: String, response: QuestionResponse) =
    tagger.tag(sentence).map { tokens =>
      (tokens, response + ("tokens" -> tokens))
    }
}
