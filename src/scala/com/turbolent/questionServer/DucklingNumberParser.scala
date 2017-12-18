package com.turbolent.questionServer

import com.turbolent.questionServer.DucklingClient.{Span, Value}
import com.turbolent.wikidataOntology.NumberParser
import com.twitter.util.Await

class DucklingNumberParser(client: DucklingClient) extends NumberParser {

  override def parse(text: String): Double =
    Await.result {
      client.parse(text).map { spans =>
        spans.collectFirst {
          case Span("number", body, 0, length, Value(Some("value"), value))
            if body == text && length == text.length() =>

            value match {
              case d: Double => d
              case i: Int => i.toDouble
              case _ => value.toString.toDouble
            }
        }.get
      }
    }
}
