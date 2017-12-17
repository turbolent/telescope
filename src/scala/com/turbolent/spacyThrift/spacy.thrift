namespace java com.turbolent.spacyThrift
#@namespace scala com.turbolent.spacyThrift

struct Token {
  1: string word,
  2: string tag,
  3: string lemma
}

service SpacyThriftService {
  list<Token> tag(1: string sentence)
}
