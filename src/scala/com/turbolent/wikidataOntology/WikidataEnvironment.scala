package com.turbolent.wikidataOntology

import java.util.concurrent.atomic.AtomicInteger

import com.turbolent.questionCompiler.Environment
import com.turbolent.questionCompiler.graph.Node

class WikidataEnvironment(val generateLabel: Boolean,
                          val generateWikipediaTitle: Boolean,
                          val wikipediaTitleIsOptional: Boolean)
    extends Environment[NodeLabel, EdgeLabel] {

  override def newNode() = Node(newVar())

  private val varCounter = new AtomicInteger()

  private def newVar() =
    VarLabel(varCounter.addAndGet(1))
}
