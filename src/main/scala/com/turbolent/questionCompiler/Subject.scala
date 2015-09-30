package com.turbolent.questionCompiler

import com.turbolent.questionParser.Token


trait Subject

case class NamedSubject(name: Seq[Token]) extends Subject

case object ThingSubject extends Subject

case object PersonSubject extends Subject
