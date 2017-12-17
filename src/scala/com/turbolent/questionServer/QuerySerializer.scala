package com.turbolent.questionServer

import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString
import org.apache.jena.query.{QueryFactory, Query => JenaQuery}

class QuerySerializer extends CustomSerializer[JenaQuery](format => (
    {
      case JString(query) =>
        QueryFactory.create(query)
    },
    {
      case query: JenaQuery =>
        JString(query.toString)
    }))