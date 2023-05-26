package com.na4zagin3.sandbox.cross

import scala.util.parsing.combinator._

/** Parses PO File
  * <p>
  * References
  * <ul>
  * <li> https://www.gnu.org/software/gettext/manual/html_node/PO-Files.html
  * <li> https://www.gnu.org/software/gettext/manual/html_node/PO-Files.html
  * </ul>
  */
object POParser extends RegexParsers:
    def literalStringBody: Parser[String] = """(?:[^\\"]|\\.)*""".r ^^ { matched =>
      // TODO Optimize
      // See C Language Spec sec 6.4.5
      val replaced = matched
        .replaceAll("\\\\'", "'")
        .replaceAll("\\\\\"", "\"")
        .replaceAll("\\\\a", "\u0007")
        .replaceAll("\\\\b", "\b")
        .replaceAll("\\\\t", "\t")
        .replaceAll("\\\\n", "\n")
        .replaceAll("\\\\v", "\u0009")
        .replaceAll("\\\\f", "\f")
        .replaceAll("\\\\r", "\r")

        .replaceAll("\\\\\\\\", "\\\\")

      // Support octal-escape-sequence and hexadecimal-escape-sequence, but not universal-character-name

      replaced
    }

    def literalString: Parser[String] = "\"" ~> literalStringBody <~ "\""
    def consequenctLiteralStrings: Parser[String] = repsep(literalString, """\s*""".r) ^^ {
      _.mkString("")
    }

    def flag: Parser[POFlag] = """[-\w]+""".r ^^ { POFlag.ofString(_).get }

    def commentLine(header: Parser[String]): Parser[String] = header ~> """[^\n]*""".r <~ "\n"
    def translatorCommentLine: Parser[String] = commentLine("#  ")
    def extractedCommentLine: Parser[String] = commentLine("#. ")
    def referenceLine: Parser[String] = commentLine("#: ")
    def flagsLine: Parser[Seq[POFlag]] = "#, " ~> repsep(flag, ",") <~ "\n"

    def msgid: Parser[String] =
      "msgid " ~> consequenctLiteralStrings
    def msgidPlural: Parser[String] =
      "msgid_plural " ~> consequenctLiteralStrings
    def msgstr: Parser[String] =
      "msgstr " ~> consequenctLiteralStrings

    def singularEntry: Parser[POEntry.Singular] =
      for {
        trCom <- rep(translatorCommentLine)
        id <- msgid
        str <- msgstr
      } yield POEntry.Singular(
        comments = trCom,
        msgid = id,
        msgstr = str,
      )


    def entries: Parser[List[POEntry]] =
      repsep(singularEntry, """\s*""")
