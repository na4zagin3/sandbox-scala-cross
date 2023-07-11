package com.na4zagin3.sandbox.cross

import scala.util.parsing.combinator.Parsers

class POParserSpec extends munit.FunSuite {
  import POParser._

  test("literalStringBody matches a string without surrounding quotes") {
    assertMatches(POParser.literalStringBody, "abcdef", "abcdef")
    assertMatches(POParser.literalStringBody, "abc\\\"def", "abc\"def")
    assertMatches(POParser.literalStringBody, "", "")
    assertMatches(POParser.literalStringBody, " ", " ")
  }

  test("literalString matches a string with surrounding quotes") {
    assertMatches(POParser.literalString, "\"abcdef\"", "abcdef")
    assertMatches(POParser.literalString, "\"abc\\\"def\"", "abc\"def")
    assertMatches(POParser.literalString, "\"\"", "")
    assertMatches(POParser.literalString, "\" \"", " ")
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
      POParser.entry,
      """msgid "abc"
msgstr "def"
""",
      POEntry.Singular(key = POKey(msgid = "abc"), msgstr = "def")
    )
    assertMatches(
      POParser.entry,
      """#  translator comment
msgid "abc"
msgstr "def"
""",
      POEntry.Singular(
        comments = Option("translator comment"),
        key = POKey(
          msgid = "abc"
        ),
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
