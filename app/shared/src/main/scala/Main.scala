import com.na4zagin3.sandbox.cross.POEntry

import scala.collection

object Main {

  def main(args: Array[String]): Unit = {
    println("Hello world!")
    println(msg)

    val entry = POEntry.Singular(
      msgid = "abc",
      msgstr = "str"
    )
    println(entry)

    println(Seq(args: _*))
  }

  def msg = "I was compiled by Scala 3. :)"

}
