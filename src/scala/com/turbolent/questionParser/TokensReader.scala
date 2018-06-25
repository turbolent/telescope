package com.turbolent.questionParser

import scala.util.parsing.input.Reader

class TokensReader(val elements: Seq[Token], val index: Int) extends Reader[Token] {

  def this(elements: Seq[Token]) =
    this(elements, 0)

  override def first =
    elements(index)

  override def atEnd: Boolean =
    index >= elements.length

  override def pos =
    new TokensPosition(index, elements)

  override def rest: TokensReader =
    if (index < elements.length)
      new TokensReader(elements, index + 1)
    else this
}
