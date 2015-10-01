package com.turbolent

import com.turbolent.questionCompiler.graph.Node
import com.turbolent.wikidataOntology.{EdgeLabel, NodeLabel}
import scala.collection.immutable.HashMap


package object questionServer {
  type ParseResponse = HashMap[String, AnyRef]

  type WikidataNode = Node[NodeLabel, EdgeLabel]
}
