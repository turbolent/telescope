package com.turbolent.wikidataOntology

import com.turbolent.questionCompiler.QuestionCompiler
import com.turbolent.questionCompiler.sparql.SparqlGraphCompiler
import com.turbolent.questionParser.{ListParser, Token}
import org.apache.jena.query.{Query, QueryFactory}
import org.apache.jena.sparql.algebra.Algebra
import org.scalatest.{Assertion, Matchers}


trait Utilities extends Matchers {

  val wikidataOntology: WikidataOntology

  def compileListQuestion(tokens: Seq[Token]): Seq[WikidataNode] = {
    val result = ListParser.parse(tokens, ListParser.phrase(ListParser.Question))
    assertSuccess(result)
    val question = result.get
    new QuestionCompiler(wikidataOntology, new WikidataEnvironment())
      .compileQuestion(question)
  }

  def tokenize(taggedSentence: String): Array[Token] =
    taggedSentence.split(" ").map(_.split("/")).map {
      case Array(word, tag, lemma) => Token(word, tag, lemma)
    }

  def assertSuccess(result: ListParser.ParseResult[_]): Assertion =
    result shouldBe a [ListParser.Success[_]]

  def assertEquivalent(expected: Query, actual: Query) {
    // NOTE: compare Ops, comparing queries is broken (ElementUnion)
    val expectedOp = Algebra.compile(expected)
    val actualOp = Algebra.compile(actual)
    try {
      expectedOp shouldEqual actualOp
    } catch {
      case e: Throwable =>
        println(actual)
        throw e
    }
  }

  val PROLOGUE = """
                   |PREFIX  bd:        <http://www.bigdata.com/rdf#>
                   |PREFIX  wdt:       <http://www.wikidata.org/prop/direct/>
                   |PREFIX  wikibase:  <http://wikiba.se/ontology#>
                   |PREFIX  xsd:       <http://www.w3.org/2001/XMLSchema#>
                   |PREFIX  rdfs:      <http://www.w3.org/2000/01/rdf-schema#>
                   |PREFIX  wd:        <http://www.wikidata.org/entity/>
                   |PREFIX  p:         <http://www.wikidata.org/prop/>
                   |PREFIX  v:         <http://www.wikidata.org/prop/statement/>
                   |
                 """.stripMargin

  val SELECT_FORMAT = "SELECT DISTINCT ?%s ?%sLabel"
  val WHERE_FORMAT = """
                       |WHERE {
                       |  %s
                       |  SERVICE wikibase:label {
                       |     bd:serviceParam wikibase:language "en" .
                       |  }
                       |}
                     """.stripMargin
  val ORDER_FORMAT = "ORDER BY %s"

  def parseSparqlQuery(variable: String, query: String, ordering: Option[String]): Query =
    QueryFactory.create(PROLOGUE
      + String.format(SELECT_FORMAT, variable, variable)
      + String.format(WHERE_FORMAT, query)
      + ordering.map(String.format(ORDER_FORMAT, _)).getOrElse(""))

  def compileSparqlQuery(node: WikidataNode, env: WikidataEnvironment): Query = {
    val backend = new WikidataSparqlBackend
    val compiler = new SparqlGraphCompiler(backend, env)
    compiler.compileQuery(node)
  }
}
