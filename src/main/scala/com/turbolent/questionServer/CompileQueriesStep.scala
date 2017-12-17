package com.turbolent.questionServer

import java.io.{PrintWriter, StringWriter}

import com.turbolent.questionCompiler.sparql.SparqlGraphCompiler
import com.turbolent.wikidataOntology.{WikidataEnvironment, WikidataSparqlBackend}
import com.twitter.finagle.http.{Status, Request}
import com.twitter.util.Future
import org.apache.jena.query.{Query => JenaQuery}


object CompileQueriesStep
    extends QuestionStep[(Seq[WikidataNode], WikidataEnvironment), Seq[JenaQuery]]
{

  def apply(req: Request, input: (Seq[WikidataNode], WikidataEnvironment),
            response: QuestionResponse) =
  {
    try {
      val (nodes, env) = input
      val compiler = new SparqlGraphCompiler(new WikidataSparqlBackend, env)
      val queries = nodes.map(compiler.compileQuery)
      Future.value((queries, response + ("queries" -> queries)))
    } catch {
      case e: RuntimeException =>
        val writer = new StringWriter()
        e.printStackTrace(new PrintWriter(writer))
        Future.exception(QuestionError(Status.Ok,
          response + ("error" -> writer.toString)))
    }
  }
}
