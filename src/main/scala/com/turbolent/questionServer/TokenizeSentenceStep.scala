package com.turbolent.questionServer


import com.turbolent.questionParser.Token
import com.twitter.finagle.http.Request
import com.twitter.util.Future
import spacyThrift.client.SpacyThriftClient


class TokenizeSentenceStep(spacyThriftClient: SpacyThriftClient)
    extends QuestionStep[String, Seq[Token]]
{

  def apply(req: Request, sentence: String, response: QuestionResponse) =
    spacyThriftClient.tag(sentence).map { spacyTokens =>
      val tokens = spacyTokens.map {
        case spacyThrift.Token(word, tag, lemma) => Token(word, tag, lemma)
      }
      (tokens, response + ("tokens" -> tokens))
    }
}
