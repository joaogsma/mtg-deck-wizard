package org.joaogsma.ports.deck

import java.io.BufferedWriter

import org.joaogsma.adapters.text.DeckEntryAdapter
import org.joaogsma.models.DeckEntry
import org.joaogsma.ports.FilePort

import scala.util.Failure
import scala.util.Success
import scala.util.Try

object DeckPort extends FilePort {
  def read(filename: String): Try[List[DeckEntry]] = {
    val deckLines: List[String] = usingFile(filename, _.getLines.toList)
    val parsedLines: List[(Try[DeckEntry], Int)] = deckLines
        .map(_.trim)
        .zipWithIndex
        .map { case (line, index) => (line, index + 1) }
        .filter(_._1.nonEmpty)
        .filterNot { case (line, _) => line(0) == '/' && line(1) == '/' } // Comments
        .map { case (line, lineNumber) => (DeckEntryAdapter.parse(line), lineNumber) }

    Try {
      parsedLines
          .map {
            case (Success(deckEntry), _) => deckEntry
            case (Failure(exception), lineNumber) =>
              throw new RuntimeException(s"(line $lineNumber) ${exception.getMessage}")
          }
    }
  }

  def write(entries: Seq[DeckEntry], filename: String): Unit = {
    val sb = new StringBuilder()
    entries
        .map(DeckEntryAdapter.toString)
        .foreach(str => {
          sb.append(str)
          sb.append('\n')
        })

    usingFile(filename, (_: BufferedWriter).write(sb.mkString))
  }
}