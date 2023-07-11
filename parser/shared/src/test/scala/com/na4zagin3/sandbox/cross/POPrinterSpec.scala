package com.na4zagin3.sandbox.cross

import munit.ScalaCheckSuite
import org.scalacheck.Prop._
import org.scalacheck.Gen
import cats.data.NonEmptyList

class POPrinterSpec extends ScalaCheckSuite {
  test("literalString prints a string as a literal string") {
    assertEquals(POPrinter.literalString("abcdef"), "\"abcdef\"")
    assertEquals(POPrinter.literalString("abcdef\n"), "\"abcdef\\n\"")
    assertEquals(POPrinter.literalString(" "), "\" \"")
  }

  property("printer and parser for literalString are invertible for ASCII chars") {
    forAll (Gen.asciiStr) { (input: String) =>
      assertMatches(POParser.literalString, POPrinter.literalString(input), input)
    }
  }

  property("printer and parser for POEntry are invertible") {
    forAll (genPOEntry) { (input: POEntry) =>
      assertMatches(POParser.entry, POPrinter.printEntry(input), input)
    }
  }

  def genPOKey =
    for {
      msgctxt <- Gen.option(Gen.asciiStr)
      msgid <- Gen.asciiStr
    } yield POKey(
      msgctxt = msgctxt,
      msgid = msgid,
    )

  def genPOReference =
    for {
      filepath <- Gen.asciiPrintableStr
      lineNum <- Gen.numStr
    } yield s"$filepath:$lineNum"

  def genFlag =
    Gen.oneOf(
      POFlag.ofString("fuzzy"),
    )

  def genPOEntrySingular =
    for {
      comments <- Gen.option(Gen.asciiStr)
      extractedComments <- Gen.option(Gen.asciiStr)
      references <- Gen.listOf(genPOReference)
      flags <- Gen.listOf(genFlag).map(_.toSet)
      key <- genPOKey
      msgstr <- Gen.asciiStr
    } yield POEntry.Singular(
      comments = comments,
      extractedComments = extractedComments,
      references = references,
      flags = flags,
      key = key,
      msgstr = msgstr,
    )

  def genPOEntryPlural =
    for {
      comments <- Gen.option(Gen.asciiStr)
      extractedComments <- Gen.option(Gen.asciiStr)
      references <- Gen.listOf(genPOReference)
      flags <- Gen.listOf(genFlag).map(_.toSet)
      key <- genPOKey
      msgidPlural <- Gen.asciiStr
      msgstr <- Gen.asciiStr
      msgstrs <- Gen.listOf(Gen.asciiStr)
    } yield POEntry.Plural(
      comments = comments,
      extractedComments = extractedComments,
      references = references,
      flags = flags,
      key = key,
      msgidPlural = msgidPlural,
      msgstrs = NonEmptyList.of(msgstr, msgstrs: _*),
    )

  def genPOEntry =
    Gen.oneOf(genPOEntrySingular, genPOEntryPlural)

  def assertMatches[T](parser: POParser.Parser[T], input: String, expected: T) =
    import POParser._
    parse(parser, input) match {
      case Success(matched, _) =>
        assertEquals(matched, expected)
      case result =>
        fail("failed to parse", clues(input, expected, result))
    }
}

