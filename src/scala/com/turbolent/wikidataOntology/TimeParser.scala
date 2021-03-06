package com.turbolent.wikidataOntology

import java.time.Year
import java.time.temporal.Temporal

import scala.util.matching.Regex

object TimeParser {

  val yearPattern: Regex = """[12]\d{3}""".r

  // TODO: extend
  def parseTemporal(name: String): Option[Temporal] =
    name match {
      case yearPattern(_*) => Some(Year.parse(name))
      case _               => None
    }
}
