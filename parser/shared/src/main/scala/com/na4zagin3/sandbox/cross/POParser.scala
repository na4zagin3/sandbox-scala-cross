package com.na4zagin3.sandbox.cross

import cats._
import cats.data._
import cats.syntax.all._
import scala.util.parsing.combinator._

/** Parses PO File <p> References <ul> <li>
  * https://www.gnu.org/software/gettext/manual/html_node/PO-Files.html <li>
  * https://www.gnu.org/software/gettext/manual/html_node/PO-Files.html </ul>
  */
object POParser extends RegexParsers:
  override def skipWhitespace = false

  def literalStringNormalChar: Parser[String] = """[^\\"\n]""".r
  def literalStringEscapedChar: Parser[String] = """\\[\\'"abtnvfr]""".r ^^ {
    // TODO Optimize
    // See C Language Spec sec 6.4.5
    case "\\'"  => "'"
    case "\\\"" => "\""
    case "\\\\" => "\\"
    case "\\a"  => "\u0007"
    case "\\b"  => "\b"
    case "\\t"  => "\t"
    case "\\n"  => "\n"
    case "\\v"  => "\u0009"
    case "\\f"  => "\f"
    case "\\r"  => "\r"
    // TODO Ocatal
    // TODO Hexadecimal
  }

  def literalStringBody: Parser[String] =
    rep(literalStringEscapedChar | literalStringNormalChar) ^^ { matched =>
      matched.mkString("")
    }

  def literalString: Parser[String] = "\"" ~> literalStringBody <~ "\""
  def consequenctLiteralStrings: Parser[String] =
    repsep(literalString, """\s*""".r) ^^ {
      _.mkString("")
    }

  def flag: Parser[POFlag] = """[-\w]+""".r ^^ { POFlag.ofString(_).get }

  def commentLine(header: Parser[String]): Parser[String] =
    header ~> """[^\n]*""".r <~ "\n"
  def commentLines(header: Parser[String]): Parser[String] =
    rep1(commentLine(header)) ^^ { lines =>
      lines.mkString("\n")
    }
  def translatorCommentLines: Parser[String] = commentLines(
    POEntry.TranslatorCommentHeader
  )
  def extractedCommentLines: Parser[String] = commentLines(
    POEntry.ExtractedCommentHeader
  )
  def referenceLines: Parser[Seq[String]] = rep(
    commentLine(POEntry.ReferenceCommentHeader)
  )
  def flagsLines: Parser[Set[POFlag]] =
    rep(POEntry.FlagCommentHeader ~> repsep(flag, ",") <~ "\n") ^^ {
      Monoid.combineAll(_).toSet
    }

  def directive[T](keyword: Parser[T]): Parser[T ~ String] =
    keyword ~ (" " ~> consequenctLiteralStrings <~ "\n")

  def msgctxt: Parser[String] =
    directive("msgctxt") ^^ { case index ~ str => str }
  def msgid: Parser[String] =
    directive("msgid") ^^ { case index ~ str => str }
  def msgidPlural: Parser[String] =
    directive("msgid_plural") ^^ { case index ~ str => str }
  def msgstr: Parser[String] =
    directive("msgstr") ^^ { case index ~ str => str }
  def msgstrsHeader: Parser[Int] =
    ("msgstr[" ~> """0|[1-9][0-9]*""".r <~ "]") ^^ { _.toInt }
  def msgstrs: Parser[Seq[String]] =
    rep(directive(msgstrsHeader)) ^^ { matched =>
      val map = matched.map { case index ~ str => index -> str }.toMap
      // TODO Validate
      map.toList.sorted.map(_._2)
    }

  def entry: Parser[POEntry] =
    for {
      trComs <- opt(translatorCommentLines)
      extComs <- opt(extractedCommentLines)
      refs <- referenceLines
      flags <- flagsLines
      ctxt <- opt(msgctxt)
      id <- msgid
      idPlOpt <- opt(msgidPlural)
      str <- (msgstr ^^ { Left(_) }) ||| (msgstrs ^^ { Right(_) })
      key = POKey(ctxt, id)
      entry <- (str, idPlOpt) match {
        case (Left(str), None) =>
          success(
            POEntry.Singular(
              comments = trComs,
              extractedComments = extComs,
              references = refs,
              flags = flags,
              key = key,
              msgstr = str
            )
          )
        case (Right(strHead :: strTail), Some(idPl)) =>
          success(
            POEntry.Plural(
              comments = trComs,
              extractedComments = extComs,
              references = refs,
              flags = flags,
              key = key,
              msgidPlural = idPl,
              msgstrs = NonEmptyList.of(strHead, strTail: _*)
            )
          )
        case (strs, ids) =>
          failure(s"Combination of msgstr $strs and msgid $ids is not allowed")
      }
    } yield entry
  def entries: Parser[List[POEntry]] =
    repsep(entry, """\s*""")
