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
