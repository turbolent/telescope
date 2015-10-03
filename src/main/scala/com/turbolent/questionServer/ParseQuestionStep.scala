package com.turbolent.questionServer

import com.turbolent.lemmatizer.Lemmatizer
import com.turbolent.questionParser.ast.Question
import com.turbolent.questionParser.{ListParser, Token}
import com.twitter.finagle.httpx.{Request, Status}
import com.twitter.util.Future


class ParseQuestionStep(implicit lemmatizer: Lemmatizer)
    extends QuestionStep[Seq[Token], Question]
{

  def apply(req: Request, tokens: Seq[Token], response: QuestionResponse) = {
    val isStrict = req.getBooleanParam("strict")
    val questionParser = ListParser.Question
    val result = ListParser.parse(tokens,
      if (isStrict)
        ListParser.phrase(questionParser)
      else
        questionParser)
    result map { question =>
      Future.value((question, response + ("question" -> question)))
    } getOrElse {
      Future.exception(QuestionError(Status.Ok,
        response + ("error" -> s"Couldn't parse sentence: $result")))
    }
  }
}
