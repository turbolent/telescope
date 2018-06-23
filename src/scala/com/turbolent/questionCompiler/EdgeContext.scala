package com.turbolent.questionCompiler

import com.turbolent.questionParser.Token

case class EdgeContext(subject: Subject,
                       filter: Seq[Token],
                       value: Seq[Token],
                       unit: Seq[Token],
                       valueIsNumber: Boolean)
