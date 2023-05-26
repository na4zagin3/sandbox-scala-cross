package com.na4zagin3.sandbox.cross

case class POFlag private (flag: String)
object POFlag:
    def ofString(flag: String): Option[POFlag] =
      // TODO implement check
      Option(POFlag(flag))

type POReference = String

enum POEntry:
  case Singular(
    comments: Seq[String] = Seq(),
    extractedComments: Seq[String] = Seq(),
    references: Seq[POReference] = Seq(),
    flags: Set[POFlag] = Set(),
    msgctxt: Option[String] = None,
    msgid: String,
    msgstr: String,
  )
  case Plural(
    comments: Seq[String] = Seq(),
    extractedComments: Seq[String] = Seq(),
    references: Seq[POReference] = Seq(),
    flags: Set[POFlag] = Set(),
    msgctxt: Option[String] = None,
    msgid: String,
    msgidPlural: String,
    msgstrs: Seq[String],
  )

type POFile = Seq[POEntry]
