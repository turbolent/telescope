package com.turbolent.questionServer

import java.io.{PrintWriter, StringWriter}

import com.turbolent.questionCompiler.sparql.SparqlGraphCompiler
import com.turbolent.wikidataOntology.{WikidataSparqlBackend, EdgeLabel, NodeLabel}
import com.twitter.finagle.httpx.{Status, Request}
import com.twitter.util.Future
import org.apache.jena.query.{Query => JenaQuery}


object CompileQueriesStep extends QuestionStep[Seq[WikidataNode], Seq[JenaQuery]] {

  def apply(req: Request, nodes: Seq[WikidataNode], response: QuestionResponse) = {
    try {
      val compiler = new SparqlGraphCompiler[NodeLabel, EdgeLabel](WikidataSparqlBackend)
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
