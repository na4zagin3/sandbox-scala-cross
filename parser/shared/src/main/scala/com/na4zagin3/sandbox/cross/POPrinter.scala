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
    List[Option[String]](
      entry.comments.map(printComment(POEntry.TranslatorCommentHeader, _)),
      entry.extractedComments.map(printComment(POEntry.ExtractedCommentHeader, _)),
      Some(entry.references.toList.map(POEntry.ReferenceCommentHeader + _).mkString("\n")),
      entry.key.msgctxt.map(printDirective("msgctxt ", _)),
      Some(entry.key.msgid).map(printDirective("msgid ", _)),
      Some(entry.msgstr).map(printDirective("msgstr ", _)),
    )
    .flatten
    .mkString("\n")


  def printPluralEntry(entry: POEntry.Plural) =
    List[Option[String]](
      entry.comments.map(printComment(POEntry.TranslatorCommentHeader, _)),
      entry.extractedComments.map(printComment(POEntry.ExtractedCommentHeader, _)),
      Some(entry.references.toList.map(POEntry.ReferenceCommentHeader + _).mkString("\n")),
      entry.key.msgctxt.map(printDirective("msgctxt ", _)),
      Some(entry.key.msgid).map(printDirective("msgid ", _)),
      Some(entry.msgidPlural).map(printDirective("msgid_plural ", _)),
      Some(entry.msgstrs.zipWithIndex.map{case (str, index) => printDirective(s"msgstr[$index] ", str)}.mkString("\n")),
    )
    .flatten
    .mkString("\n")

  def printEntry(entry: POEntry) = entry match {
    case entry: POEntry.Singular => printSingularEntry(entry)
    case entry: POEntry.Plural => printPluralEntry(entry)
  }
