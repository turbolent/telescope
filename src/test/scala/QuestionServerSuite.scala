import com.turbolent.questionServer.QuestionServer
import com.twitter.finagle.httpx._
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{OptionValues, Matchers, FunSuite, TryValues}

import scala.util.Try


@RunWith(classOf[JUnitRunner])
class QuestionServerSuite extends FunSuite
    with TwitterFutures
    with Matchers
    with TryValues
    with OptionValues
{

  val service = QuestionServer.getService()

  implicit val formats = DefaultFormats

  def get[U](path: String)(fun: Response => U) = {
    val request = Request(Method.Get, path)
    whenReady(service(request))(fun)
  }

  test("unknown route") {
    get("/") { response =>
      response.status shouldEqual Status.NotFound
      response.contentString shouldBe empty
    }
  }

  def tryParseResponse(response: Response) =
    Try(JsonMethods.parse(response.contentString)
        .extract[Map[String, _]]).success.get

  test("missing parameter") {
    get("/parse") { response =>
      response.status shouldEqual Status.BadRequest
      response.contentType.value shouldBe Message.ContentTypeJson
      val content = tryParseResponse(response)
      content shouldBe Map("error" -> "Missing query parameter: sentence")
    }
  }

  test("empty sentence") {
    get("/parse?sentence=") { response =>
      response.status shouldEqual Status.Ok
      response.contentType.value shouldBe Message.ContentTypeJson
      val content = tryParseResponse(response)
      checkError(content)
      content should contain key "tokens"
      content("tokens") shouldBe List()
    }
  }

  def checkError(content: Map[String, _]) {
    content should contain key "error"
  }

  def checkTokens(content: Map[String, _]) {
    content should contain key "tokens"
    val maybeTokens = content("tokens")
    maybeTokens shouldBe an[List[_]]
    val tokens = maybeTokens.asInstanceOf[List[_]]
    tokens should not be empty
  }

  def checkQuestion(content: Map[String, _]) {
    content should contain key "question"
    val maybeQuestion = content("question")
    maybeQuestion shouldBe an[Map[_, _]]
    val question = maybeQuestion.asInstanceOf[Map[_, _]]
    question should not be empty
  }

  def checkNodes(content: Map[String, _]) {
    content should contain key "nodes"
    val maybeNodes = content("nodes")
    maybeNodes shouldBe an[List[_]]
    val nodes = maybeNodes.asInstanceOf[List[_]]
    nodes should not be empty
  }

  test("incomplete sentence") {
    get("/parse?sentence=who") { response =>
      response.status shouldEqual Status.Ok
      response.contentType.value shouldBe Message.ContentTypeJson
      val content = tryParseResponse(response)
      checkError(content)
      checkTokens(content)
    }
  }

  test("sentence with missing property edge") {
    get("/parse?sentence=who+is") { response =>
      response.status shouldEqual Status.Ok
      response.contentType.value shouldBe Message.ContentTypeJson
      val content = tryParseResponse(response)
      checkError(content)
      checkTokens(content)
      checkQuestion(content)
    }
  }

  test("supported sentence") {
    get("/parse?sentence=who+died") { response =>
      response.status shouldEqual Status.Ok
      response.contentType.value shouldBe Message.ContentTypeJson
      val content = tryParseResponse(response)
      content should not (contain key "error")
      checkTokens(content)
      checkQuestion(content)
      checkNodes(content)
    }
  }
}
