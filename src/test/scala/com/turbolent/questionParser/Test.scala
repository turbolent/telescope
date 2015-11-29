package com.turbolent.questionParser

import java.nio.file.Paths

import com.turbolent.questionParser.ast._
import com.turbolent.lemmatizer.Lemmatizer
import junit.framework.TestCase
import org.hamcrest.Matcher
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Assert._


class Test extends TestCase {

  implicit lazy val lemmatizer =
    Lemmatizer.loadFrom(Paths.get("lemmatizer-model"))

  def parseListQuestion(tokens: Seq[Token]) =
    ListParser.parse(tokens, ListParser.phrase(ListParser.Question))

  def tokenize(taggedSentence: String) =
    taggedSentence.split(' ').toSeq map { taggedWord =>
      val Array(word, tag) = taggedWord.split('/')
      Token(word, tag)
    }

  def assertSuccess(x: Any) = {
    val matcher: Matcher[Any] = instanceOf(classOf[BaseParser#Success[Any]])
    assertThat(x, matcher)
  }

  def testListQuestions() {
    {
      val tokens = tokenize("Give/VB me/PRP all/DT musicians/NNS that/WDT "
                            + "were/VBD born/VBN in/IN Vienna/NNP "
                            + "and/CC died/VBN in/IN Berlin/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("musicians", "NNS"))),
        AndProperty(List(PropertyWithFilter(List(Token("were", "VBD"), Token("born", "VBN")),
          FilterWithModifier(List(Token("in", "IN")),
            NamedValue(List(Token("Vienna", "NNP"))))),
          PropertyWithFilter(List(Token("died", "VBN")),
            FilterWithModifier(List(Token("in", "IN")),
              NamedValue(List(Token("Berlin", "NNP")))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Which/WDT presidents/NNS were/VBD born/VBN before/IN 1900/CD")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("presidents", "NNS"))),
        PropertyWithFilter(List(Token("were", "VBD"), Token("born", "VBN")),
          FilterWithModifier(List(Token("before", "IN")), Number(List(Token("1900", "CD")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Give/VB me/PRP all/DT actors/NNS born/VBN in/IN "
                            + "Berlin/NNP or/CC San/NNP Francisco/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("actors", "NNS"))),
        PropertyWithFilter(List(Token("born", "VBN")), FilterWithModifier(List(Token("in", "IN")),
          OrValue(List(NamedValue(List(Token("Berlin", "NNP"))),
            NamedValue(List(Token("San", "NNP"), Token("Francisco", "NNP")))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("List/VB books/NNS by/IN George/NNP Orwell/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NNS"))),
        PropertyWithFilter(List(),
          FilterWithModifier(List(Token("by", "IN")),
            NamedValue(List(Token("George", "NNP"), Token("Orwell", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Which/WDT books/NN did/VBD George/NNP Orwell/NNP write/VB")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NN"))),
        InversePropertyWithFilter(List(Token("did", "VBD"), Token("write", "VB")),
          PlainFilter(NamedValue(List(Token("George", "NNP"), Token("Orwell", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("list/VB presidents/NNS of/IN Argentina/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(RelationshipQuery(NamedQuery(List(Token("presidents", "NNS"))),
        NamedQuery(List(Token("Argentina", "NNP"))), Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("List/VB movies/NNS directed/VBN by/IN Quentin/NNP Tarantino/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("movies", "NNS"))),
        PropertyWithFilter(List(Token("directed", "VBN")),
          FilterWithModifier(List(Token("by", "IN")),
            NamedValue(List(Token("Quentin", "NNP"), Token("Tarantino", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Which/WDT movies/NN did/VBD Mel/NNP Gibson/NNP direct/VB")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("movies", "NN"))),
        InversePropertyWithFilter(List(Token("did", "VBD"), Token("direct", "VB")),
          PlainFilter(NamedValue(List(Token("Mel", "NNP"), Token("Gibson", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Which/WDT movies/NN did/VBD Obama/NNP star/VB in/RP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("movies", "NN"))),
        InversePropertyWithFilter(List(Token("did", "VBD"),
          Token("star", "VB"), Token("in", "RP")),
          PlainFilter(NamedValue(List(Token("Obama", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("In/IN what/WDT movies/NN did/VBD Jennifer/NNP Aniston/NNP appear/VB")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("movies", "NN"))),
        InversePropertyWithFilter(List(Token("did", "VBD"), Token("appear", "VB")),
          PlainFilter(NamedValue(List(Token("Jennifer", "NNP"), Token("Aniston", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Movies/NNP starring/VB Winona/NNP Ryder/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("Movies", "NNP"))),
        PropertyWithFilter(List(Token("starring", "VB")),
          PlainFilter(NamedValue(List(Token("Winona", "NNP"), Token("Ryder", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("List/VB albums/NNS of/IN Pink/NNP Floyd/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(RelationshipQuery(NamedQuery(List(Token("albums", "NNS"))),
        NamedQuery(List(Token("Pink", "NNP"), Token("Floyd", "NNP"))), Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("List/VB the/DT actors/NNS of/IN Titanic/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("actors", "NNS"))),
          NamedQuery(List(Token("Titanic", "NNP"))), Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("who/WP are/VBP the/DT actors/NNS of/IN Titanic/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("actors", "NNS"))),
          NamedQuery(List(Token("Titanic", "NNP"))), Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("who/WP acted/VBD in/IN Alien/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = PersonListQuestion(PropertyWithFilter(List(Token("acted", "VBD")),
        FilterWithModifier(List(Token("in", "IN")), NamedValue(List(Token("Alien", "NNP"))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("who/WP starred/VBD in/IN Inception/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = PersonListQuestion(PropertyWithFilter(List(Token("starred", "VBD")),
        FilterWithModifier(List(Token("in", "IN")),
          NamedValue(List(Token("Inception", "NNP"))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("who/WP are/VBP the/DT actors/NNS which/WDT starred/VBD in/IN Inception/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("the", "DT"),
          Token("actors", "NNS"))),
          PropertyWithFilter(List(Token("starred", "VBD")),
            FilterWithModifier(List(Token("in", "IN")),
              NamedValue(List(Token("Inception", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Who/WP is/VBZ the/DT director/NN of/IN Big/NN Fish/NN")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("director", "NN"))),
          NamedQuery(List(Token("Big", "NN"), Token("Fish", "NN"))), Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("who/WP directed/VBD Pocahontas/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = PersonListQuestion(PropertyWithFilter(List(Token("directed", "VBD")),
        PlainFilter(NamedValue(List(Token("Pocahontas", "NNP"))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Which/WDT city/NN is/VBZ bigger/JJR than/IN New/NNP York/NNP City/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("city", "NN"))),
        PropertyWithFilter(List(Token("is", "VBZ")),
          FilterWithComparativeModifier(List(Token("bigger", "JJR"), Token("than", "IN")),
            NamedValue(List(Token("New", "NNP"), Token("York", "NNP"), Token("City", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("What/WP are/VBP the/DT members/NNS of/IN Metallica/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("members", "NNS"))),
          NamedQuery(List(Token("Metallica", "NNP"))), Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("members/NNS of/IN Metallica/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(RelationshipQuery(NamedQuery(List(Token("members", "NNS"))),
        NamedQuery(List(Token("Metallica", "NNP"))), Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("What/WP is/VBZ the/DT music/NN genre/NN of/IN Gorillaz/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("music", "NN"), Token("genre", "NN"))),
          NamedQuery(List(Token("Gorillaz", "NNP"))), Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("actors/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(NamedQuery(List(Token("actors", "NNP"))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Radiohead/NNS members/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(NamedQuery(List(Token("Radiohead", "NNS"),
        Token("members", "NNP"))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("What/WP is/VBZ the/DT cast/NN of/IN Friends/NNS")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"), Token("cast", "NN"))),
          NamedQuery(List(Token("Friends", "NNS"))), Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Who/WP works/VBZ in/IN Breaking/NNS Bad/NNS")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = PersonListQuestion(PropertyWithFilter(List(Token("works", "VBZ")),
        FilterWithModifier(List(Token("in", "IN")),
          NamedValue(List(Token("Breaking", "NNS"), Token("Bad", "NNS"))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("what/WDT languages/NNS are/VBP spoken/VBN in/IN Switzerland/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("languages", "NNS"))),
        PropertyWithFilter(List(Token("are", "VBP"), Token("spoken", "VBN")),
          FilterWithModifier(List(Token("in", "IN")),
            NamedValue(List(Token("Switzerland", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Which/WDT cities/NNS have/VBP more/JJR than/IN "
                 + "two/CD million/CD inhabitants/NNS")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("cities", "NNS"))),
        PropertyWithFilter(List(Token("have", "VBP")),
          FilterWithComparativeModifier(List(Token("more", "JJR"), Token("than", "IN")),
            NumberWithUnit(List(Token("two", "CD"), Token("million", "CD")),
              List(Token("inhabitants", "NNS")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("What/WP films/NNS featured/VBD the/DT character/NN Popeye/NNP Doyle/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("films", "NNS"))),
        PropertyWithFilter(List(Token("featured", "VBD")),
          PlainFilter(NamedValue(List(Token("the", "DT"), Token("character", "NN"),
            Token("Popeye", "NNP"), Token("Doyle", "NNP")))))))
      assertEquals(expected, result.get)

    }
    {
      val tokens = tokenize("Who/WP is/VBZ taller/JJR than/IN Obama/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = PersonListQuestion(PropertyWithFilter(List(Token("is", "VBZ")),
        FilterWithComparativeModifier(List(Token("taller", "JJR"), Token("than", "IN")),
          NamedValue(List(Token("Obama", "NNP"))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Who/WP lived/VBD in/IN Berlin/NNP and/CC Copenhagen/NNP "
                            + "or/CC New/NNP York/NNP City/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = PersonListQuestion(PropertyWithFilter(List(Token("lived", "VBD")),
        FilterWithModifier(List(Token("in", "IN")),
          OrValue(List(AndValue(List(NamedValue(List(Token("Berlin", "NNP"))),
            NamedValue(List(Token("Copenhagen", "NNP"))))),
            NamedValue(List(Token("New", "NNP"), Token("York", "NNP"), Token("City", "NNP"))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("which/WDT books/NNS written/VBN by/IN Orwell/NNP are/VBP about/IN "
                 + "dystopian/JJ societies/NNS")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NNS"))),
        AndProperty(List(PropertyWithFilter(List(Token("written", "VBN")),
          FilterWithModifier(List(Token("by", "IN")), NamedValue(List(Token("Orwell", "NNP"))))),
          PropertyWithFilter(List(Token("are", "VBP")),
            FilterWithModifier(List(Token("about", "IN")),
              NamedValue(List(Token("dystopian", "JJ"), Token("societies", "NNS")))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Which/WDT books/NN did/VBD Orwell/NNP or/CC Shakespeare/NNP write/VB")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NN"))),
        InversePropertyWithFilter(List(Token("did", "VBD"), Token("write", "VB")),
          PlainFilter(OrValue(List(NamedValue(List(Token("Orwell", "NNP"))),
            NamedValue(List(Token("Shakespeare", "NNP")))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("who/WP are/VBP the/DT children/NNS of/IN the/DT presidents/NNS")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("children", "NNS"))),
          NamedQuery(List(Token("the", "DT"), Token("presidents", "NNS"))),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("what/WP are/VBP the/DT largest/JJS cities/NNS in/IN europe/NN")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(QueryWithProperty(
        NamedQuery(List(Token("the", "DT"), Token("largest", "JJS"), Token("cities", "NNS"))),
        PropertyWithFilter(List(),
          FilterWithModifier(List(Token("in", "IN")),
            NamedValue(List(Token("europe", "NN")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("what/WP are/VBP the/DT population/NN sizes/NNS of/IN cities/NNS "
                 + "located/VBN in/IN california/NN")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("population", "NN"),
          Token("sizes", "NNS"))),
          QueryWithProperty(NamedQuery(List(Token("cities", "NNS"))),
            PropertyWithFilter(List(Token("located", "VBN")),
              FilterWithModifier(List(Token("in", "IN")),
                NamedValue(List(Token("california", "NN")))))),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("what/WP are/VBP the/DT population/NN sizes/NNS of/IN cities/NNS "
                 + "that/WDT are/VBP located/VBN in/IN california/NN")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("population", "NN"),
          Token("sizes", "NNS"))),
          QueryWithProperty(NamedQuery(List(Token("cities", "NNS"))),
            PropertyWithFilter(List(Token("are", "VBP"), Token("located", "VBN")),
              FilterWithModifier(List(Token("in", "IN")),
                NamedValue(List(Token("california", "NN")))))),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Who/WP are/VBP the/DT children/NNS "
                 + "of/IN the/DT children/NNS "
                 + "of/IN Bill/NNP Clinton/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("children", "NNS"))),
          RelationshipQuery(NamedQuery(List(Token("the", "DT"), Token("children", "NNS"))),
            NamedQuery(List(Token("Bill", "NNP"), Token("Clinton", "NNP"))),
            Token("of", "IN")),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("What/WP are/VBP the/DT largest/JJS cities/NNS of/IN California/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(
          NamedQuery(List(Token("the", "DT"), Token("largest", "JJS"), Token("cities", "NNS"))),
          NamedQuery(List(Token("California", "NNP"))),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("What/WP are/VBP the/DT biggest/JJS cities/NNS of/IN California/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("biggest", "JJS"), Token("cities", "NNS"))),
          NamedQuery(List(Token("California", "NNP"))),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Who/WP did/VBD Bill/NNP Clinton/NNP marry/VB")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        PersonListQuestion(InversePropertyWithFilter(List(Token("did", "VBD"),
          Token("marry", "VB")),
          PlainFilter(NamedValue(List(Token("Bill", "NNP"), Token("Clinton", "NNP"))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Clinton/NNP 's/POS daughters/NN")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(RelationshipQuery(NamedQuery(List(Token("daughters", "NN"))),
        NamedQuery(List(Token("Clinton", "NNP"))),
        Token("'s", "POS")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("What/WP are/VBP California/NNP 's/POS biggest/JJS cities/NNS")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("biggest", "JJS"),
          Token("cities", "NNS"))),
          NamedQuery(List(Token("California", "NNP"))),
          Token("'s", "POS")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Who/WP is/VBZ Bill/NNP Clinton/NNP 's/POS daughter/NN")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = ListQuestion(RelationshipQuery(NamedQuery(List(Token("daughter", "NN"))),
        NamedQuery(List(Token("Bill", "NNP"), Token("Clinton", "NNP"))),
        Token("'s", "POS")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Who/WP is/VBZ Bill/NNP Clinton/NNP 's/POS daughter/NN 's/POS "
                            + "husband/NN 's/POS daughter/NN 's/POS grandfather/NN")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("grandfather", "NN"))),
          RelationshipQuery(NamedQuery(List(Token("daughter", "NN"))),
            RelationshipQuery(NamedQuery(List(Token("husband", "NN"))),
              RelationshipQuery(NamedQuery(List(Token("daughter", "NN"))),
                NamedQuery(List(Token("Bill", "NNP"), Token("Clinton", "NNP"))),
                Token("'s", "POS")),
              Token("'s", "POS")),
            Token("'s", "POS")),
          Token("'s", "POS")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("presidents/NNS that/WDT have/VBP children/NNS")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("presidents", "NNS"))),
          PropertyWithFilter(List(Token("have", "VBP")),
            PlainFilter(NamedValue(List(Token("children", "NNS")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Who/WP did/VBD Bill/NNP Clinton/NNP 's/POS daughter/NN marry/VB")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        PersonListQuestion(InversePropertyWithFilter(List(Token("did", "VBD"),
          Token("marry", "VB")),
          PlainFilter(ValueRelationship(NamedValue(List(Token("daughter", "NN"))),
            NamedValue(List(Token("Bill", "NNP"), Token("Clinton", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("In/IN which/WDT californian/NN cities/NNS "
                            + "live/VBP more/JJR than/IN 2/CD million/CD people/NNS")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("californian", "NN"),
          Token("cities", "NNS"))),
          PropertyWithFilter(List(Token("live", "VBP")),
            FilterWithComparativeModifier(List(Token("more", "JJR"), Token("than", "IN")),
              NumberWithUnit(List(Token("2", "CD"), Token("million", "CD")),
                List(Token("people", "NNS")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("the/DT population/NNP of/IN Japan/NNP before/IN 1900/CD")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("population", "NNP"))),
          QueryWithProperty(NamedQuery(List(Token("Japan", "NNP"))),
            PropertyWithFilter(List(),
              FilterWithModifier(List(Token("before", "IN")),
                Number(List(Token("1900", "CD")))))),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("What/WP are/VBP the/DT population/NN of/IN China/NNP and/CC the/DT USA/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("population", "NN"))),
          AndQuery(List(NamedQuery(List(Token("China", "NNP"))),
            NamedQuery(List(Token("the", "DT"), Token("USA", "NNP"))))),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("the/DT population/NNP of/IN Japan/NNP and/CC China/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("population", "NNP"))),
          AndQuery(List(NamedQuery(List(Token("Japan", "NNP"))),
            NamedQuery(List(Token("China", "NNP"))))),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("the/DT population/NNP of/IN Japan/NNP and/CC China/NNP before/IN 1900/CD")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("population", "NNP"))),
          QueryWithProperty(AndQuery(List(NamedQuery(List(Token("Japan", "NNP"))),
            NamedQuery(List(Token("China", "NNP"))))),
            PropertyWithFilter(List(),
              FilterWithModifier(List(Token("before", "IN")),
                Number(List(Token("1900", "CD")))))),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("the/DT population/NNP and/CC area/NNP of/IN Japan/NNP and/CC China/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(AndQuery(List(NamedQuery(List(Token("the", "DT"),
          Token("population", "NNP"))),
          NamedQuery(List(Token("area", "NNP"))))),
          AndQuery(List(NamedQuery(List(Token("Japan", "NNP"))),
            NamedQuery(List(Token("China", "NNP"))))),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("the/DT population/NN ,/, land/NN area/NN and/CC capitals/NNP "
                 + "of/IN Japan/NNP ,/, India/NNP and/CC China/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(AndQuery(List(NamedQuery(List(Token("the", "DT"),
          Token("population", "NN"))),
          NamedQuery(List(Token("land", "NN"), Token("area", "NN"))),
          NamedQuery(List(Token("capitals", "NNP"))))),
          AndQuery(List(NamedQuery(List(Token("Japan", "NNP"))),
            NamedQuery(List(Token("India", "NNP"))),
            NamedQuery(List(Token("China", "NNP"))))),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("children/NNS of/IN all/DT presidents/NNS of/IN the/DT US/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("children", "NNS"))),
          RelationshipQuery(NamedQuery(List(Token("all", "DT"), Token("presidents", "NNS"))),
            NamedQuery(List(Token("the", "DT"), Token("US", "NNP"))),
            Token("of", "IN")),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Clinton/NNP 's/POS children/NNS and/CC grandchildren/NNS")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(AndQuery(List(NamedQuery(List(Token("children", "NNS"))),
          NamedQuery(List(Token("grandchildren", "NNS"))))),
          NamedQuery(List(Token("Clinton", "NNP"))),
          Token("'s", "POS")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Japan/NNP and/CC China/NNP 's/POS population/NNP and/CC area/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(AndQuery(List(NamedQuery(List(Token("population", "NNP"))),
          NamedQuery(List(Token("area", "NNP"))))),
          AndQuery(List(NamedQuery(List(Token("Japan", "NNP"))),
            NamedQuery(List(Token("China", "NNP"))))),
          Token("'s", "POS")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("which/WDT books/NNS were/VBD written/VBN by/IN Orwell/NNP "
                 + "and/CC are/VBP about/IN dystopian/NN societies/NNS")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NNS"))),
          AndProperty(List(PropertyWithFilter(List(Token("were", "VBD"), Token("written", "VBN")),
            FilterWithModifier(List(Token("by", "IN")),
              NamedValue(List(Token("Orwell", "NNP"))))),
            PropertyWithFilter(List(Token("are", "VBP")),
              FilterWithModifier(List(Token("about", "IN")),
                NamedValue(List(Token("dystopian", "NN"), Token("societies", "NNS")))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Who/WP is/VBZ the/DT president/NN of/IN France/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("president", "NN"))),
          NamedQuery(List(Token("France", "NNP"))),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Who/WP are/VBP the/DT daughters/NNS of/IN the/DT wife/NN of/IN "
                 + "the/DT president/NN of/IN the/DT United/NNP States/NNPS")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"),
          Token("daughters", "NNS"))),
          RelationshipQuery(NamedQuery(List(Token("the", "DT"), Token("wife", "NN"))),
            RelationshipQuery(NamedQuery(List(Token("the", "DT"), Token("president", "NN"))),
              NamedQuery(List(Token("the", "DT"),
                Token("United", "NNP"), Token("States", "NNPS"))),
              Token("of", "IN")),
            Token("of", "IN")),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Which/WDT books/NN were/VBD authored/VBN by/IN George/NNP Orwell/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NN"))),
          PropertyWithFilter(List(Token("were", "VBD"), Token("authored", "VBN")),
            FilterWithModifier(List(Token("by", "IN")),
              NamedValue(List(Token("George", "NNP"), Token("Orwell", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("What/WDT actor/NN married/VBD John/NNP F./NNP Kennedy/NNP 's/POS sister/NN")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("actor", "NN"))),
          PropertyWithFilter(List(Token("married", "VBD")),
            PlainFilter(ValueRelationship(NamedValue(List(Token("sister", "NN"))),
              NamedValue(List(Token("John", "NNP"), Token("F.", "NNP"),
                Token("Kennedy", "NNP"))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Which/WDT instrument/NN did/VBD John/NNP Lennon/NNP play/VB")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("instrument", "NN"))),
          InversePropertyWithFilter(List(Token("did", "VBD"), Token("play", "VB")),
            PlainFilter(NamedValue(List(Token("John", "NNP"), Token("Lennon", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Which/WDT poets/NNS lived/VBD in/IN the/DT 19th/JJ century/NN")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("poets", "NNS"))),
          PropertyWithFilter(List(Token("lived", "VBD")),
            FilterWithModifier(List(Token("in", "IN")),
              NamedValue(List(Token("the", "DT"), Token("19th", "JJ"),
                Token("century", "NN")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Who/WP wrote/VBD ``/`` Le/NNP Petit/NNP Prince/NNP ''/'' "
                 + "and/CC ``/`` Vol/NNP de/IN Nuit/NNP ''/''")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        PersonListQuestion(PropertyWithFilter(List(Token("wrote", "VBD")),
          PlainFilter(AndValue(List(NamedValue(List(Token("Le", "NNP"),
            Token("Petit", "NNP"), Token("Prince", "NNP"))),
            NamedValue(List(Token("Vol", "NNP"), Token("de", "IN"), Token("Nuit", "NNP"))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Who/WP is/VBZ the/DT son/NN of/IN the/DT main/JJ actor/NN "
                 + "of/IN ``/`` I/PRP ,/, Robot/NNP ''/''")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("the", "DT"), Token("son", "NN"))),
          RelationshipQuery(NamedQuery(List(Token("the", "DT"),
            Token("main", "JJ"), Token("actor", "NN"))),
            NamedQuery(List(Token("I", "PRP"), Token(",", ","), Token("Robot", "NNP"))),
            Token("of", "IN")),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("What/WP did/VBD George/NNP Orwell/NNP write/VB")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ThingListQuestion(InversePropertyWithFilter(List(Token("did", "VBD"),
          Token("write", "VB")),
          PlainFilter(NamedValue(List(Token("George", "NNP"), Token("Orwell", "NNP"))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("What/WP was/VBD authored/VBN by/IN George/NNP Orwell/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ThingListQuestion(PropertyWithFilter(List(Token("was", "VBD"), Token("authored", "VBN")),
          FilterWithModifier(List(Token("by", "IN")),
            NamedValue(List(Token("George", "NNP"), Token("Orwell", "NNP"))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("What/WP books/NNP were/VBD authored/VBN by/IN George/NNP Orwell/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NNP"))),
          PropertyWithFilter(List(Token("were", "VBD"), Token("authored", "VBN")),
            FilterWithModifier(List(Token("by", "IN")),
              NamedValue(List(Token("George", "NNP"), Token("Orwell", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("children/NNS and/CC grand/JJ children/NNS of/IN Clinton/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(RelationshipQuery(AndQuery(List(NamedQuery(List(Token("children", "NNS"))),
          NamedQuery(List(Token("grand", "JJ"), Token("children", "NNS"))))),
          NamedQuery(List(Token("Clinton", "NNP"))),
          Token("of", "IN")))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Which/WDT books/NN did/VBD Orwell/NNP write/VB "
                 + "before/IN the/DT world/NN war/NN")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      // TODO: handle in second stage:
      //       empty property with modifying filter is constraining preceding property
      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("books", "NN"))),
        AndProperty(List(InversePropertyWithFilter(List(Token("did", "VBD"),
          Token("write", "VB")),
          PlainFilter(NamedValue(List(Token("Orwell", "NNP"))))),
          PropertyWithFilter(List(),
            FilterWithModifier(List(Token("before", "IN")),
              NamedValue(List(Token("the", "DT"), Token("world", "NN"), Token("war", "NN")))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("What/WP are/VBP cities/NNS which/WDT have/VBP a/DT population/NN "
                 + "larger/JJR than/IN 1/CD million/CD")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      // TODO: handle in second stage:
      //       filter of empty second property is constraining preceding property
      //       (empty property is shortened from ~"which is")
      val expected = ListQuestion(QueryWithProperty(NamedQuery(List(Token("cities", "NNS"))),
        AndProperty(List(PropertyWithFilter(List(Token("have", "VBP")),
          PlainFilter(NamedValue(List(Token("a", "DT"), Token("population", "NN"))))),
          PropertyWithFilter(List(),
            FilterWithComparativeModifier(List(Token("larger", "JJR"), Token("than", "IN")),
              Number(List(Token("1", "CD"), Token("million", "CD")))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("which/WDT cities/NNS have/VBP a/DT population/NN larger/JJR than/IN 1000/CD")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      // TODO: handle in second stage:
      //       filter of empty second property is constraining preceding property
      //       (empty property is shortened from ~"which is")
      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("cities", "NNS"))),
          AndProperty(List(PropertyWithFilter(List(Token("have", "VBP")),
            PlainFilter(NamedValue(List(Token("a", "DT"), Token("population", "NN"))))),
            PropertyWithFilter(List(),
              FilterWithComparativeModifier(List(Token("larger", "JJR"), Token("than", "IN")),
                Number(List(Token("1000", "CD")))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("cities/NNS which/WDT have/VBP a/DT population/NN "
                            + "that/WDT is/VBZ larger/JJR than/IN 1000/CD")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      // TODO: handle in second stage:
      //       filter of second property with single "be" lemma is constraining preceding property
      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("cities", "NNS"))),
          AndProperty(List(PropertyWithFilter(List(Token("have", "VBP")),
            PlainFilter(NamedValue(List(Token("a", "DT"), Token("population", "NN"))))),
            PropertyWithFilter(List(Token("is", "VBZ")),
              FilterWithComparativeModifier(List(Token("larger", "JJR"), Token("than", "IN")),
                Number(List(Token("1000", "CD")))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("cities/NNS which/WDT have/VBP a/DT population/NN larger/JJR than/IN 1000/CD")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      // TODO: handle in second stage:
      //       filter of empty second property is constraining preceding property
      //       (empty property is shortened from ~"which is")
      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("cities", "NNS"))),
          AndProperty(List(PropertyWithFilter(List(Token("have", "VBP")),
            PlainFilter(NamedValue(List(Token("a", "DT"), Token("population", "NN"))))),
            PropertyWithFilter(List(),
              FilterWithComparativeModifier(List(Token("larger", "JJR"), Token("than", "IN")),
                Number(List(Token("1000", "CD")))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("What/WP are/VBP California/NNP 's/POS cities/NNS "
                 + "which/WDT have/VBP a/DT population/NN "
                 + "larger/JJR than/IN 1/CD million/CD")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      // TODO: handle in second stage:
      //       filter of empty second property is constraining preceding property
      //       (empty property is shortened from ~"which is")
      val expected =
        ListQuestion(QueryWithProperty(RelationshipQuery(NamedQuery(List(Token("cities", "NNS"))),
          NamedQuery(List(Token("California", "NNP"))), Token("'s", "POS")),
          AndProperty(List(PropertyWithFilter(List(Token("have", "VBP")),
            PlainFilter(NamedValue(List(Token("a", "DT"), Token("population", "NN"))))),
            PropertyWithFilter(List(),
              FilterWithComparativeModifier(List(Token("larger", "JJR"), Token("than", "IN")),
                Number(List(Token("1", "CD"), Token("million", "CD")))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Which/WDT cities/NNS in/IN California/NNP are/VBP larger/JJR than/IN "
                 + "cities/NNS in/IN Germany/NNP or/CC in/IN France/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      // TODO: handle in second stage:
      //       filter of empty last property is constraining preceding property
      //       (empty property is shortened from ~"which are located in")
      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("cities", "NNS"))),
          AndProperty(List(PropertyWithFilter(List(),
            FilterWithModifier(List(Token("in", "IN")),
              NamedValue(List(Token("California", "NNP"))))),
            PropertyWithFilter(List(Token("are", "VBP")),
              FilterWithComparativeModifier(List(Token("larger", "JJR"),
                Token("than", "IN")),
                NamedValue(List(Token("cities", "NNS"))))),
            PropertyWithFilter(List(),
              OrFilter(List(FilterWithModifier(List(Token("in", "IN")),
                NamedValue(List(Token("Germany", "NNP")))),
                FilterWithModifier(List(Token("in", "IN")),
                  NamedValue(List(Token("France", "NNP")))))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Who/WP composed/VBN the/DT music/NN for/IN Schindler/NNP 's/POS List/NN")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      // TODO: "Schindler's List" should be detected as one name, not as a possessive:
      //       also run NER for possessives
      // TODO: handle in second stage:
      //       filter of empty second property is constraining preceding property
      val expected =
        PersonListQuestion(AndProperty(List(PropertyWithFilter(List(Token("composed", "VBN")),
          PlainFilter(NamedValue(List(Token("the", "DT"), Token("music", "NN"))))),
          PropertyWithFilter(List(),
            FilterWithModifier(List(Token("for", "IN")),
              ValueRelationship(NamedValue(List(Token("List", "NN"))),
                NamedValue(List(Token("Schindler", "NNP")))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("Which/WDT cities/NNS in/IN California/NNP are/VBP larger/JJR than/IN "
                 + "cities/NNS which/WDT are/VBD located/VBD in/IN Germany/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      // TODO: handle in second stage:
      //       filter of second property is constraining preceding property with comparative filter
      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("cities", "NNS"))),
          AndProperty(List(PropertyWithFilter(List(),
            FilterWithModifier(List(Token("in", "IN")),
              NamedValue(List(Token("California", "NNP"))))),
            PropertyWithFilter(List(Token("are", "VBP")),
              FilterWithComparativeModifier(List(Token("larger", "JJR"), Token("than", "IN")),
                NamedValue(List(Token("cities", "NNS"))))),
            PropertyWithFilter(List(Token("are", "VBD"), Token("located", "VBD")),
              FilterWithModifier(List(Token("in", "IN")),
                NamedValue(List(Token("Germany", "NNP")))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("authors/NNS who/WP died/VBD")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("authors", "NNS"))),
          NamedProperty(List(Token("died", "VBD")))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("authors/NNS which/WDT died/VBD in/IN Berlin/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("authors", "NNS"))),
          PropertyWithFilter(List(Token("died", "VBD")),
            FilterWithModifier(List(Token("in", "IN")),
              NamedValue(List(Token("Berlin", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("which/WDT mountains/NNS are/VBP 1000/CD meters/NNS high/JJ")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("mountains", "NNS"))),
          AdjectivePropertyWithFilter(List(Token("are", "VBP"), Token("high", "JJ")),
            PlainFilter(NumberWithUnit(List(Token("1000", "CD")), List(Token("meters", "NNS")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("which/WDT mountains/NNS are/VBP more/JJR than/IN 1000/CD meters/NNS high/JJ")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("mountains", "NNS"))),
          AdjectivePropertyWithFilter(List(Token("are", "VBP"), Token("high", "JJ")),
            FilterWithComparativeModifier(List(Token("more", "JJR"), Token("than", "IN")),
              NumberWithUnit(List(Token("1000", "CD")), List(Token("meters", "NNS")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens =
        tokenize("who/WP starred/VBD in/IN movies/NNS directed/VBN by/IN Christopher/NN Nolan/NN")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      // NOTE: AndProperty is correct, next stage should realize second property
      //       is relative to first one, not subject
      val expected =
        PersonListQuestion(AndProperty(List(PropertyWithFilter(List(Token("starred", "VBD")),
           FilterWithModifier(List(Token("in", "IN")),
             NamedValue(List(Token("movies", "NNS"))))),
          PropertyWithFilter(List(Token("directed", "VBN")),
            FilterWithModifier(List(Token("by", "IN")),
              NamedValue(List(Token("Christopher", "NN"), Token("Nolan", "NN"))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Which/WDT country/NN was/VBD Obama/NNP born/VBN in/IN")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("country", "NN"))),
          InversePropertyWithFilter(List(Token("was", "VBD"), Token("born", "VBN"),
            Token("in", "IN")),
            PlainFilter(NamedValue(List(Token("Obama", "NNP")))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Which/WDT country/NN was/VBD Obama/NNP born/VBN in/IN in/IN 1961/CD")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected =
        ListQuestion(QueryWithProperty(NamedQuery(List(Token("country", "NN"))),
          AndProperty(List(InversePropertyWithFilter(List(Token("was", "VBD"),
            Token("born", "VBN"), Token("in", "IN")),
            PlainFilter(NamedValue(List(Token("Obama", "NNP"))))),
            PropertyWithFilter(List(),FilterWithModifier(List(Token("in", "IN")),
              Number(List(Token("1961", "CD")))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("What/WP are/VBP some/DT of/IN Seth/NNP Gabel/NNP 's/POS "
                            + "father-in-law/NN 's/POS movies/NNS")

      val result = parseListQuestion(tokens)
      assertSuccess(result)
      val expected =
        ListQuestion(RelationshipQuery(NamedQuery(List(Token("movies", "NNS"))),
          RelationshipQuery(NamedQuery(List(Token("father-in-law", "NN"))),
            NamedQuery(List(Token("Seth", "NNP"), Token("Gabel", "NNP"))),
            Token("'s", "POS")),
          Token("'s", "POS")))
      assertEquals(expected, result.get)
    }
  }

  def testNew() {
    {
      val tokens = tokenize("Who/WP lived/VBD in/IN Berlin/NNP ,/, Copenhagen/NNP ,/, "
                            + "or/CC New/NNP York/NNP City/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = PersonListQuestion(PropertyWithFilter(List(Token("lived", "VBD")),
        FilterWithModifier(List(Token("in", "IN")),
          OrValue(List(NamedValue(List(Token("Berlin", "NNP"))),
            NamedValue(List(Token("Copenhagen", "NNP"))),
            NamedValue(List(Token("New", "NNP"), Token("York", "NNP"), Token("City", "NNP"))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Who/WP lived/VBD in/IN Berlin/NNP ,/, Copenhagen/NNP ,/, "
                            + "and/CC New/NNP York/NNP City/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = PersonListQuestion(PropertyWithFilter(List(Token("lived", "VBD")),
        FilterWithModifier(List(Token("in", "IN")),
          AndValue(List(NamedValue(List(Token("Berlin", "NNP"))),
            NamedValue(List(Token("Copenhagen", "NNP"))),
            NamedValue(List(Token("New", "NNP"), Token("York", "NNP"), Token("City", "NNP"))))))))
      assertEquals(expected, result.get)
    }
    {
      val tokens = tokenize("Who/WP lived/VBD in/IN Berlin/NNP and/CC Paris/NNP ,/, "
                            + "Copenhagen/NNP or/CC Toronto/NNP ,/, "
                            + "and/CC New/NNP York/NNP City/NNP")
      val result = parseListQuestion(tokens)
      assertSuccess(result)

      val expected = PersonListQuestion(PropertyWithFilter(List(Token("lived", "VBD")),
        FilterWithModifier(List(Token("in", "IN")),
          AndValue(List(AndValue(List(NamedValue(List(Token("Berlin", "NNP"))),
            NamedValue(List(Token("Paris", "NNP"))))),
            OrValue(List(NamedValue(List(Token("Copenhagen", "NNP"))),
              NamedValue(List(Token("Toronto", "NNP"))))),
            NamedValue(List(Token("New", "NNP"), Token("York", "NNP"), Token("City", "NNP"))))))))
      assertEquals(expected, result.get)
    }
  }
}
