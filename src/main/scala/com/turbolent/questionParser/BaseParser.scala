package com.turbolent.questionParser

import com.turbolent.lemmatizer.Lemmatizer

import scala.util.parsing.combinator.PackratParsers


trait BaseParser extends PackratParsers {
  override type Elem = Token

  def parse[T](tokens: Seq[Token], production: Parser[T])(implicit lemmatizer: Lemmatizer) = {
    val tokenReader = new TokensReader(tokens)
    val packratReader = new PackratReader(tokenReader)
    production(packratReader)
  }

  implicit def word(word: String): Parser[Token] =
    elem(s"word '${word.toLowerCase}'", {
      _.word equalsIgnoreCase word
    })

  def pos(pos: String, strict: Boolean = false) =
    elem(s"POS $pos*",
      if (strict) { _.pennTag.equals(pos) }
      else { _.pennTag.startsWith(pos) })

  def lemma(lemma: String) = {
    val lowered = lemma.toLowerCase
    elem(s"lemma '$lowered'", { _.lemmas.contains(lowered) })
  }

  lazy val Noun = pos("N")

  lazy val Nouns = rep1(Noun)

  lazy val Verb = pos("V")

  lazy val Verbs = rep1(Verb)

  lazy val Number = pos("CD")

  lazy val Numbers = rep1(Number)

  def orAndList[T](parser: Parser[T],
                   innerWrapper: Seq[T] => T,
                   outerWrapper: Seq[T] => T,
                   andOptional: Boolean = false): Parser[T] =
  {
    val and = word("and")
    val andParser = rep1sep(parser, if (andOptional) opt(and) else and)
    rep1sep(andParser, word("or")) ^^ {
      _ map {
        case Seq(inner) => inner
        case inner => innerWrapper(inner)
      } match {
        case Seq(outer) => outer
        case outer => outerWrapper(outer)
      }
    }
  }

  def ignore(p: Parser[_]) = p ^^^ None

}
