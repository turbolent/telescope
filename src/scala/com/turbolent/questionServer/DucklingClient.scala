package com.turbolent.questionServer

import java.nio.charset.StandardCharsets.UTF_8
import java.net.URL

import com.twitter.finagle.http.Response
import com.twitter.util.Future
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods

object DucklingClient {
  case class Value(`type`: Option[String], value: Any)
  case class Span(dim: String, body: String, start: Int, end: Int, value: Value)
}

class DucklingClient(hostname: String, port: Int) {
  import DucklingClient.Span

  private implicit val formats: DefaultFormats = DefaultFormats

  val client = new featherbed.Client(new URL(s"http://$hostname:$port/"))

  def parse(text: String): Future[Seq[Span]] =
    client
      .post("parse")
      .withParams("text" -> text)
      .withCharset(UTF_8)
      .send[Response]()
      .map { response =>
        JsonMethods.parse(response.contentString)
          .extract[Seq[Span]]
      }
}
