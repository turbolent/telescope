package com.turbolent.questionParser

import com.turbolent.questionParser.ast._

object ListParser extends BaseParser {

  // Examples:
  //   - "in which"
  //   - "what"

  lazy val whichWhat: PackratParser[Unit] =
    ignore(opt(Preposition) ~ ("which" | "what"))


  // Examples:
  //   - "what is"
  //   - "what are"
  //   - "who were"

  lazy val whoWhatBe: PackratParser[Unit] =
    ignore(("who" | "what") ~ lemma("be"))


  // Examples:
  //   - "find"
  //   - "list"
  //   - "show me"

  lazy val findListGiveShow: PackratParser[Unit] =
    ignore(("find" | "list" | "give" | "show") ~ opt("me"))


  // Examples:
  //   - "all"
  //   - "some of"
  //   - "a couple"

  lazy val someAllAny: PackratParser[Unit] =
    ignore((ignore("some" | "all" | "any" | "only" | "many" | "both") |
             ignore(opt("a") ~ ("few" | "couple" | "number" | "lot")))
           ~ opt("of"))


  // Examples:
  //   - "Europe"
  //   - "the US"
  //   - "the 19th century"
  //   - "green paintings"
  //   - "people"

  lazy val NamedValue: PackratParser[NamedValue] =
    (opt(Determiner) ~ rep(AnyAdjective) ~ Nouns) ^^ {
      case optDeterminer ~ adjectives ~ nouns =>
        optDeterminer map { determiner =>
          ast.NamedValue(determiner :: adjectives ++ nouns)
        } getOrElse
          ast.NamedValue(adjectives ++ nouns)
    }


  // Examples:
  //   - "100"
  //   - "thousand"
  //   - "1 million"
  //   - "42 meters"
  //   - "two million inhabitants"

  lazy val NumericValue: PackratParser[ast.Value] =
    (Numbers ~ opt(Nouns)) ^^ {
      case numbers ~ optNouns =>
        optNouns map {
          ast.NumberWithUnit(numbers, _)
        } getOrElse
          ast.Number(numbers)
    }


  // Examples:
  //   - "Obama's children"
  //   - "Obama's children's mothers"

  lazy val NamedValues: PackratParser[ast.Value] =
    (NamedValue ~ rep(Possessive ~> NamedValue)) ^^ {
      case first ~ rest =>
        rest.foldLeft[ast.Value](first) { (result, namedValue) =>
          ast.RelationshipValue(namedValue, result)
        }
    }


  // Examples:
  //   - "\"The Red Victorian\""

  lazy val OpeningQuotationMarks: PackratParser[Token] =
    pos("``", strict = true)

  lazy val ClosingQuotationMarks: PackratParser[Token] =
    pos("''", strict = true)

  lazy val AnyExceptClosingQuotationMarks: PackratParser[List[Token]] =
    rep(elem("not('')", _.pennTag != "''"))

  lazy val Quoted: PackratParser[List[Token]] =
    OpeningQuotationMarks ~> AnyExceptClosingQuotationMarks <~ ClosingQuotationMarks


  // Examples:
  //   - "America"
  //   - "dystopian societies"
  //   - "Obama's children"
  //   - "1900"
  //   - "1000 kilometers"
  //   - "two million inhabitants"
  //   - "\"I, Robot\""

  lazy val Value: PackratParser[Value] =
    NamedValues |
    NumericValue |
    (Quoted ^^ ast.NamedValue)


  // Examples:
  //   - "Europe or America"
  //   - "1900 or 1901"
  //   - "Copenhagen and Berlin"

  lazy val Values: PackratParser[Value] =
    commaOrAndList(Value, ast.AndValue, ast.OrValue,
      andOptional = false)


  // Examples:
  //   - "before 1900"
  //   - "in Europe"
  //   - "larger than Europe"
  //   - "smaller than Europe and the US"

  lazy val PrepositionExceptOf: PackratParser[Token] =
    Preposition filter { _.word != "of" }

  lazy val Filter: PackratParser[ast.Filter] =
    (opt(opt(ComparativeAdjective) ~ PrepositionExceptOf) ~ Values) ^^ {
      case optional ~ values =>
        optional map {
          case None ~ preposition =>
            ast.FilterWithModifier(Seq(preposition), values)
          case Some(comparative) ~ preposition =>
            ast.FilterWithComparativeModifier(Seq(comparative, preposition), values)
        } getOrElse
          ast.PlainFilter(values)
    }


  // Examples:
  //   - "before 1900"
  //   - "before 1900 or after 1910"

  lazy val Filters: PackratParser[Filter] =
    commaOrAndList(Filter, ast.AndFilter, ast.OrFilter,
      andOptional = false)


  // Examples:
  //   - "died"
  //   - "died before 1900 or after 1910" (NOTE: one property with two filters)
  //   - "by George Orwell"
  //   - "did George Orwell write"
  //   - "did Obama star in"
  // NOTE: first two examples are "direct", second two are "inverse":
  //       "did Orwell write" ~= "were written by" Orwell => "did write Orwell"

  lazy val InversePropertySuffix: PackratParser[(List[Token], Filter) => InversePropertyWithFilter] =
    (Verbs ~ opt(Particle | (Preposition <~ not(NamedValue)))) ^^ { suffix =>
      (verbs: List[Token], filter: ast.Filter) =>
        suffix match {
          case moreVerbs ~ Some(particle) =>
            ast.InversePropertyWithFilter(verbs ++ moreVerbs :+ particle, filter)
          case moreVerbs ~ None =>
            ast.InversePropertyWithFilter(verbs ++ moreVerbs, filter)
        }
    }

  lazy val PropertyAdjectiveSuffix: PackratParser[(List[Token], Filter) => AdjectivePropertyWithFilter] =
    StrictAdjective ^^ { adjective =>
      (verbs: List[Token], filter: ast.Filter) =>
        ast.AdjectivePropertyWithFilter(verbs :+ adjective, filter)
    }

  val auxiliaryVerbLemmas =
    Set("have", "be", "do")

  def isAuxiliaryVerb(token: Token): Boolean =
    auxiliaryVerbLemmas.contains(token.lemma)

  lazy val Property: PackratParser[ast.Property] =
    opt(WhDeterminer) ~> opt(Verbs) >> {
      // TODO: more after filters only when verb is auxiliary do/does/did
      case Some(verbs) =>
        val moreParser = verbs match {
          case List(token) if isAuxiliaryVerb(token) =>
            opt(InversePropertySuffix | PropertyAdjectiveSuffix)
          case _ =>
            opt(PropertyAdjectiveSuffix)
        }
        opt(Filters ~ moreParser) ^^ {
          case Some(filter ~ more) => more match {
            case Some(suffixConstructor) =>
              suffixConstructor(verbs, filter)
            case None =>
              ast.PropertyWithFilter(verbs, filter)
          }
          case None =>
            ast.NamedProperty(verbs)
        }
      case None =>
        Filters ^^ {
          ast.PropertyWithFilter(List(), _)
        }
    }


  // Examples:
  //   - "died before 1900 or after 1910 or were born in 1923"
  //   - "written by Orwell were longer than 200 pages"
  //     (NOTE: 2 properties, "and" is optional,
  //            valid when starting with "which books")

  lazy val Properties: PackratParser[Property] =
    commaOrAndList(Property, ast.AndProperty, ast.OrProperty,
      andOptional = true)


  // Examples:
  //   - "of the USA"
  //   - "of China"

  lazy val Relationship: PackratParser[~[Token, Query]] =
    "of" ~ FullQuery


  // Examples:
  //   - "youngest children"
  //   - "the largest cities"
  //   - "main actor"

  // Note: disambiguate/extract superlative and adjective in later stage,
  //       might either be named entity or specific type

  lazy val NamedQuery: PackratParser[NamedQuery] =
    (opt(Determiner) ~ opt(SuperlativeAdjective) ~ rep(AnyAdjective) ~ Nouns) ^^ {
      case None ~ None ~ adjectives ~ nouns =>
        ast.NamedQuery(adjectives ++ nouns)
      case None ~ Some(superlative) ~ adjectives ~ nouns =>
        ast.NamedQuery(superlative :: adjectives ++ nouns)
      case Some(determiner) ~ None ~ adjectives ~ nouns =>
        ast.NamedQuery(determiner :: adjectives ++ nouns)
      case Some(determiner) ~ Some(superlative) ~ adjectives ~ nouns =>
        ast.NamedQuery(determiner :: superlative :: adjectives ++ nouns)
    }


  lazy val Query: PackratParser[NamedQuery] =
    NamedQuery |
    (Quoted ^^ ast.NamedQuery)


  // Examples:
  //   - "children and grandchildren"
  //   - "China, the USA, and Japan"
  // TODO: differentiate "and" and "or"?!
  // TODO: handling nesting of "and" and "or"

  lazy val QueriesSeparator: PackratParser[Unit] =
    ignore("," ~ opt(CoordinatingConjunction)) |
    ignore(opt(",") ~ CoordinatingConjunction)

  lazy val Queries: PackratParser[ast.Query] =
    rep1sep(Query, QueriesSeparator) ^^ {
      case Seq(x) => x
      case xs => ast.AndQuery(xs)
    }


  // Examples:
  //   - "Clinton's children"
  //   - "California's cities"
  //   - "California's cities' population sizes"
  //   - "Clinton's children and grandchildren"

  lazy val QueryRelationships: PackratParser[ast.Query] =
    chainl1(Queries, Possessive ^^ { sep =>
      (a: ast.Query, b: ast.Query) =>
        ast.RelationshipQuery(b, a, sep)
    })


  // Examples
  //   - "people"
  //   - "actors that died in Berlin"
  //   - "engineers which"
  //   - "largest cities that"
  //   - "cities of California"
  //   - "California's cities"
  //   - "population of the USA"

  // TODO: execution should "filter" first (use properties),
  //       before applying inner superlative

  lazy val QueryProperties: PackratParser[Query => QueryWithProperty] =
    Properties ^^ { property =>
      (query: ast.Query) =>
        ast.QueryWithProperty(query, property)
    }

  lazy val QueryRelationship: PackratParser[Query => RelationshipQuery] =
    Relationship ^^ {
      case sep ~ nested =>
        (query: ast.Query) =>
          ast.RelationshipQuery(query, nested, sep)
    }

  lazy val FullQuery: PackratParser[ast.Query] =
    ((QueryRelationships <~ opt(WhDeterminer | "who")) ~ opt(QueryProperties ||| QueryRelationship)) ^^ {
      case query ~ None =>
        query
      case query ~ Some(queryConstructor) =>
        queryConstructor(query)
    }


  // Examples:
  //   - "which"
  //   - "what are"
  //   - "find all"
  //   - "give me a few"
  // NOTE: ||| to match as much as possible

  lazy val ListQuestionStart: PackratParser[Unit] =
    ignore(opt(whichWhat ||| ignore((whoWhatBe ||| findListGiveShow) ~ opt(someAllAny))))


  // Examples:
  //   - "which presidents were born before 1900"
  //   - "give me all actors born in Berlin and San Francisco"

  lazy val ListQuestion: PackratParser[ListQuestion] =
    (ListQuestionStart ~ FullQuery) ^^ {
      // TODO: handle start
      case _ ~ query =>
        ast.ListQuestion(query)
    }


  // Examples:
  //   - "who died in 1900"
  //   - "who was born in Europe and died in the US"

  lazy val PersonQuestion: PackratParser[PersonListQuestion] =
    ("who" ~> Properties) ^^ ast.PersonListQuestion


  // Examples:
  //   - "what did George Orwell write"
  //   - "what was authored by George Orwell"

  lazy val ThingQuestion: PackratParser[ThingListQuestion] =
    ("what" ~> Properties) ^^ ast.ThingListQuestion


  lazy val Question: PackratParser[ast.Question] =
    (ListQuestion | PersonQuestion | ThingQuestion) <~ opt(SentenceTerminator)
}
