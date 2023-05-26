package com.na4zagin3.sandbox.cross

import munit.ScalaCheckSuite
import org.scalacheck.Prop._
import org.scalacheck.Gen

class POPrinterSpec extends ScalaCheckSuite {
  test("literalString prints a string as a literal string") {
    assertEquals(POPrinter.literalString("abcdef"), "\"abcdef\"")
    assertEquals(POPrinter.literalString("abcdef\n"), "\"abcdef\\n\"")
  }

  property("printer and parser for literalString are invertible for ASCII chars") {
    forAll (Gen.asciiStr) { (input: String) =>
      assertMatches(POParser.literalString, POPrinter.literalString(input), input)
    }
  }

  def assertMatches[T](parser: POParser.Parser[T], input: String, expected: T) =
    import POParser._
    parse(parser, input) match {
      case Success(matched, _) =>
        assertEquals(matched, expected)
      case result =>
        fail("failed to parse", clues(result))
    }
}

