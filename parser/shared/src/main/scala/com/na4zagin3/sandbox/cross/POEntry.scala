package com.na4zagin3.sandbox.cross

case class POFlag private (flag: String)
object POFlag:
    def ofString(flag: String): Option[POFlag] =
      // TODO implement check
      Option(POFlag(flag))

type POReference = String

case class POKey(
    msgctxt: Option[String] = None,
    msgid: String,
)

enum POEntry:
  case Singular(
    comments: Option[String] = None,
    extractedComments: Option[String] = None,
    references: Seq[POReference] = Seq(),
    flags: Set[POFlag] = Set(),
    key: POKey,
    msgstr: String,
  )
  case Plural(
    comments: Option[String] = None,
    extractedComments: Option[String] = None,
    references: Seq[POReference] = Seq(),
    flags: Set[POFlag] = Set(),
    key: POKey,
    msgidPlural: String,
    msgstrs: Seq[String],
  )

type POFile = Seq[POEntry]

object POEntry:
    val TranslatorCommentHeader = "#  "
    val ExtractedCommentHeader = "#. "
    val ReferenceCommentHeader = "#: "
    val FlagCommentHeader = "#, "
