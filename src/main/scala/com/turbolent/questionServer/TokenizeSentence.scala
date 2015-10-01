package com.turbolent.questionServer

import com.turbolent.aptagger.Tagger
import com.turbolent.lemmatizer.Lemmatizer
import com.turbolent.questionParser.Token
import com.twitter.finagle.httpx.Request
import com.twitter.util.Future


class TokenizeSentence(implicit tagger: Tagger, lemmatizer: Lemmatizer)
    extends ParseStep[String, Seq[Token]]
{

  def apply(req: Request, sentence: String, response: ParseResponse) = {
    val tokens = Token.tokensFromSentence(sentence)
    Future.value((tokens, response + ("tokens" -> tokens)))
  }

}
