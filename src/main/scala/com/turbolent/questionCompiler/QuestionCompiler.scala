package com.turbolent.questionCompiler

import graph.{Node, Edge, ConjunctionEdge, DisjunctionEdge}

import com.turbolent.questionParser.{ast, Token}


class QuestionCompiler[N, E, EnvT <: Environment[N, E]]
    (ontology: Ontology[N, E, EnvT], env: EnvT)
{

  type NodeT = Node[N, E]
  type EdgeT = Edge[E, N]
  type NodeFactory = (NodeT, Seq[Token]) => NodeT
  type EdgeContextFactory = (Subject) => EdgeContext
  type EdgeFactory = (NodeT, EdgeContextFactory) => EdgeT

  val identityNodeFactory: NodeFactory =
    (node, name) => node

  def compileQuestion(question: ast.Question): Seq[NodeT] =
    question match {
      case ast.PersonListQuestion(properties) =>
        val node = env.newNode()
            .and(ontology.makePersonEdge(env))
            .and(compileProperty(properties, PersonSubject))
        Seq(node)

      case ast.ThingListQuestion(properties) =>
        val node = env.newNode()
            .and(compileProperty(properties, ThingSubject))
        Seq(node)

      case ast.ListQuestion(query) =>
        compileQuery(query, identityNodeFactory)
    }

  def compileQuery(query: ast.Query, nodeFactory: NodeFactory): Seq[NodeT] =
    query match {
      case ast.QueryWithProperty(nestedQuery, property) =>
        val nodeFactory: NodeFactory = (node, name) =>
          node.and(compileProperty(property, NamedSubject(name)))
        compileQuery(nestedQuery, nodeFactory)

      case ast.NamedQuery(name) =>
        val node = ontology.makeValueNode(name, Nil, env)
        Seq(nodeFactory(node, name))

      case ast.AndQuery(queries) =>
        queries.flatMap(compileQuery(_, nodeFactory))

      case ast.RelationshipQuery(first, second, _) =>
        val nodes = compileQuery(second, nodeFactory)
        compileRelationshipSubquery(first, nodes)
    }

  def compileRelationshipSubquery(query: ast.Query, nodes: Seq[NodeT]): Seq[NodeT] =
    query match {
      case ast.NamedQuery(name) =>
        nodes.map { node =>
          val edge = ontology.makeRelationshipEdge(name, node, env)
          env.newNode().and(edge)
        }

      case ast.AndQuery(queries) =>
        queries.flatMap(compileRelationshipSubquery(_, nodes))

      case ast.RelationshipQuery(first, second, _) =>
        val secondNodes = compileRelationshipSubquery(second, nodes)
        compileRelationshipSubquery(first, secondNodes)
    }

  def compileProperty(property: ast.Property, subject: Subject): EdgeT =
    property match {
      case ast.NamedProperty(name) =>
        ontology.makeNamedPropertyEdge(name, env.newNode(), subject, env)

      case ast.PropertyWithFilter(name, filter) =>
        val comparative = filter.isInstanceOf[ast.FilterWithComparativeModifier]
        compileFilter(filter, (node, contextFactory) => {
          val context = contextFactory(subject)
          if (comparative)
            ontology.makeComparativePropertyEdge(name, node, context, env)
          else
            ontology.makeValuePropertyEdge(name, node, context, env)
        })

      case ast.InversePropertyWithFilter(name, filter) =>
        compileFilter(filter, (node, contextFactory) =>
          ontology.makeInversePropertyEdge(name, node,
            contextFactory(subject), env))

      case ast.AdjectivePropertyWithFilter(name, filter) =>
        compileFilter(filter, (node, contextFactory) =>
          ontology.makeAdjectivePropertyEdge(name, node,
            contextFactory(subject), env))

      case ast.AndProperty(properties) =>
        ConjunctionEdge(properties.map(compileProperty(_, subject)).toSet)

      case ast.OrProperty(properties) =>
        DisjunctionEdge(properties.map(compileProperty(_, subject)).toSet)
    }


  def compileFilter(filter: ast.Filter, edgeFactory: EdgeFactory): EdgeT =
    filter match {
      case ast.FilterWithModifier(modifier, value) =>
        compileValue(value, modifier, edgeFactory)
      case ast.FilterWithComparativeModifier(modifier, value) =>
        compileValue(value, modifier, edgeFactory)
      case ast.PlainFilter(value) =>
        compileValue(value, Nil, edgeFactory)
    }

  def compileValue(value: ast.Value, filter: Seq[Token], edgeFactory: EdgeFactory): EdgeT =
    value match {
      case ast.NamedValue(name) =>
        val node = ontology.makeValueNode(name, filter, env)
        edgeFactory(node, (subject) =>
          EdgeContext(subject, filter, name, Nil,
            valueIsNumber = false))

      case ast.NumberWithUnit(number, unit) =>
        val node = ontology.makeNumberNode(number, unit, filter, env)
        edgeFactory(node, (subject) =>
          EdgeContext(subject, filter, number, unit,
            valueIsNumber = true))

      case ast.Number(number) =>
        val node = ontology.makeNumberNode(number, Nil, filter, env)
        edgeFactory(node, (subject) =>
          EdgeContext(subject, filter, number, Nil,
            valueIsNumber = true))

      case ast.OrValue(values) =>
        DisjunctionEdge(values.map(compileValue(_, filter, edgeFactory)).toSet)

      case ast.AndValue(values) =>
        ConjunctionEdge(values.map(compileValue(_, filter, edgeFactory)).toSet)

      case ast.ValueRelationship(ast.NamedValue(name), second) =>
        val secondEdgeFactory: EdgeFactory =
          (node, contextFactory) =>
            ontology.makeRelationshipEdge(name, node, env)
        val edge = compileValue(second, filter, secondEdgeFactory)
        val node = env.newNode().and(edge)
        edgeFactory(node, (subject) =>
          EdgeContext(subject, filter, name, Nil,
            valueIsNumber = false))
    }
}