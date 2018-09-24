package org.joaogsma

import org.joaogsma.models.Color
import org.joaogsma.models.DeckEntry
import org.joaogsma.models.Mana

package object metrics
{
  def countCards(entries: Seq[DeckEntry]): Int = entries.map(_.count).sum

  def countTags(entries: Seq[DeckEntry]): Map[String, Int] =
  {
    entries
        .flatMap(card => card.tags.map(_ -> card.count))
        .groupBy(_._1)
        .mapValues(_.map(_._2).sum)
  }

  def countColors(entries: Seq[DeckEntry]): Map[Option[Color], Int] =
  {
    val counts: Map[Option[Color], Int] = entries
        .ensuring(_.forall(_.card.isDefined))
        .flatMap(entry =>
        {
          entry.card.get.colors match {
            case Nil => List(Option.empty -> entry.count)
            case colors => colors.map(Some(_) -> entry.count)
          }
        })
        .groupBy(_._1)
        .mapValues(_.map(_._2).sum)

    val keys = List(
      None,
      Some(Color.White),
      Some(Color.Blue),
      Some(Color.Black),
      Some(Color.Red),
      Some(Color.Green)
    )
    keys.map(key => key -> counts.getOrElse(key, 0)).toMap
  }

  def countManaSymbols(entries: Seq[DeckEntry]): Map[Color, Int] =
  {
    val counts: Map[Color, Int] = entries
        .ensuring(_.forall(_.card.isDefined))
        .flatMap(entry =>
        {
          entry.card.get.manaCost
              .map
              {
                case Mana.White(count) => Option(Color.White -> count * entry.count)
                case Mana.Blue(count) => Option(Color.Blue -> count * entry.count)
                case Mana.Black(count) => Option(Color.Black -> count * entry.count)
                case Mana.Red(count) => Option(Color.Red -> count * entry.count)
                case Mana.Green(count) => Option(Color.Green -> count * entry.count)
                case _ => Option.empty
              }
              .filter(_.isDefined)
              .map(_.get)
        })
        .groupBy(_._1)
        .mapValues(_.map(_._2).sum)

    val keys = List(Color.White, Color.Blue, Color.Black, Color.Red, Color.Green)
    keys.map(key => key -> counts.getOrElse(key, 0)).toMap
  }
}
