package com.turbolent.questionServer

import java.io.{PrintWriter, StringWriter}

import com.turbolent.questionCompiler.QuestionCompiler
import com.turbolent.questionParser.ast.Question
import com.turbolent.wikidataOntology.{WikidataEnvironment, WikidataOntology}
import com.twitter.finagle.httpx.{Request, Status}
import com.twitter.util.Future


object CompileQuestionStep
    extends QuestionStep[Question, (Seq[WikidataNode], WikidataEnvironment)]
{

  def apply(req: Request, question: Question, response: QuestionResponse) = {
    try {
      val env = new WikidataEnvironment
      val nodes = new QuestionCompiler(WikidataOntology, env)
          .compileQuestion(question)
      val result = (nodes, env)
      Future.value((result, response + ("nodes" -> nodes)))
    } catch {
      case e: RuntimeException =>
        val writer = new StringWriter()
        e.printStackTrace(new PrintWriter(writer))
        Future.exception(QuestionError(Status.Ok,
          response + ("error" -> writer.toString)))
    }
  }
}
