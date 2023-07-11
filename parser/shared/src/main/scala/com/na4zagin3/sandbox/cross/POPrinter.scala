package com.na4zagin3.sandbox.cross

import scala.util.parsing.combinator._
import scala.compiletime.ops.string

/** Parses PO File <p> References <ul> <li>
  * https://www.gnu.org/software/gettext/manual/html_node/PO-Files.html <li>
  * https://www.gnu.org/software/gettext/manual/html_node/PO-Files.html </ul>
  */
object POPrinter:

  def literalString(string: String): String =
      val body = string
        .replaceAll("\\\\", "\\\\\\\\")

        .replaceAll("'", "\\\\'")
        .replaceAll("\"", "\\\\\"")
        .replaceAll("\u0007", "\\\\a")
        .replaceAll("\b", "\\\\b")
        .replaceAll("\t", "\\\\t")
        .replaceAll("\n", "\\\\n")
        .replaceAll("\u0009", "\\\\v")
        .replaceAll("\f", "\\\\f")
        .replaceAll("\r", "\\\\r")

      // Support octal-escape-sequence and hexadecimal-escape-sequence, but not universal-character-name

      s"\"$body\""

  def printComment(header: String, comment: String): String =
    """^""".r.replaceAllIn(comment, header)

  def printDirective(header: String, string: String): String =
    header + literalString(string)

  def printSingularEntry(entry: POEntry.Singular) =
    List[Seq[String]](
      entry.comments.map(printComment(POEntry.TranslatorCommentHeader, _)).toSeq,
      entry.extractedComments.map(printComment(POEntry.ExtractedCommentHeader, _)).toSeq,
      entry.references.map(POEntry.ReferenceCommentHeader + _),
      entry.flags.map(flag => POEntry.FlagCommentHeader + flag.flag).toSeq,
      entry.key.msgctxt.map(printDirective("msgctxt ", _)).toSeq,
      List(printDirective("msgid ", entry.key.msgid)),
      List(printDirective("msgstr ", entry.msgstr)),
    )
    .flatten
    .mkString("", "\n", "\n")


  def printPluralEntry(entry: POEntry.Plural) =
    List[Seq[String]](
      entry.comments.map(printComment(POEntry.TranslatorCommentHeader, _)).toSeq,
      entry.extractedComments.map(printComment(POEntry.ExtractedCommentHeader, _)).toSeq,
      entry.references.map(POEntry.ReferenceCommentHeader + _),
      entry.flags.map(flag => POEntry.FlagCommentHeader + flag.flag).toSeq,
      entry.key.msgctxt.map(printDirective("msgctxt ", _)).toSeq,
      List(printDirective("msgid ", entry.key.msgid)),
      List(printDirective("msgid_plural ", entry.msgidPlural)),
      entry.msgstrs.zipWithIndex.map{case (str, index) => printDirective(s"msgstr[$index] ", str)}.toList,
    )
    .flatten
    .mkString("", "\n", "\n")

  def printEntry(entry: POEntry) = entry match {
    case entry: POEntry.Singular => printSingularEntry(entry)
    case entry: POEntry.Plural => printPluralEntry(entry)
  }
