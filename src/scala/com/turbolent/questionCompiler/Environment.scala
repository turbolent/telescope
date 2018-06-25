package com.turbolent.questionCompiler

trait Environment[N, E] {

  /** Return a new, anonymous node */
  def newNode(): graph.Node[N, E]
}
