package com.turbolent.wikidataOntology

import java.time.Year

import com.turbolent.questionCompiler.graph.EqualsFilter
import com.turbolent.questionCompiler.graph.Node
import com.turbolent.questionCompiler.sparql.{NodeCompilationContext, SparqlBackend, TripleNodeCompilationContext}
import org.apache.jena.datatypes.RDFDatatype
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.graph.{Node => JenaNode, NodeFactory => JenaNodeFactory, Triple => JenaTriple}
import org.apache.jena.query.Query
import org.apache.jena.sparql.algebra.Op
import org.apache.jena.sparql.algebra.op.{OpBGP, OpJoin, OpLeftJoin, OpService}
import org.apache.jena.sparql.core.{BasicPattern, Var}
import org.apache.jena.sparql.path._
import org.apache.jena.sparql.expr.{E_DateTimeYear, Expr}

import collection.mutable


class WikidataSparqlBackend extends SparqlBackend[NodeLabel, EdgeLabel, WikidataEnvironment] {

  val ENTITY_BASE = "http://www.wikidata.org/entity/"
  val PROPERTY_BASE = "http://www.wikidata.org/prop/direct/"
  val RDFS_BASE = "http://www.w3.org/2000/01/rdf-schema#"
  val XSD_BASE = "http://www.w3.org/2001/XMLSchema#"
  val WIKIBASE_BASE = "http://wikiba.se/ontology#"
  val BIGDATA_BASE = "http://www.bigdata.com/rdf#"
  val STATEMENT_BASE = "http://www.wikidata.org/prop/"
  val VALUE_BASE = "http://www.wikidata.org/prop/statement/"
  val SCHEMA_BASE = "http://schema.org/"

  override def prepareQuery(query: Query, env: WikidataEnvironment) {
    query.setPrefix("wd", ENTITY_BASE)
    query.setPrefix("wdt", PROPERTY_BASE)
    query.setPrefix("rdfs", RDFS_BASE)
    query.setPrefix("xsd", XSD_BASE)
    query.setPrefix("wikibase", WIKIBASE_BASE)
    query.setPrefix("bd", BIGDATA_BASE)
    query.setPrefix("p", STATEMENT_BASE)
    query.setPrefix("v", VALUE_BASE)
    query.setPrefix("schema", SCHEMA_BASE)
  }

  def compileUnit(unit: Unit): RDFDatatype =
  // TODO:
    XSDDatatype.XSDinteger

  override def compileNodeLabel(label: NodeLabel, env: WikidataEnvironment): JenaNode =
    label match {
      case VarLabel(id) =>
        Var.alloc(id.toString)

      case ItemLabel(item) =>
        JenaNodeFactory.createURI(ENTITY_BASE + "Q" + item.id)

      case ValueLabel(value) =>
        JenaNodeFactory.createLiteral(value, "en")

      case NumberLabel(value) =>
        JenaNodeFactory.createLiteral(value.toString, XSDDatatype.XSDdouble)

      case NumberWithUnitLabel(value, unit) =>
        val datatype = compileUnit(unit)
        JenaNodeFactory.createLiteral(value.toString, datatype)

      case TemporalLabel(temporal) =>
        temporal match {
          case year: Year =>
            val value = year.getValue.toString
            JenaNodeFactory.createLiteral(value, XSDDatatype.XSDinteger)
        }
    }

  def compilePropertyNode(property: Property): JenaNode =
    JenaNodeFactory.createURI(PROPERTY_BASE + "P" + property.id)

  def compilePropertyStatementNode(property: Property): JenaNode =
    JenaNodeFactory.createURI(STATEMENT_BASE + "P" + property.id)

  def compilePropertyValueNode(property: Property): JenaNode =
    JenaNodeFactory.createURI(VALUE_BASE + "P" + property.id)


  def locatedInPath(property: Property) =
    new P_Seq(new P_Link(compilePropertyNode(property)),
      new P_ZeroOrMore1(new P_Link(compilePropertyNode(P.isLocatedIn))))

  def instanceOfSubclassPath(property: Property): Path = {
    // use `p:P<ID>/v:P<ID>/wdt:P279*` instead of plain wdt:P<ID> (P279 = isSubclassOf):
    //   - entities will may have several values for this property and they are only
    //     accessible through the statement/value path
    //   - instance relationships through superclasses are not automatically inferred

    val instanceOfStatementPath = new P_Link(compilePropertyStatementNode(property))
    val instanceOfValuePath = new P_Link(compilePropertyValueNode(property))
    val subclassOfPath = new P_Link(compilePropertyNode(P.isSubclassOf))
    new P_Seq(instanceOfStatementPath,
      new P_Seq(instanceOfValuePath,
        new P_ZeroOrMore1(subclassOfPath)))
  }

  def containmentPath(property: Property) =
    new P_OneOrMore1(new P_Link(compilePropertyNode(property)))

  val paths: mutable.Map[Property, Path] =
    mutable.Map(
      P.isA -> instanceOfSubclassPath(P.isA),
      P.hasOccupation -> instanceOfSubclassPath(P.hasOccupation),
      P.holdsPosition -> instanceOfSubclassPath(P.holdsPosition),
      P.isPartOf -> containmentPath(P.isPartOf),
      P.isLocatedIn -> containmentPath(P.isLocatedIn),
      P.hasPlaceOfBirth -> locatedInPath(P.hasPlaceOfBirth),
      P.hasPlaceOfDeath -> locatedInPath(P.hasPlaceOfDeath),
      P.hasFilmingLocation -> locatedInPath(P.hasFilmingLocation),
      P.hasHeadquartersLocation -> locatedInPath(P.hasHeadquartersLocation))

  def compileProperty(property: Property): Either[JenaNode, Path] =
    paths.get(property) map { Right(_) } getOrElse {
      val node = compilePropertyNode(property)
      Left(node)
    }

  private val rdfsLabelNode: JenaNode =
    JenaNodeFactory.createURI(RDFS_BASE + "label")

  // enable labeling service by adding
  // `SERVICE wikibase:label { bd:serviceParam wikibase:language "en" }`.
  private val labelServiceOp: Op = {
    val triple = new JenaTriple(
      JenaNodeFactory.createURI(BIGDATA_BASE + "serviceParam"),
      JenaNodeFactory.createURI(WIKIBASE_BASE + "language"),
      JenaNodeFactory.createLiteral("en")
    )
    val pattern = new BasicPattern()
    pattern.add(triple)
    val subOp = new OpBGP(pattern)
    new OpService(JenaNodeFactory.createURI(WIKIBASE_BASE + "label"), subOp, false)
  }

  private val wikipediaTitleVariable: Var =
    Var.alloc("wikipediaTitle")

  // include title of English Wikipedia page
  // ```
  // ?sitelink schema:about ?item ;
  //           schema:isPartOf <https://en.wikipedia.org/> ;
  //           schema:name ?wikipediaTitle
  // ```
  private def wikipediaTitleOp(target: Var): Op = {
    val site = Var.alloc("site")
    val pattern = new BasicPattern()
    pattern.add(new JenaTriple(
      site,
      JenaNodeFactory.createURI(SCHEMA_BASE + "about"),
      target
    ))
    pattern.add(new JenaTriple(
      site,
      JenaNodeFactory.createURI(SCHEMA_BASE + "isPartOf"),
      JenaNodeFactory.createURI("https://en.wikipedia.org/")
    ))
    pattern.add(new JenaTriple(
      site,
      JenaNodeFactory.createURI(SCHEMA_BASE + "name"),
      wikipediaTitleVariable
    ))
    new OpBGP(pattern)
  }

  override def compileEdgeLabel(label: EdgeLabel,
                                env: WikidataEnvironment): Either[JenaNode, Path] = {
    label match {
      case PropertyLabel(property) =>
        compileProperty(property)

      case NameLabel =>
        Left(rdfsLabelNode)
    }
  }

  override def additionalResultVariables(variable: Var, env: WikidataEnvironment): List[Var] = {
    // add additional label variable, resolved by labeling service.
    // also see prepareOp

    val optLabelVariable = Option(env.generateLabel)
      .collect { case true =>
        val name = variable.getVarName
        Var.alloc(name + "Label")
      }

    val optWikipediaTitleVariable = Option(env.generateWikipediaTitle)
      .collect { case true =>
        wikipediaTitleVariable
      }

    List(optLabelVariable, optWikipediaTitleVariable).flatten
  }

  override def prepareOp(op: Op, variable: Var, env: WikidataEnvironment): Op = {
    // also see additionalResultVariables

    val optLabelServiceOp = Option(env.generateLabel)
      .collect { case true =>
        (labelServiceOp, OpJoin.create _)
      }

    val optWikipediaTitleOp = Option((env.generateWikipediaTitle, env.wikipediaTitleIsOptional))
      .collect { case (true, optional) =>
        (
          wikipediaTitleOp(variable),
          if (optional)
            (op1: Op, op2: Op) =>
              OpLeftJoin.create(op1, op2, null.asInstanceOf[Expr])
          else
            OpJoin.create _
        )
      }

    Seq(optLabelServiceOp, optWikipediaTitleOp).flatten.foldLeft(op) {
      case (result, (other, f)) =>
        f(result, other)
    }
  }

  override def expandNode(node: Node, context: NodeCompilationContext,
                          env: WikidataEnvironment): Node =
  {
    (context, node.label) match {
      case (TripleNodeCompilationContext, label: TemporalLabel) =>
        // temporal only allowed in filter, introduce intermediate node
        env.newNode().filter(EqualsFilter(node))
      case _ =>
        node
    }
  }

  override def prepareLeftFunctionExpression(leftExpr: Expr, otherNode: Node): Expr =
    otherNode match {
      case Node(TemporalLabel(_: Year), _, _, _, _) =>
        new E_DateTimeYear(leftExpr)

      case _ =>
        leftExpr
    }
}
