package com.turbolent.questionServer

import java.io.{PrintWriter, StringWriter}

import com.turbolent.questionCompiler.QuestionCompiler
import com.turbolent.questionParser.ast.Question
import com.turbolent.wikidataOntology.{NumberParser, WikidataEnvironment, WikidataOntology}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.util.Future


class CompileQuestionStep(numberParser: NumberParser)
  extends QuestionStep[Question, (Seq[WikidataNode], WikidataEnvironment)]
{

  def apply(req: Request, question: Question, response: QuestionResponse) = {
    try {
      val generateLabel = req.params.getBooleanOrElse("label", default = true)
      val generateWikipediaTitle = req.params.getBooleanOrElse("wikipediaTitle", default = true)
      val wikipediaTitleIsOptional = req.params.getBooleanOrElse("wikipediaTitleOptional", default = true)

      val env = new WikidataEnvironment(
        generateLabel = generateLabel,
        generateWikipediaTitle = generateWikipediaTitle,
        wikipediaTitleIsOptional = wikipediaTitleIsOptional
      )
      val wikidataOntology = new WikidataOntology(numberParser)
      val nodes = new QuestionCompiler(wikidataOntology, env)
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
