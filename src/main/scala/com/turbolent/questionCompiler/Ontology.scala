package com.turbolent.questionCompiler

import com.turbolent.questionParser.Token


trait Ontology[N, E] {

  type Node = graph.Node[N, E]
  type Edge = graph.Edge[E, N]
  type Env = Environment[N, E]

  def makePersonEdge(env: Env): Edge

  def makeNamedPropertyEdge(name: Seq[Token], node: Node,
                            subject: Subject, env: Env): Edge

  def makeInversePropertyEdge(name: Seq[Token], node: Node,
                              context: EdgeContext, env: Env): Edge

  def makeAdjectivePropertyEdge(name: Seq[Token], node: Node,
                                context: EdgeContext, env: Env): Edge

  def makeComparativePropertyEdge(name: Seq[Token], node: Node,
                                  context: EdgeContext, env: Env): Edge

  def makeValuePropertyEdge(name: Seq[Token], node: Node,
                            context: EdgeContext, env: Env): Edge

  def makeRelationshipEdge(name: Seq[Token], node: Node, env: Env): Edge

  def makeValueNode(name: Seq[Token], filter: Seq[Token], env: Env): Node

  def makeNumberNode(number: Seq[Token], unit: Seq[Token], filter: Seq[Token], env: Env): Node

}
