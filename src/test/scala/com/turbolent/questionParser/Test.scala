package com.turbolent.questionParser

// import ai.x.diff._
import com.turbolent.questionParser.ast._
import org.scalatest.{AppendedClues, FunSuite, Matchers}


// scalastyle:off multiple.string.literals


class Test extends FunSuite with Matchers with AppendedClues {

  def parseListQuestion(tokens: Seq[Token]) =
    ListParser.parse(tokens, ListParser.phrase(ListParser.Question))

  def tokenize(taggedSentence: String) =
    taggedSentence.split(" ").map(_.split("/")).map {
      case Array(word, tag, lemma) => Token(word, tag, lemma)
    }

  def assertSuccess(result: ListParser.ParseResult[_]) =
    result shouldBe a [ListParser.Success[_]]

  def test(sentence: String, expected: Question) {
    val tokens = tokenize(sentence)
    val testName = tokens.map(_.word).mkString(" ")

    registerTest(testName) {
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      { result.get shouldEqual expected }
      // TODO: enable, once diff has been released for 2.12
//      withClue {
//        DiffShow.diff(result.get, expected).string
//          .replaceAll("\u001B\\[32m", "++++ {{{")
//          .replaceAll("\u001B\\[31m", "---- {{{")
//          .replaceAll("\u001B\\[0m", "}}}")
//      }
    }
  }


  test("Give/VB/give me/PRP/me all/DT/all musicians/NNS/musician that/WDT/that "
    + "were/VBD/be born/VBN/bear in/IN/in Vienna/NNP/vienna "
    + "and/CC/and died/VBN/die in/IN/in Berlin/NNP/berlin",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("musicians", "NNS", "musician"))),
      AndProperty(List(PropertyWithFilter(List(Token("were", "VBD", "be"), Token("born", "VBN", "bear")),
        FilterWithModifier(List(Token("in", "IN", "in")),
          NamedValue(List(Token("Vienna", "NNP", "vienna"))))),
        PropertyWithFilter(List(Token("died", "VBN", "die")),
          FilterWithModifier(List(Token("in", "IN", "in")),
            NamedValue(List(Token("Berlin", "NNP", "berlin"))))))))))


  test("Which/WDT/which presidents/NNS/president were/VBD/be born/VBN/bear before/IN/before 1900/CD/1900",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("presidents", "NNS", "president"))),
      PropertyWithFilter(List(Token("were", "VBD", "be"), Token("born", "VBN", "bear")),
        FilterWithModifier(List(Token("before", "IN", "before")), Number(List(Token("1900", "CD", "1900"))))))))


  test("Give/VB/give me/PRP/me all/DT/all actors/NNS/actor born/VBN/bear in/IN/in "
    + "Berlin/NNP/berlin or/CC/or San/NNP/san Francisco/NNP/francisco",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("actors", "NNS", "actor"))),
      PropertyWithFilter(List(Token("born", "VBN", "bear")), FilterWithModifier(List(Token("in", "IN", "in")),
        OrValue(List(NamedValue(List(Token("Berlin", "NNP", "berlin"))),
          NamedValue(List(Token("San", "NNP", "san"), Token("Francisco", "NNP", "francisco"))))))))))


  test("List/VB/list books/NNS/book by/IN/by George/NNP/george Orwell/NNP/orwell",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NNS", "book"))),
      PropertyWithFilter(List(),
        FilterWithModifier(List(Token("by", "IN", "by")),
          NamedValue(List(Token("George", "NNP", "george"), Token("Orwell", "NNP", "orwell"))))))))


  test("Which/WDT/which books/NN/book did/VBD/do George/NNP/george Orwell/NNP/orwell write/VB/write",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NN", "book"))),
      InversePropertyWithFilter(List(Token("did", "VBD", "do"), Token("write", "VB", "write")),
        PlainFilter(NamedValue(List(Token("George", "NNP", "george"), Token("Orwell", "NNP", "orwell"))))))))


  test("list/VB/list presidents/NNS/president of/IN/of Argentina/NNP/argentina",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("presidents", "NNS", "president"))),
        NamedQuery(List(Token("Argentina", "NNP", "argentina"))), Token("of", "IN", "of"))))


  test("List/VB/list movies/NNS/movie directed/VBN/direct by/IN/by Quentin/NNP/quentin Tarantino/NNP/tarantino",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("movies", "NNS", "movie"))),
      PropertyWithFilter(List(Token("directed", "VBN", "direct")),
        FilterWithModifier(List(Token("by", "IN", "by")),
          NamedValue(List(Token("Quentin", "NNP", "quentin"), Token("Tarantino", "NNP", "tarantino"))))))))


  test("Which/WDT/which movies/NN/movie did/VBD/do Mel/NNP/mel Gibson/NNP/gibson direct/VB/direct",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("movies", "NN", "movie"))),
      InversePropertyWithFilter(List(Token("did", "VBD", "do"), Token("direct", "VB", "direct")),
        PlainFilter(NamedValue(List(Token("Mel", "NNP", "mel"), Token("Gibson", "NNP", "gibson"))))))))


  test("Which/WDT/which movies/NN/movie did/VBD/do Obama/NNP/obama star/VB/star in/RP/in",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("movies", "NN", "movie"))),
      InversePropertyWithFilter(List(Token("did", "VBD", "do"),
        Token("star", "VB", "star"), Token("in", "RP", "in")),
        PlainFilter(NamedValue(List(Token("Obama", "NNP", "obama"))))))))


  test("In/IN/in what/WDT/what movies/NN/movie did/VBD/do "
    + "Jennifer/NNP/jennifer Aniston/NNP/aniston appear/VB/appear",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("movies", "NN", "movie"))),
      InversePropertyWithFilter(List(Token("did", "VBD", "do"), Token("appear", "VB", "appear")),
        PlainFilter(NamedValue(List(Token("Jennifer", "NNP", "jennifer"), Token("Aniston", "NNP", "aniston"))))))))


  test("Movies/NNP/movie starring/VB/star Winona/NNP/winona Ryder/NNP/ryder",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("Movies", "NNP", "movie"))),
      PropertyWithFilter(List(Token("starring", "VB", "star")),
        PlainFilter(NamedValue(List(Token("Winona", "NNP", "winona"), Token("Ryder", "NNP", "ryder"))))))))


  test("List/VB/list albums/NNS/album of/IN/of Pink/NNP/pink Floyd/NNP/floyd",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("albums", "NNS", "album"))),
      NamedQuery(List(Token("Pink", "NNP", "pink"), Token("Floyd", "NNP", "floyd"))), Token("of", "IN", "of"))))


  test("List/VB/list the/DT/the actors/NNS/actor of/IN/of Titanic/NNP/titanic",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
      Token("actors", "NNS", "actor"))),
      NamedQuery(List(Token("Titanic", "NNP", "titanic"))), Token("of", "IN", "of"))))


  test("who/WP/who are/VBP/be the/DT/the actors/NNS/actor of/IN/of Titanic/NNP/titanic",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
      Token("actors", "NNS", "actor"))),
      NamedQuery(List(Token("Titanic", "NNP", "titanic"))), Token("of", "IN", "of"))))


  test("who/WP/who acted/VBD/act in/IN/in Alien/NNP/alien",

    PersonListQuestion(PropertyWithFilter(List(Token("acted", "VBD", "act")),
      FilterWithModifier(List(Token("in", "IN", "in")), NamedValue(List(Token("Alien", "NNP", "alien")))))))


  test("who/WP/who starred/VBD/star in/IN/in Inception/NNP/inception",

    PersonListQuestion(PropertyWithFilter(List(Token("starred", "VBD", "star")),
      FilterWithModifier(List(Token("in", "IN", "in")),
        NamedValue(List(Token("Inception", "NNP", "inception")))))))


  test("who/WP/who are/VBP/be the/DT/the actors/NNS/actor "
    + "which/WDT/which starred/VBD/star in/IN/in Inception/NNP/inception",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("the", "DT", "the"),
      Token("actors", "NNS", "actor"))),
        PropertyWithFilter(List(Token("starred", "VBD", "star")),
          FilterWithModifier(List(Token("in", "IN", "in")),
            NamedValue(List(Token("Inception", "NNP", "inception"))))))))


  test("Who/WP/who is/VBZ/be the/DT/the director/NN/director of/IN/of Big/NN/big Fish/NN/fish",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
      Token("director", "NN", "director"))),
      NamedQuery(List(Token("Big", "NN", "big"), Token("Fish", "NN", "fish"))), Token("of", "IN", "of"))))


  test("who/WP/who directed/VBD/direct Pocahontas/NNP/pocahontas",

    PersonListQuestion(PropertyWithFilter(List(Token("directed", "VBD", "direct")),
      PlainFilter(NamedValue(List(Token("Pocahontas", "NNP", "pocahontas")))))))


  test("Which/WDT/which city/NN/city is/VBZ/be bigger/JJR/big than/IN/than New/NNP/new York/NNP/york City/NNP/city",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("city", "NN", "city"))),
      PropertyWithFilter(List(Token("is", "VBZ", "be")),
        FilterWithComparativeModifier(List(Token("bigger", "JJR", "big"), Token("than", "IN", "than")),
          NamedValue(List(Token("New", "NNP", "new"), Token("York", "NNP", "york"),
            Token("City", "NNP", "city"))))))))


  test("What/WP/what are/VBP/be the/DT/the members/NNS/member of/IN/of Metallica/NNP/metallica",

      ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
          Token("members", "NNS", "member"))),
        NamedQuery(List(Token("Metallica", "NNP", "metallica"))), Token("of", "IN", "of"))))


  test("members/NNS/member of/IN/of Metallica/NNP/metallica",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("members", "NNS", "member"))),
      NamedQuery(List(Token("Metallica", "NNP", "metallica"))), Token("of", "IN", "of"))))


  test("What/WP/what is/VBZ/be the/DT/the music/NN/music genre/NN/genre of/IN/of Gorillaz/NNP/gorillaz",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
      Token("music", "NN", "music"), Token("genre", "NN", "genre"))),
      NamedQuery(List(Token("Gorillaz", "NNP", "gorillaz"))), Token("of", "IN", "of"))))


  test("actors/NNP/actor",

    ListQuestion(NamedQuery(List(Token("actors", "NNP", "actor")))))


  test("Radiohead/NNS/radiohead members/NNP/member",

    ListQuestion(NamedQuery(List(Token("Radiohead", "NNS", "radiohead"),
      Token("members", "NNP", "member")))))


  test("What/WP/what is/VBZ/be the/DT/the cast/NN/cast of/IN/of Friends/NNS/friend",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"), Token("cast", "NN", "cast"))),
      NamedQuery(List(Token("Friends", "NNS", "friend"))), Token("of", "IN", "of"))))


  test("Who/WP/who works/VBZ/work in/IN/in Breaking/NNS/break Bad/NNS/bad",

    PersonListQuestion(PropertyWithFilter(List(Token("works", "VBZ", "work")),
      FilterWithModifier(List(Token("in", "IN", "in")),
        NamedValue(List(Token("Breaking", "NNS", "break"), Token("Bad", "NNS", "bad")))))))


  test("what/WDT/what languages/NNS/language are/VBP/be spoken/VBN/speak in/IN/in Switzerland/NNP/switzerland",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("languages", "NNS", "language"))),
      PropertyWithFilter(List(Token("are", "VBP", "be"), Token("spoken", "VBN", "speak")),
        FilterWithModifier(List(Token("in", "IN", "in")),
          NamedValue(List(Token("Switzerland", "NNP", "switzerland"))))))))


  test("Which/WDT/which cities/NNS/city have/VBP/have more/JJR/more than/IN/than "
    + "two/CD/two million/CD/million inhabitants/NNS/inhabitant",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("cities", "NNS", "city"))),
      PropertyWithFilter(List(Token("have", "VBP", "have")),
        FilterWithComparativeModifier(List(Token("more", "JJR", "more"), Token("than", "IN", "than")),
          NumberWithUnit(List(Token("two", "CD", "two"), Token("million", "CD", "million")),
            List(Token("inhabitants", "NNS", "inhabitant"))))))))


  test("What/WP/what films/NNS/film featured/VBD/feature "
    + "the/DT/the character/NN/character Popeye/NNP/popeye Doyle/NNP/doyle",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("films", "NNS", "film"))),
      PropertyWithFilter(List(Token("featured", "VBD", "feature")),
        PlainFilter(NamedValue(List(Token("the", "DT", "the"), Token("character", "NN", "character"),
          Token("Popeye", "NNP", "popeye"), Token("Doyle", "NNP", "doyle"))))))))


  test("Who/WP/who is/VBZ/be taller/JJR/tall than/IN/than Obama/NNP/obama",

    PersonListQuestion(PropertyWithFilter(List(Token("is", "VBZ", "be")),
      FilterWithComparativeModifier(List(Token("taller", "JJR", "tall"), Token("than", "IN", "than")),
        NamedValue(List(Token("Obama", "NNP", "obama")))))))


  test("Who/WP/who lived/VBD/live in/IN/in Berlin/NNP/berlin and/CC/and Copenhagen/NNP/copenhagen "
    + "or/CC/or New/NNP/new York/NNP/york City/NNP/city",

    PersonListQuestion(PropertyWithFilter(List(Token("lived", "VBD", "live")),
      FilterWithModifier(List(Token("in", "IN", "in")),
        OrValue(List(AndValue(List(NamedValue(List(Token("Berlin", "NNP", "berlin"))),
          NamedValue(List(Token("Copenhagen", "NNP", "copenhagen"))))),
          NamedValue(List(Token("New", "NNP", "new"), Token("York", "NNP", "york"),
            Token("City", "NNP", "city")))))))))


  test("which/WDT/which books/NNS/book written/VBN/write by/IN/by Orwell/NNP/orwell are/VBP/be about/IN/about "
    + "dystopian/JJ/dystopian societies/NNS/society",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NNS", "book"))),
      AndProperty(List(PropertyWithFilter(List(Token("written", "VBN", "write")),
        FilterWithModifier(List(Token("by", "IN", "by")), NamedValue(List(Token("Orwell", "NNP", "orwell"))))),
        PropertyWithFilter(List(Token("are", "VBP", "be")),
          FilterWithModifier(List(Token("about", "IN", "about")),
            NamedValue(List(Token("dystopian", "JJ", "dystopian"), Token("societies", "NNS", "society"))))))))))


  test("Which/WDT/which books/NN/book did/VBD/do"
    + " Orwell/NNP/orwell or/CC/or Shakespeare/NNP/shakespeare write/VB/write",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NN", "book"))),
      InversePropertyWithFilter(List(Token("did", "VBD", "do"), Token("write", "VB", "write")),
        PlainFilter(OrValue(List(NamedValue(List(Token("Orwell", "NNP", "orwell"))),
          NamedValue(List(Token("Shakespeare", "NNP", "shakespeare"))))))))))


  test("who/WP/who are/VBP/be the/DT/the children/NNS/child of/IN/of the/DT/the presidents/NNS/president",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
      Token("children", "NNS", "child"))),
      NamedQuery(List(Token("the", "DT", "the"), Token("presidents", "NNS", "president"))),
      Token("of", "IN", "of"))))


  test("what/WP/what are/VBP/be the/DT/the largest/JJS/large cities/NNS/city in/IN/in europe/NN/europe",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("the", "DT", "the"),
        Token("largest", "JJS", "large"), Token("cities", "NNS", "city"))),
      PropertyWithFilter(List(),
        FilterWithModifier(List(Token("in", "IN", "in")),
          NamedValue(List(Token("europe", "NN", "europe"))))))))


  test("what/WP/what are/VBP/be the/DT/the population/NN/population sizes/NNS/size of/IN/of cities/NNS/city "
    + "located/VBN/locate in/IN/in california/NN/california",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
      Token("population", "NN", "population"),
      Token("sizes", "NNS", "size"))),
      QueryWithProperty(NamedQuery(List(Token("cities", "NNS", "city"))),
        PropertyWithFilter(List(Token("located", "VBN", "locate")),
          FilterWithModifier(List(Token("in", "IN", "in")),
            NamedValue(List(Token("california", "NN", "california")))))),
      Token("of", "IN", "of"))))


  test("what/WP/what are/VBP/be the/DT/the population/NN/population sizes/NNS/size of/IN/of cities/NNS/city "
    + "that/WDT/that are/VBP/be located/VBN/locate in/IN/in california/NN/california",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
      Token("population", "NN", "population"),
      Token("sizes", "NNS", "size"))),
      QueryWithProperty(NamedQuery(List(Token("cities", "NNS", "city"))),
        PropertyWithFilter(List(Token("are", "VBP", "be"), Token("located", "VBN", "locate")),
          FilterWithModifier(List(Token("in", "IN", "in")),
            NamedValue(List(Token("california", "NN", "california")))))),
      Token("of", "IN", "of"))))


  test("Who/WP/who are/VBP/be the/DT/the children/NNS/child "
     + "of/IN/of the/DT/the children/NNS/child "
     + "of/IN/of Bill/NNP/bill Clinton/NNP/clinton",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
      Token("children", "NNS", "child"))),
      RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"), Token("children", "NNS", "child"))),
        NamedQuery(List(Token("Bill", "NNP", "bill"), Token("Clinton", "NNP", "clinton"))),
        Token("of", "IN", "of")),
      Token("of", "IN", "of"))))


  test("What/WP/what are/VBP/be the/DT/the largest/JJS/large cities/NNS/city of/IN/of California/NNP/california",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
        Token("largest", "JJS", "large"), Token("cities", "NNS", "city"))),
      NamedQuery(List(Token("California", "NNP", "california"))),
      Token("of", "IN", "of"))))


  test("What/WP/what are/VBP/be the/DT/the biggest/JJS/big cities/NNS/city of/IN/of California/NNP/california",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
      Token("biggest", "JJS", "big"), Token("cities", "NNS", "city"))),
      NamedQuery(List(Token("California", "NNP", "california"))),
      Token("of", "IN", "of"))))


  test("Who/WP/who did/VBD/do Bill/NNP/bill Clinton/NNP/clinton marry/VB/marry",

    PersonListQuestion(InversePropertyWithFilter(List(Token("did", "VBD", "do"),
      Token("marry", "VB", "marry")),
      PlainFilter(NamedValue(List(Token("Bill", "NNP", "bill"), Token("Clinton", "NNP", "clinton")))))))


  test("Clinton/NNP/clinton 's/POS/'s daughters/NN/daughter",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("daughters", "NN", "daughter"))),
      NamedQuery(List(Token("Clinton", "NNP", "clinton"))),
      Token("'s", "POS", "'s"))))


  test("What/WP/what are/VBP/be California/NNP/california 's/POS/'s biggest/JJS/big cities/NNS/city",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("biggest", "JJS", "big"),
      Token("cities", "NNS", "city"))),
      NamedQuery(List(Token("California", "NNP", "california"))),
      Token("'s", "POS", "'s"))))


  test("Who/WP/who is/VBZ/be Bill/NNP/bill Clinton/NNP/clinton 's/POS/'s daughter/NN/daughter",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("daughter", "NN", "daughter"))),
      NamedQuery(List(Token("Bill", "NNP", "bill"), Token("Clinton", "NNP", "clinton"))),
      Token("'s", "POS", "'s"))))


  test("Who/WP/who is/VBZ/be Bill/NNP/bill Clinton/NNP/clinton 's/POS/'s daughter/NN/daughter 's/POS/'s "
    + "husband/NN/husband 's/POS/'s daughter/NN/daughter 's/POS/'s grandfather/NN/grandfather",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("grandfather", "NN", "grandfather"))),
      RelationshipQuery(NamedQuery(List(Token("daughter", "NN", "daughter"))),
        RelationshipQuery(NamedQuery(List(Token("husband", "NN", "husband"))),
          RelationshipQuery(NamedQuery(List(Token("daughter", "NN", "daughter"))),
            NamedQuery(List(Token("Bill", "NNP", "bill"), Token("Clinton", "NNP", "clinton"))),
            Token("'s", "POS", "'s")),
          Token("'s", "POS", "'s")),
        Token("'s", "POS", "'s")),
      Token("'s", "POS", "'s"))))


  test("presidents/NNS/president that/WDT/that have/VBP/have children/NNS/child",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("presidents", "NNS", "president"))),
      PropertyWithFilter(List(Token("have", "VBP", "have")),
        PlainFilter(NamedValue(List(Token("children", "NNS", "child"))))))))


  test("Who/WP/who did/VBD/do Bill/NNP/bill Clinton/NNP/clinton 's/POS/'s daughter/NN/daughter marry/VB/marry",

    PersonListQuestion(InversePropertyWithFilter(List(Token("did", "VBD", "do"), Token("marry", "VB", "marry")),
      PlainFilter(RelationshipValue(NamedValue(List(Token("daughter", "NN", "daughter"))),
        NamedValue(List(Token("Bill", "NNP", "bill"), Token("Clinton", "NNP", "clinton"))))))))


  test("In/IN/in which/WDT/which californian/JJS/californian cities/NNS/city "
    + "live/VBP/live more/JJR/more than/IN/than 2/CD/2 million/CD/million people/NNS/people",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("californian", "JJS", "californian"),
      Token("cities", "NNS", "city"))),
      PropertyWithFilter(List(Token("live", "VBP", "live")),
        FilterWithComparativeModifier(List(Token("more", "JJR", "more"), Token("than", "IN", "than")),
          NumberWithUnit(List(Token("2", "CD", "2"), Token("million", "CD", "million")),
            List(Token("people", "NNS", "people"))))))))


  test("the/DT/the population/NNP/population of/IN/of Japan/NNP/japan before/IN/before 1900/CD/1900",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
      Token("population", "NNP", "population"))),
      QueryWithProperty(NamedQuery(List(Token("Japan", "NNP", "japan"))),
        PropertyWithFilter(List(),
          FilterWithModifier(List(Token("before", "IN", "before")),
            Number(List(Token("1900", "CD", "1900")))))),
      Token("of", "IN", "of"))))


  test("What/WP/what are/VBP/be the/DT/the population/NN/population "
    + "of/IN/of China/NNP/china and/CC/and the/DT/the USA/NNP/usa",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
      Token("population", "NN", "population"))),
      AndQuery(List(NamedQuery(List(Token("China", "NNP", "china"))),
        NamedQuery(List(Token("the", "DT", "the"), Token("USA", "NNP", "usa"))))),
      Token("of", "IN", "of"))))


  test("the/DT/the population/NNP/population of/IN/of Japan/NNP/japan and/CC/and China/NNP/china",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
      Token("population", "NNP", "population"))),
      AndQuery(List(NamedQuery(List(Token("Japan", "NNP", "japan"))),
        NamedQuery(List(Token("China", "NNP", "china"))))),
      Token("of", "IN", "of"))))


  test("the/DT/the population/NNP/population of/IN/of Japan/NNP/japan "
    + "and/CC/and China/NNP/china before/IN/before 1900/CD/1900",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
        Token("population", "NNP", "population"))),
      QueryWithProperty(AndQuery(List(NamedQuery(List(Token("Japan", "NNP", "japan"))),
        NamedQuery(List(Token("China", "NNP", "china"))))),
        PropertyWithFilter(List(),
          FilterWithModifier(List(Token("before", "IN", "before")),
            Number(List(Token("1900", "CD", "1900")))))),
      Token("of", "IN", "of"))))


  test("the/DT/the population/NNP/population and/CC/and area/NNP/area"
    + " of/IN/of Japan/NNP/japan and/CC/and China/NNP/china",

    ListQuestion(RelationshipQuery(AndQuery(List(NamedQuery(List(Token("the", "DT", "the"),
        Token("population", "NNP", "population"))),
      NamedQuery(List(Token("area", "NNP", "area"))))),
      AndQuery(List(NamedQuery(List(Token("Japan", "NNP", "japan"))),
        NamedQuery(List(Token("China", "NNP", "china"))))),
      Token("of", "IN", "of"))))


  test("the/DT/the population/NN/population ,/,/, land/NN/land area/NN/area and/CC/and capitals/NNP/capital "
    + "of/IN/of Japan/NNP/japan ,/,/, India/NNP/india and/CC/and China/NNP/china",

    ListQuestion(RelationshipQuery(AndQuery(List(NamedQuery(List(Token("the", "DT", "the"),
        Token("population", "NN", "population"))),
      NamedQuery(List(Token("land", "NN", "land"), Token("area", "NN", "area"))),
      NamedQuery(List(Token("capitals", "NNP", "capital"))))),
      AndQuery(List(NamedQuery(List(Token("Japan", "NNP", "japan"))),
        NamedQuery(List(Token("India", "NNP", "india"))),
        NamedQuery(List(Token("China", "NNP", "china"))))),
      Token("of", "IN", "of"))))


  test("children/NNS/child of/IN/of all/DT/all presidents/NNS/president of/IN/of the/DT/the US/NNP/us",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("children", "NNS", "child"))),
      RelationshipQuery(NamedQuery(List(Token("all", "DT", "all"), Token("presidents", "NNS", "president"))),
        NamedQuery(List(Token("the", "DT", "the"), Token("US", "NNP", "us"))),
        Token("of", "IN", "of")),
      Token("of", "IN", "of"))))


  test("Clinton/NNP/clinton 's/POS/'s children/NNS/child and/CC/and grandchildren/NNS/grandchild",

    ListQuestion(RelationshipQuery(AndQuery(List(NamedQuery(List(Token("children", "NNS", "child"))),
      NamedQuery(List(Token("grandchildren", "NNS", "grandchild"))))),
      NamedQuery(List(Token("Clinton", "NNP", "clinton"))),
      Token("'s", "POS", "'s"))))


  test("Japan/NNP/japan and/CC/and China/NNP/china 's/POS/'s population/NNP/population and/CC/and area/NNP/area",

    ListQuestion(RelationshipQuery(AndQuery(List(NamedQuery(List(Token("population", "NNP", "population"))),
      NamedQuery(List(Token("area", "NNP", "area"))))),
      AndQuery(List(NamedQuery(List(Token("Japan", "NNP", "japan"))),
        NamedQuery(List(Token("China", "NNP", "china"))))),
      Token("'s", "POS", "'s"))))


  test("which/WDT/which books/NNS/book were/VBD/be written/VBN/write by/IN/by Orwell/NNP/orwell "
    + "and/CC/and are/VBP/be about/IN/about dystopian/JJ/dystopian societies/NNS/society",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NNS", "book"))),
      AndProperty(List(PropertyWithFilter(List(Token("were", "VBD", "be"), Token("written", "VBN", "write")),
        FilterWithModifier(List(Token("by", "IN", "by")),
          NamedValue(List(Token("Orwell", "NNP", "orwell"))))),
        PropertyWithFilter(List(Token("are", "VBP", "be")),
          FilterWithModifier(List(Token("about", "IN", "about")),
            NamedValue(List(Token("dystopian", "JJ", "dystopian"), Token("societies", "NNS", "society"))))))))))


  test("Who/WP/who is/VBZ/be the/DT/the president/NN/president of/IN/of France/NNP/france",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
      Token("president", "NN", "president"))),
      NamedQuery(List(Token("France", "NNP", "france"))),
      Token("of", "IN", "of"))))


  test("Who/WP/who are/VBP/be the/DT/the daughters/NNS/daughter of/IN/of the/DT/the wife/NN/wife of/IN/of "
     + "the/DT/the president/NN/president of/IN/of the/DT/the United/NNP/united States/NNPS/state",

      ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
        Token("daughters", "NNS", "daughter"))),
        RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"), Token("wife", "NN", "wife"))),
          RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"), Token("president", "NN", "president"))),
            NamedQuery(List(Token("the", "DT", "the"),
              Token("United", "NNP", "united"), Token("States", "NNPS", "state"))),
            Token("of", "IN", "of")),
          Token("of", "IN", "of")),
        Token("of", "IN", "of"))))


  test("which/WDT/which books/NN/book were/VBD/be authored/VBN/author by/IN/by George/NNP/george Orwell/NNP/orwell",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NN", "book"))),
      PropertyWithFilter(List(Token("were", "VBD", "be"), Token("authored", "VBN", "author")),
        FilterWithModifier(List(Token("by", "IN", "by")),
          NamedValue(List(Token("George", "NNP", "george"), Token("Orwell", "NNP", "orwell"))))))))


  test("What/WDT/what actor/NN/actor married/VBD/marry"
    + " John/NNP/john F./NNP/f. Kennedy/NNP/kennedy 's/POS/'s sister/NN/sister",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("actor", "NN", "actor"))),
      PropertyWithFilter(List(Token("married", "VBD", "marry")),
        PlainFilter(RelationshipValue(NamedValue(List(Token("sister", "NN", "sister"))),
          NamedValue(List(Token("John", "NNP", "john"), Token("F.", "NNP", "f."),
            Token("Kennedy", "NNP", "kennedy")))))))))


  test("which/WDT/which instrument/NN/instrument did/VBD/do John/NNP/john Lennon/NNP/lennon play/VB/play",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("instrument", "NN", "instrument"))),
      InversePropertyWithFilter(List(Token("did", "VBD", "do"), Token("play", "VB", "play")),
        PlainFilter(NamedValue(List(Token("John", "NNP", "john"), Token("Lennon", "NNP", "lennon"))))))))


  test("which/WDT/which poets/NNS/poet lived/VBD/live in/IN/in the/DT/the 19th/JJ/19th century/NN/century",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("poets", "NNS", "poet"))),
      PropertyWithFilter(List(Token("lived", "VBD", "live")),
        FilterWithModifier(List(Token("in", "IN", "in")),
          NamedValue(List(Token("the", "DT", "the"), Token("19th", "JJ", "19th"),
            Token("century", "NN", "century"))))))))


  test("Who/WP/who wrote/VBD/write \"/``/\" Le/NNP/le Petit/NNP/petit Prince/NNP/prince \"/''/\" "
    + "and/CC/and \"/``/\" Vol/NNP/vol de/NNP/de Nuit/NNP/nuit \"/''/\"",

    PersonListQuestion(PropertyWithFilter(List(Token("wrote", "VBD", "write")),
      PlainFilter(AndValue(List(NamedValue(List(Token("Le", "NNP", "le"),
        Token("Petit", "NNP", "petit"), Token("Prince", "NNP", "prince"))),
        NamedValue(List(Token("Vol", "NNP", "vol"), Token("de", "NNP", "de"), Token("Nuit", "NNP", "nuit")))))))))


  test("Who/WP/who is/VBZ/be the/DT/the son/NN/son of/IN/of the/DT/the main/JJ/main actor/NN/actor "
    + "of/IN/of \"/``/\" I/PRP/i ,/,/, Robot/NNP/robot \"/''/\"",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"), Token("son", "NN", "son"))),
      RelationshipQuery(NamedQuery(List(Token("the", "DT", "the"),
        Token("main", "JJ", "main"), Token("actor", "NN", "actor"))),
        NamedQuery(List(Token("I", "PRP", "i"), Token(",", ",", ","), Token("Robot", "NNP", "robot"))),
        Token("of", "IN", "of")),
      Token("of", "IN", "of"))))


  test("What/WP/what did/VBD/do George/NNP/george Orwell/NNP/orwell write/VB/write",

    ThingListQuestion(InversePropertyWithFilter(List(Token("did", "VBD", "do"),
      Token("write", "VB", "write")),
      PlainFilter(NamedValue(List(Token("George", "NNP", "george"), Token("Orwell", "NNP", "orwell")))))))


  test("What/WP/what was/VBD/be authored/VBN/author by/IN/by George/NNP/george Orwell/NNP/orwell",

    ThingListQuestion(PropertyWithFilter(List(Token("was", "VBD", "be"), Token("authored", "VBN", "author")),
      FilterWithModifier(List(Token("by", "IN", "by")),
        NamedValue(List(Token("George", "NNP", "george"), Token("Orwell", "NNP", "orwell")))))))


  test("What/WP/what books/NNP/book were/VBD/be authored/VBN/author by/IN/by George/NNP/george Orwell/NNP/orwell",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NNP", "book"))),
      PropertyWithFilter(List(Token("were", "VBD", "be"), Token("authored", "VBN", "author")),
        FilterWithModifier(List(Token("by", "IN", "by")),
          NamedValue(List(Token("George", "NNP", "george"), Token("Orwell", "NNP", "orwell"))))))))


  test("children/NNS/child and/CC/and grand/JJ/grand children/NNS/child of/IN/of Clinton/NNP/clinton",

    ListQuestion(RelationshipQuery(AndQuery(List(NamedQuery(List(Token("children", "NNS", "child"))),
      NamedQuery(List(Token("grand", "JJ", "grand"), Token("children", "NNS", "child"))))),
      NamedQuery(List(Token("Clinton", "NNP", "clinton"))),
      Token("of", "IN", "of"))))


  test("which/WDT/which books/NN/book did/VBD/do Orwell/NNP/orwell write/VB/write "
    + "before/IN/before the/DT/the world/NN/world war/NN/war",

    // TODO: handle in second stage:
    //       empty property with modifying filter is constraining preceding property

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NN", "book"))),
      AndProperty(List(InversePropertyWithFilter(List(Token("did", "VBD", "do"),
        Token("write", "VB", "write")),
        PlainFilter(NamedValue(List(Token("Orwell", "NNP", "orwell"))))),
        PropertyWithFilter(List(),
          FilterWithModifier(List(Token("before", "IN", "before")),
            NamedValue(List(Token("the", "DT", "the"), Token("world", "NN", "world"), Token("war", "NN", "war"))))))))))


  test("What/WP/what are/VBP/be cities/NNS/city which/WDT/which have/VBP/have a/DT/a population/NN/population "
    + "larger/JJR/large than/IN/than 1/CD/1 million/CD/million",

    // TODO: handle in second stage:
    //       filter of empty second property is constraining preceding property
    //       (empty property is shortened from ~"which is")

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("cities", "NNS", "city"))),
      AndProperty(List(PropertyWithFilter(List(Token("have", "VBP", "have")),
        PlainFilter(NamedValue(List(Token("a", "DT", "a"), Token("population", "NN", "population"))))),
        PropertyWithFilter(List(),
          FilterWithComparativeModifier(List(Token("larger", "JJR", "large"), Token("than", "IN", "than")),
            Number(List(Token("1", "CD", "1"), Token("million", "CD", "million"))))))))))


  test("which/WDT/which cities/NNS/city have/VBP/have a/DT/a population/NN/population"
    + " larger/JJR/large than/IN/than 1000/CD/1000",

    // TODO: handle in second stage:
    //       filter of empty second property is constraining preceding property
    //       (empty property is shortened from ~"which is")

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("cities", "NNS", "city"))),
      AndProperty(List(PropertyWithFilter(List(Token("have", "VBP", "have")),
        PlainFilter(NamedValue(List(Token("a", "DT", "a"), Token("population", "NN", "population"))))),
        PropertyWithFilter(List(),
          FilterWithComparativeModifier(List(Token("larger", "JJR", "large"), Token("than", "IN", "than")),
            Number(List(Token("1000", "CD", "1000"))))))))))


  test("cities/NNS/city which/WDT/which have/VBP/have a/DT/a population/NN/population "
    + "that/WDT/that is/VBZ/be larger/JJR/large than/IN/than 1000/CD/1000",

    // TODO: handle in second stage:
    //       filter of second property with single "be" lemma is constraining preceding property

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("cities", "NNS", "city"))),
      AndProperty(List(PropertyWithFilter(List(Token("have", "VBP", "have")),
        PlainFilter(NamedValue(List(Token("a", "DT", "a"), Token("population", "NN", "population"))))),
        PropertyWithFilter(List(Token("is", "VBZ", "be")),
          FilterWithComparativeModifier(List(Token("larger", "JJR", "large"), Token("than", "IN", "than")),
            Number(List(Token("1000", "CD", "1000"))))))))))


  test("cities/NNS/city which/WDT/which have/VBP/have a/DT/a population/NN/population "
    + "larger/JJR/large than/IN/than 1000/CD/1000",

    // TODO: handle in second stage:
    //       filter of empty second property is constraining preceding property
    //       (empty property is shortened from ~"which is")

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("cities", "NNS", "city"))),
      AndProperty(List(PropertyWithFilter(List(Token("have", "VBP", "have")),
        PlainFilter(NamedValue(List(Token("a", "DT", "a"), Token("population", "NN", "population"))))),
        PropertyWithFilter(List(),
          FilterWithComparativeModifier(List(Token("larger", "JJR", "large"), Token("than", "IN", "than")),
            Number(List(Token("1000", "CD", "1000"))))))))))


  test("What/WP/what are/VBP/be California/NNP/california 's/POS/'s cities/NNS/city "
    + "which/WDT/which have/VBP/have a/DT/a population/NN/population "
    + "larger/JJR/large than/IN/than 1/CD/1 million/CD/million",

    // TODO: handle in second stage:
    //       filter of empty second property is constraining preceding property
    //       (empty property is shortened from ~"which is")

    ListQuestion(QueryWithProperty(RelationshipQuery(NamedQuery(List(Token("cities", "NNS", "city"))),
      NamedQuery(List(Token("California", "NNP", "california"))), Token("'s", "POS", "'s")),
      AndProperty(List(PropertyWithFilter(List(Token("have", "VBP", "have")),
        PlainFilter(NamedValue(List(Token("a", "DT", "a"), Token("population", "NN", "population"))))),
        PropertyWithFilter(List(),
          FilterWithComparativeModifier(List(Token("larger", "JJR", "large"), Token("than", "IN", "than")),
            Number(List(Token("1", "CD", "1"), Token("million", "CD", "million"))))))))))


  test("which/WDT/which cities/NNS/city in/IN/in California/NNP/california are/VBP/be larger/JJR/large than/IN/than "
    + "cities/NNS/city in/IN/in Germany/NNP/germany or/CC/or in/IN/in France/NNP/france",

    // TODO: handle in second stage:
    //       filter of empty last property is constraining preceding property
    //       (empty property is shortened from ~"which are located in")

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("cities", "NNS", "city"))),
      AndProperty(List(PropertyWithFilter(List(),
        FilterWithModifier(List(Token("in", "IN", "in")),
          NamedValue(List(Token("California", "NNP", "california"))))),
        PropertyWithFilter(List(Token("are", "VBP", "be")),
          FilterWithComparativeModifier(List(Token("larger", "JJR", "large"),
            Token("than", "IN", "than")),
            NamedValue(List(Token("cities", "NNS", "city"))))),
        PropertyWithFilter(List(),
          OrFilter(List(FilterWithModifier(List(Token("in", "IN", "in")),
            NamedValue(List(Token("Germany", "NNP", "germany")))),
            FilterWithModifier(List(Token("in", "IN", "in")),
              NamedValue(List(Token("France", "NNP", "france"))))))))))))


  test("Who/WP/who composed/VBN/compose the/DT/the music/NN/music "
    + "for/IN/for Schindler/NNP/schindler 's/POS/'s List/NN/list",

    // TODO: "Schindler's List" should be detected as one name, not as a possessive:
    //       also run NER for possessives
    // TODO: handle in second stage:
    //       filter of empty second property is constraining preceding property

    PersonListQuestion(AndProperty(List(PropertyWithFilter(List(Token("composed", "VBN", "compose")),
      PlainFilter(NamedValue(List(Token("the", "DT", "the"), Token("music", "NN", "music"))))),
      PropertyWithFilter(List(),
        FilterWithModifier(List(Token("for", "IN", "for")),
          RelationshipValue(NamedValue(List(Token("List", "NN", "list"))),
            NamedValue(List(Token("Schindler", "NNP", "schindler"))))))))))


  test("which/WDT/which cities/NNS/city in/IN/in California/NNP/california are/VBP/be larger/JJR/large than/IN/than "
    + "cities/NNS/city which/WDT/which are/VBD/be located/VBD/locate in/IN/in Germany/NNP/germany",

    // TODO: handle in second stage:
    //       filter of second property is constraining preceding property with comparative filter

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("cities", "NNS", "city"))),
      AndProperty(List(PropertyWithFilter(List(),
        FilterWithModifier(List(Token("in", "IN", "in")),
          NamedValue(List(Token("California", "NNP", "california"))))),
        PropertyWithFilter(List(Token("are", "VBP", "be")),
          FilterWithComparativeModifier(List(Token("larger", "JJR", "large"), Token("than", "IN", "than")),
            NamedValue(List(Token("cities", "NNS", "city"))))),
        PropertyWithFilter(List(Token("are", "VBD", "be"), Token("located", "VBD", "locate")),
          FilterWithModifier(List(Token("in", "IN", "in")),
            NamedValue(List(Token("Germany", "NNP", "germany"))))))))))


  test("authors/NNS/author who/WP/who died/VBD/die",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("authors", "NNS", "author"))),
      NamedProperty(List(Token("died", "VBD", "die"))))))


  test("authors/NNS/author which/WDT/which died/VBD/die in/IN/in Berlin/NNP/berlin",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("authors", "NNS", "author"))),
      PropertyWithFilter(List(Token("died", "VBD", "die")),
        FilterWithModifier(List(Token("in", "IN", "in")),
          NamedValue(List(Token("Berlin", "NNP", "berlin"))))))))


  test("which/WDT/which mountains/NNS/mountain are/VBP/be 1000/CD/1000 meters/NNS/meter high/JJ/high",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("mountains", "NNS", "mountain"))),
      AdjectivePropertyWithFilter(List(Token("are", "VBP", "be"), Token("high", "JJ", "high")),
        PlainFilter(NumberWithUnit(List(Token("1000", "CD", "1000")), List(Token("meters", "NNS", "meter"))))))))


  test("which/WDT/which mountains/NNS/mountain are/VBP/be "
    + "more/JJR/more than/IN/than 1000/CD/1000 meters/NNS/meter high/JJ/high",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("mountains", "NNS", "mountain"))),
      AdjectivePropertyWithFilter(List(Token("are", "VBP", "be"), Token("high", "JJ", "high")),
        FilterWithComparativeModifier(List(Token("more", "JJR", "more"), Token("than", "IN", "than")),
          NumberWithUnit(List(Token("1000", "CD", "1000")), List(Token("meters", "NNS", "meter"))))))))


  test("who/WP/who starred/VBD/star in/IN/in movies/NNS/movie "
    + "directed/VBN/direct by/IN/by Christopher/NN/christopher Nolan/NN/nolan",

    // NOTE: AndProperty is correct, next stage should realize second property
    //       is relative to first one, not subject

    PersonListQuestion(AndProperty(List(PropertyWithFilter(List(Token("starred", "VBD", "star")),
      FilterWithModifier(List(Token("in", "IN", "in")),
        NamedValue(List(Token("movies", "NNS", "movie"))))),
      PropertyWithFilter(List(Token("directed", "VBN", "direct")),
        FilterWithModifier(List(Token("by", "IN", "by")),
          NamedValue(List(Token("Christopher", "NN", "christopher"), Token("Nolan", "NN", "nolan")))))))))


  test("which/WDT/which country/NN/country was/VBD/be Obama/NNP/obama born/VBN/bear in/IN/in",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("country", "NN", "country"))),
      InversePropertyWithFilter(List(Token("was", "VBD", "be"), Token("born", "VBN", "bear"),
        Token("in", "IN", "in")),
        PlainFilter(NamedValue(List(Token("Obama", "NNP", "obama"))))))))


  test("which/WDT/which country/NN/country was/VBD/be Obama/NNP/obama born/VBN/bear in/IN/in in/IN/in 1961/CD/1961",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("country", "NN", "country"))),
      AndProperty(List(InversePropertyWithFilter(List(Token("was", "VBD", "be"),
        Token("born", "VBN", "bear"), Token("in", "IN", "in")),
        PlainFilter(NamedValue(List(Token("Obama", "NNP", "obama"))))),
        PropertyWithFilter(List(), FilterWithModifier(List(Token("in", "IN", "in")),
          Number(List(Token("1961", "CD", "1961"))))))))))


  test("What/WP/what are/VBP/be some/DT/some of/IN/of Seth/NNP/seth Gabel/NNP/gabel 's/POS/'s "
    + "father-in-law/NN/father-in-law 's/POS/'s movies/NNS/movie",

    ListQuestion(RelationshipQuery(NamedQuery(List(Token("movies", "NNS", "movie"))),
      RelationshipQuery(NamedQuery(List(Token("father-in-law", "NN", "father-in-law"))),
        NamedQuery(List(Token("Seth", "NNP", "seth"), Token("Gabel", "NNP", "gabel"))),
        Token("'s", "POS", "'s")),
      Token("'s", "POS", "'s"))))


  test("Who/WP/who lived/VBD/live in/IN/in Berlin/NNP/berlin ,/,/, Copenhagen/NNP/copenhagen ,/,/, "
    + "or/CC/or New/NNP/new York/NNP/york City/NNP/city",

    PersonListQuestion(PropertyWithFilter(List(Token("lived", "VBD", "live")),
      FilterWithModifier(List(Token("in", "IN", "in")),
        OrValue(List(NamedValue(List(Token("Berlin", "NNP", "berlin"))),
          NamedValue(List(Token("Copenhagen", "NNP", "copenhagen"))),
          NamedValue(List(Token("New", "NNP", "new"), Token("York", "NNP", "york"), Token("City", "NNP", "city")))))))))


  test("Who/WP/who lived/VBD/live in/IN/in Berlin/NNP/berlin ,/,/, Copenhagen/NNP/copenhagen ,/,/, "
    + "and/CC/and New/NNP/new York/NNP/york City/NNP/city",

    PersonListQuestion(PropertyWithFilter(List(Token("lived", "VBD", "live")),
      FilterWithModifier(List(Token("in", "IN", "in")),
        AndValue(List(NamedValue(List(Token("Berlin", "NNP", "berlin"))),
          NamedValue(List(Token("Copenhagen", "NNP", "copenhagen"))),
          NamedValue(List(Token("New", "NNP", "new"), Token("York", "NNP", "york"), Token("City", "NNP", "city")))))))))


  test("Who/WP/who lived/VBD/live in/IN/in Berlin/NNP/berlin and/CC/and Paris/NNP/paris ,/,/, "
    + "Copenhagen/NNP/copenhagen or/CC/or Toronto/NNP/toronto ,/,/, "
    + "and/CC/and New/NNP/new York/NNP/york City/NNP/city",

    PersonListQuestion(PropertyWithFilter(List(Token("lived", "VBD", "live")),
      FilterWithModifier(List(Token("in", "IN", "in")),
        AndValue(List(AndValue(List(NamedValue(List(Token("Berlin", "NNP", "berlin"))),
          NamedValue(List(Token("Paris", "NNP", "paris"))))),
          OrValue(List(NamedValue(List(Token("Copenhagen", "NNP", "copenhagen"))),
            NamedValue(List(Token("Toronto", "NNP", "toronto"))))),
          NamedValue(List(Token("New", "NNP", "new"), Token("York", "NNP", "york"), Token("City", "NNP", "city")))))))))


  test("Which/WP/which universities/NNS/university did/VBD/do Obama/NNP/obama go/VBD/go to/TO/to",

    ListQuestion(QueryWithProperty(NamedQuery(List(Token("universities", "NNS", "university"))),
      InversePropertyWithFilter(List(Token("did", "VBD", "do"), Token("go", "VBD", "go"), Token("to", "TO", "to")),
        PlainFilter(NamedValue(List(Token("Obama", "NNP", "obama"))))))))


  test("Who/WP/who went/VBD/go to/TO/to Stanford/NNP/stanford",

    PersonListQuestion(PropertyWithFilter(List(Token("went", "VBD", "go")),
      FilterWithModifier(List(Token("to", "TO", "to")), NamedValue(List(Token("Stanford", "NNP", "stanford")))))))

}
