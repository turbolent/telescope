package com.turbolent.questionCompiler.graph


sealed trait Order

case object Ascending extends Order
case object Descending extends Order
