import org.scalatest.{FunSuite, Matchers, OptionValues, TryValues}
import com.turbolent.spacyThrift.Token
import com.turbolent.spacyThrift.SpacyThriftClient

class TestSuite extends FunSuite
  with Matchers
  with TryValues
  with OptionValues
  with TwitterFutures
{
  test("tag a sentence") {
    val client = new SpacyThriftClient("localhost", 9090)
    val future = client.tag("This is a test")
    future.futureValue shouldEqual Seq(
      Token("This", "DT", "this"),
      Token("is", "VBZ", "be"),
      Token("a", "DT", "a"),
      Token("test", "NN", "test")
    )
  }
}