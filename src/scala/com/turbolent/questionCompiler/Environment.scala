package com.turbolent.questionCompiler

import graph.Node


trait Environment[N, E] {

  /** Return a new, anonymous node */
  def newNode(): Node[N, E]
}