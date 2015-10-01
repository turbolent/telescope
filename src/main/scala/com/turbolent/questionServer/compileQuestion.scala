package com.turbolent.questionServer

import com.turbolent.questionCompiler.QuestionCompiler
import com.turbolent.questionParser.ast.Question
import com.turbolent.wikidataOntology.{WikidataEnvironment, WikidataOntology}
import com.twitter.finagle.httpx.{Request, Status}
import com.twitter.util.Future


object compileQuestion extends ParseStep[Question, Seq[WikidataNode]] {

  def apply(req: Request, question: Question, response: ParseResponse) = {
    try {
      val nodes = new QuestionCompiler(WikidataOntology, new WikidataEnvironment)
          .compileQuestion(question)
      Future.value((nodes, response + ("nodes" -> nodes)))
    } catch {
      case e: RuntimeException =>
        Future.exception(ParseError(Status.Ok,
          response + ("error" -> e.getMessage)))
    }
  }
}
