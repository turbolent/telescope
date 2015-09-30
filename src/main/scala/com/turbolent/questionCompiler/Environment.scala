package com.turbolent.questionCompiler

import graph.Node


trait Environment[N, E] {
  def newNode(): Node[N, E]
}