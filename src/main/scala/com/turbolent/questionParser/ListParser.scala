package com.turbolent.questionParser

object ListParser extends BaseParser {

  // Examples:
  //   - "in which"
  //   - "what"

  lazy val whichWhat =
    ignore(opt(Preposition) ~ ("which" | "what"))


  // Examples:
  //   - "what is"
  //   - "what are"
  //   - "who were"

  lazy val whoWhatBe =
    ignore(("who" | "what") ~ lemma("be"))


  // Examples:
  //   - "find"
  //   - "list"
  //   - "show me"

  lazy val findListGiveShow =
    ignore(("find" | "list" | "give" | "show") ~ opt("me"))


  // Examples:
  //   - "all"
  //   - "some of"
  //   - "a couple"

  lazy val someAllAny =
    ignore((ignore("some" | "all" | "any" | "only" | "many" | "both") |
             ignore(opt("a") ~ ("few" | "couple" | "number" | "lot")))
           ~ opt("of"))


  // Examples:
  //   - "Europe"
  //   - "the US"
  //   - "the 19th century"
  //   - "green paintings"
  //   - "people"

  lazy val NamedValue =
    (opt(pos("DT")) ~ rep(pos("JJ")) ~ Nouns) ^^ {
      case optDeterminer ~ adjectives ~ nouns =>
        optDeterminer map { determiner =>
          ast.NamedValue(determiner :: adjectives ++ nouns)
        } getOrElse {
          ast.NamedValue(adjectives ++ nouns)
        }
    }

  // Examples:
  //   - "100"
  //   - "thousand"
  //   - "1 million"
  //   - "42 meters"
  //   - "two million inhabitants"

  lazy val NumericValue: Parser[ast.Value] =
    (Numbers ~ opt(Nouns)) ^^ {
      case numbers ~ optNouns =>
        optNouns map {
          ast.NumberWithUnit(numbers, _)
        } getOrElse {
            ast.Number(numbers)
        }
    }


  // Examples:
  //   - "Obama's children"
  //   - "Obama's children's mothers"

  lazy val NamedValues: Parser[ast.Value] =
    rep1sep(NamedValue, pos("POS")) ^^
      (_ reduceLeft[ast.Value] { (result, namedValue) =>
        ast.RelationshipValue(namedValue, result)
      })

  // Examples:
  //   - "\"The Red Victorian\""

  lazy val AnyExceptEndQuote =
    rep(elem("not('')", _.pennTag != "''"))

  lazy val Quoted =
    pos("``") ~> AnyExceptEndQuote <~ pos("''")


  // Examples:
  //   - "America"
  //   - "dystopian societies"
  //   - "Obama's children"
  //   - "1900"
  //   - "1000 kilometers"
  //   - "two million inhabitants"
  //   - "\"I, Robot\""

  lazy val Value =
    NamedValues |
    NumericValue |
    (Quoted ^^ ast.NamedValue)


  // Examples:
  //   - "Europe or America"
  //   - "1900 or 1901"
  //   - "Copenhagen and Berlin"

  lazy val Values =
    commaOrAndList(Value, ast.AndValue, ast.OrValue)


  // Examples:
  //   - "before 1900"
  //   - "in Europe"
  //   - "larger than Europe"
  //   - "smaller than Europe and the US"

  lazy val PrepositionExceptOf =
    Preposition filter { _.word != "of" }

  lazy val Filter =
    (opt(opt(pos("JJR")) ~ PrepositionExceptOf) ~ Values) ^^ {
      case optional ~ values =>
        optional map {
          case None ~ preposition =>
            ast.FilterWithModifier(Seq(preposition), values)
          case Some(comparative) ~ preposition =>
            ast.FilterWithComparativeModifier(Seq(comparative, preposition), values)
        } getOrElse {
            ast.PlainFilter(values)
        }
    }


  // Examples:
  //   - "before 1900"
  //   - "before 1900 or after 1910"

  lazy val Filters =
    commaOrAndList(Filter, ast.AndFilter, ast.OrFilter)


  // Examples:
  //   - "died"
  //   - "died before 1900 or after 1910" (NOTE: one property with two filters)
  //   - "by George Orwell"
  //   - "did George Orwell write"
  //   - "did Obama star in"
  // NOTE: first two examples are "direct", second two are "inverse":
  //       "did Orwell write" ~= "were written by" Orwell => "did write Orwell"

  lazy val InversePropertySuffix =
    (Verbs ~ opt(pos("RP") | (Preposition <~ not(NamedValue)))) ^^ { suffix =>
      (verbs: List[Token], filter: ast.Filter) =>
        suffix match {
          case moreVerbs ~ Some(particle) =>
            ast.InversePropertyWithFilter(verbs ++ moreVerbs :+ particle, filter)
          case moreVerbs ~ None =>
            ast.InversePropertyWithFilter(verbs ++ moreVerbs, filter)
        }
    }

  lazy val PropertyAdjectiveSuffix =
    pos("JJ", strict = true) ^^ { adjective =>
      (verbs: List[Token], filter: ast.Filter) =>
        ast.AdjectivePropertyWithFilter(verbs :+ adjective, filter)
    }

  def auxiliaryVerbLemmas =
    Set("have", "be", "do")

  def isAuxiliaryVerb(token: Token): Boolean =
    auxiliaryVerbLemmas.contains(token.lemma)

  lazy val Property =
    opt(pos("WDT")) ~> opt(Verbs) >> {
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

  lazy val Properties =
    commaOrAndList(Property, ast.AndProperty, ast.OrProperty, andOptional = true)


  // Examples:
  //   - "of the USA"
  //   - "of China"

  lazy val Relationship =
    "of" ~ FullQuery


  // Examples:
  //   - "youngest children"
  //   - "the largest cities"
  //   - "main actor"

  // Note: disambiguate/extract superlative and adjective in later stage,
  //       might either be named entity or specific type

  lazy val NamedQuery =
    (opt(pos("DT")) ~ opt(pos("JJS")) ~ rep(pos("JJ")) ~ Nouns) ^^ {
      case None ~ None ~ adjectives ~ nouns =>
        ast.NamedQuery(adjectives ++ nouns)
      case None ~ Some(superlative) ~ adjectives ~ nouns =>
        ast.NamedQuery(superlative :: adjectives ++ nouns)
      case Some(determiner) ~ None ~ adjectives ~ nouns =>
        ast.NamedQuery(determiner :: adjectives ++ nouns)
      case Some(determiner) ~ Some(superlative) ~ adjectives ~ nouns =>
        ast.NamedQuery(determiner :: superlative :: adjectives ++ nouns)
    }


  lazy val Query =
    NamedQuery |
    (Quoted ^^ ast.NamedQuery)


  // Examples:
  //   - "children and grandchildren"
  //   - "China, the USA, and Japan"
  // TODO: differentiate "and" and "or"?!
  // TODO: handling nesting of "and" and "or"

  lazy val QueriesSeparator =
    ("," ~ opt(pos("CC"))) |
    (opt(",") ~ pos("CC"))

  lazy val Queries: Parser[ast.Query] =
    rep1sep(Query, QueriesSeparator) ^^ {
      case Seq(x) => x
      case xs => ast.AndQuery(xs)
    }


  // Examples:
  //   - "Clinton's children"
  //   - "California's cities"
  //   - "California's cities' population sizes"
  //   - "Clinton's children and grandchildren"

  lazy val QueryRelationships: Parser[ast.Query] =
    chainl1(Queries, pos("POS") ^^ { sep =>
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

  lazy val FullQuery: Parser[ast.Query] =
    ((QueryRelationships <~ opt(pos("WDT") | "who")) ~ opt(Properties ||| Relationship)) ^^ {
      case query ~ rest => rest map {
        case property: ast.Property =>
          ast.QueryWithProperty(query, property)
        case ~(sep: Token, nested: ast.Query) =>
          ast.RelationshipQuery(query, nested, sep)
      } getOrElse query
    }


  // Examples:
  //   - "which"
  //   - "what are"
  //   - "find all"
  //   - "give me a few"
  // NOTE: ||| to match as much as possible

  lazy val ListQuestionStart =
    ignore(opt(whichWhat ||| ignore((whoWhatBe ||| findListGiveShow) ~ opt(someAllAny))))


  // Examples:
  //   - "which presidents were born before 1900"
  //   - "give me all actors born in Berlin and San Francisco"

  lazy val ListQuestion =
    (ListQuestionStart ~ FullQuery) ^^ {
      // TODO: handle start
      case start ~ query =>
        ast.ListQuestion(query)
    }


  // Examples:
  //   - "who died in 1900"
  //   - "who was born in Europe and died in the US"

  lazy val PersonQuestion =
    ("who" ~> Properties) ^^ ast.PersonListQuestion


  // Examples:
  //   - "what did George Orwell write"
  //   - "what was authored by George Orwell"

  lazy val ThingQuestion =
    ("what" ~> Properties) ^^ ast.ThingListQuestion


  lazy val Question: Parser[ast.Question] =
    (ListQuestion | PersonQuestion | ThingQuestion) <~ opt(pos("."))
}
