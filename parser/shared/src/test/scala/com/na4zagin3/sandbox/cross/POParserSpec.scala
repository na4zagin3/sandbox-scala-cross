package com.na4zagin3.sandbox.cross

import scala.util.parsing.combinator.Parsers

class POParserSpec extends munit.FunSuite {
  import POParser._

  test("literalStringBody matches a string without surrounding quotes") {
    assertMatches(POParser.literalStringBody, "abcdef", "abcdef")
    assertMatches(POParser.literalStringBody, "abc\\\"def", "abc\"def")
  }

  test("literalString matches a string with surrounding quotes") {
    assertMatches(POParser.literalString, "\"abcdef\"", "abcdef")
    assertMatches(POParser.literalString, "\"abc\\\"def\"", "abc\"def")
  }

  test("consequenctLiteralStrings matches a string with surrounding quotes") {
    assertMatches(
      POParser.consequenctLiteralStrings,
      "\"abc\"\n  \"def\"",
      "abcdef"
    )
  }

  test("flag matches a flag") {
    assertMatches(POParser.flag, "abc", POFlag.ofString("abc").get)
    assertMatches(POParser.flag, "new-flag", POFlag.ofString("new-flag").get)
  }

  test("singular matches a singular entry") {
    assertMatches(
      POParser.singularEntry,
      """msgid "abc"
msgstr "def"
""",
      POEntry.Singular(msgid = "abc", msgstr = "def")
    )
    assertMatches(
      POParser.singularEntry,
      """#  translator comment
msgid "abc"
msgstr "def"
""",
      POEntry.Singular(
        comments = Seq("translator comment"),
        msgid = "abc",
        msgstr = "def"
      )
    )
  }

  def assertMatches[T](parser: Parser[T], input: String, expected: T) =
    parse(parser, input) match {
      case Success(matched, _) =>
        assertEquals(matched, expected)
      case result =>
        fail("failed to parse", clues(result))
    }
}
