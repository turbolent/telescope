package com.turbolent.questionParser

import scala.util.parsing.combinator.PackratParsers
import scala.language.implicitConversions

trait BaseParser extends PackratParsers {
  override type Elem = Token

  def parse[T](tokens: Seq[Token], production: Parser[T]): ParseResult[T] = {
    val tokenReader = new TokensReader(tokens)
    val packratReader = new PackratReader(tokenReader)
    production(packratReader)
  }

  implicit def word(word: String): Parser[Token] =
    elem(s"word '${word.toLowerCase}'", _.word.equalsIgnoreCase(word))

  def pos(pos: String, strict: Boolean): Parser[Token] =
    elem(s"POS $pos*",
         if (strict) _.pennTag.equals(pos)
         else _.pennTag.startsWith(pos))

  def lemma(lemma: String): Parser[Token] = {
    val lowered = lemma.toLowerCase
    elem(s"lemma '$lowered'", _.lemma == lowered)
  }

  lazy val Noun: Parser[Token] =
    pos("N", strict = false)

  lazy val Nouns: Parser[List[Token]] =
    rep1(Noun)

  lazy val Verb: Parser[Token] =
    pos("V", strict = false)

  lazy val Verbs: Parser[List[Token]] =
    rep1(Verb)

  lazy val Number: Parser[Token] =
    pos("CD", strict = true)

  lazy val Numbers: Parser[List[Token]] =
    rep1(Number)

  lazy val Particle: Parser[Token] =
    pos("RP", strict = true)

  lazy val Preposition: Parser[Token] =
    pos("IN", strict = true) |
      pos("TO", strict = true)

  lazy val Determiner: Parser[Token] =
    pos("DT", strict = true)

  lazy val StrictAdjective: Parser[Token] =
    pos("JJ", strict = true)

  lazy val AnyAdjective: Parser[Token] =
    pos("JJ", strict = false)

  lazy val ComparativeAdjective: Parser[Token] =
    pos("JJR", strict = true)

  lazy val SuperlativeAdjective: Parser[Token] =
    pos("JJS", strict = true)

  lazy val Possessive: Parser[Token] =
    pos("POS", strict = true)

  lazy val CoordinatingConjunction: Parser[Token] =
    pos("CC", strict = true)

  lazy val WhDeterminer: Parser[Token] =
    pos("WDT", strict = true)

  lazy val SentenceTerminator: Parser[Token] =
    pos(".", strict = true)

  def commaOrAndList[T](parser: Parser[T],
                        andReducer: Seq[T] => T,
                        orReducer: Seq[T] => T,
                        andOptional: Boolean): Parser[T] = {
    val and = word("and")
    val or = word("or")
    val andParser = rep1sep(parser, if (andOptional) opt(and) else and)
    val orParser = rep1sep(andParser, or) ^^ {
      _.map {
        case Seq(inner) => inner
        case inner      => andReducer(inner)
      } match {
        case Seq(outer) => outer
        case outer      => orReducer(outer)
      }
    }

    (opt((rep1sep(orParser, ",") <~ ",") ~ (and | or)) ~ orParser) ^^ {
      case Some(more ~ orAnd) ~ last =>
        val reducer = if (orAnd.word == "and") andReducer else orReducer
        reducer(more :+ last)
      case None ~ last =>
        last
    }
  }

  def ignore(p: Parser[_]): Parser[Unit] = p ^^^ (())

}
