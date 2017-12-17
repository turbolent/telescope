package com.turbolent.questionCompiler

import com.turbolent.questionParser.Token


trait Ontology[N, E, Env <: Environment[N, E]] {

  type Node = graph.Node[N, E]
  type Edge = graph.Edge[E, N]

  /** Return an edge which will identify a node representing the subject
    * to be a person.
    *
    * Invoked for ast.PersonListQuestion, i.e., when the question starts
    * with "who", e.g., for the question "who died?".
    *
    * For example you could return an instance-of edge:
    *
    *   out('instanceOf, 'Person)
    */
  def makePersonEdge(env: Env): Edge


  /** Return an edge which will identify a node representing the subject
    * to have the property given by 'name'. 'node' is the object.
    *
    * Invoked for ast.NamedProperty, i.e., when the subject has a simple
    * property without a filter, e.g., for the question "who wrote?".
    *
    * For this example, 'name' would be [Token('wrote', 'VBD', 'write')]
    * and you could return a simple edge:
    *
    *   in(node, 'hasAuthor)
    */
  def makeNamedPropertyEdge(name: Seq[Token], node: Node,
                            subject: Subject, env: Env): Edge


  /** Return and edge which will identify a node representing the subject
    * (given in the context) to have a property given by 'name'.
    * 'node' is the object.
    *
    * Invoked for ast.InversePropertyWithFilter, i.e., when the subject
    * has an inverse property with a filter (given in the context),
    * e.g., for the question "what books did Shakespeare write?".
    *
    * For this example, the object ('node') is representing Shakespeare,
    * the subject (given in the context) is representing a book,'name'
    * would be [Token("did", "VBD", "do"), Token("write", "VB", "write")],
    * and you could return an edge:
    *
    *   out('hasAuthor, node)
    */
  def makeInversePropertyEdge(name: Seq[Token], node: Node,
                              context: EdgeContext, env: Env): Edge


  /** Return and edge which will identify a node representing the subject
    * to have the property given by 'name', which contains an adjective.
    * 'node' is the object.
    *
    * Invoked for ast.AdjectivePropertyWithFilter, i.e., when the subject
    * has a property with an adjective, e.g., for the question
    * "who is 42 years old?".
    *
    * For this example, 'name' would be
    * [Token("is", "VBP", "be"), Token("old", "JJ", "old")]
    * and you could return an edge:
    *
    *   out('hasAge, node)
    */
  def makeAdjectivePropertyEdge(name: Seq[Token], node: Node,
                                context: EdgeContext, env: Env): Edge


  /** Return an edge which will identify the node representing the subject
    * to have the property given by 'name', which compares to 'node',
    * which is the object.
    *
    * Invoked for ast.PropertyWithFilter with ast.FilterWithComparativeModifier,
    * i.e., when the property contains a comparative filter,
    * e.g., for the question "who is older than Obama?".
    *
    * For this example, 'name' would be [Token("is", "VBD", "be")],
    * the 'context' 'filter' field would contain
    * [Token("older", "JJR", "old"), Token("than", "IN", "than")],
    * and you could return an edge:
    *
    *   out('hasAge, ageNode.filter(filter))
    *
    * with
    *
    *   filter = GreaterThanFilter(otherAgeNode.in(node, 'hasAge))
    */
  def makeComparativePropertyEdge(name: Seq[Token], node: Node,
                                  context: EdgeContext, env: Env): Edge


  /** Return an edge which will identify the node representing the subject
    * to have the property given by 'name'. 'node' is the object.
    *
    * Invoked for ast.PropertyWithFilter with any filter but  ast.PlainFilter
    * or ast.FilterWithModifier, i.e., when the filter is not comparative,
    * e.g., for the question "who wrote Macbeth?".
    *
    * For this example, 'name' would be [Token("married", "VBD", "marry")],
    * and you could return an edge:
    *
    *   in(node, 'hasAuthor)
    */
  def makeValuePropertyEdge(name: Seq[Token], node: Node,
                            context: EdgeContext, env: Env): Edge


  /** Return an edge which will identify a node to have a possessive
    * relationship to 'node'.
    *
    * Invoked for ast.RelationshipValue, i.e., when the object/value
    * in a question contains a possessive relationship,
    * e.g., for the question "who married Clinton's daughter?".
    *
    * For this example, 'name' would be [Token("daughter", "NN", "daughter")]
    * and you could return an edge:
    *
    *   in(node, 'hasDaughter)
    */
  def makeRelationshipEdge(name: Seq[Token], node: Node, env: Env): Edge


  /** Return a node for the given 'name'.
    *
    * Invoked for ast.NamedQuery and ast.NamedValue, i.e., for the subject
    * or object of a question. 'filter' is given for context purposes and
    * might be Nil.
    *
    * For example, this method is invoked for the questions
    * "which authors were born before 2000?" and "who lived in Berlin?".
    *
    * For the first example, 'name' would be [Token("authors", "NNS", "author")]
    * and you could return an anonymous node with the edge:
    *
    *   out('instanceOf, 'Author)
    *
    * For the second example, 'name' would be [Token("Berlin", "NNP", "Berlin")]
    * and you could return an anonymous node with the edge:
    *
    *   out('hasName, "Berlin")
    */
  def makeValueNode(name: Seq[Token], filter: Seq[Token], env: Env): Node


  /** Return a node for the given 'number' and 'unit'.
    *
    * Invoked for ast.Number and ast.NumberWithUnit, i.e., when the question
    * contains a numeric value with an optional unit, e.g., for the question
    * "who was born in 1900?".
    *
    * For this example, 'number' would be [Token("1900", "CD", "1900")],
    * 'unit' would be Nil, and you could return a node:
    *
    *   Node(1900)
    *
    * Note that 'number' may contain spelled out numbers, and 'unit' a standard,
    * e.g. for "two million inhabitants" 'name' would be
    * [Token("two", "CD", "two"), Token("million", "CD", "million")] and
    * 'unit' would be [Token("inhabitants", "NNS", "inhabitant")].
    */
  def makeNumberNode(number: Seq[Token], unit: Seq[Token],
                     filter: Seq[Token], env: Env): Node

}
