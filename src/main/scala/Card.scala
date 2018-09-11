import scala.util.matching.Regex

case class Card(count: Int, tags: Set[String])
object Card {
  def apply(line: String): Option[Card] = {
    Option(getCount(line))
        .filter(_ > 0)
        .map(Card(_, getTags(line)))
  }

  private val CARD_COUNT_REGEX = "^\\[\\d+\\]".r
  private val TAG_GROUP_REGEX = "(@[^ @]+( @[^ @]+)*)*$".r
  private val CARD_TAG_REGEX = "@[^ @]+".r

  private def getTags(line: String) = {
    TAG_GROUP_REGEX
        .findFirstIn(line)
        .filterNot(_.isEmpty)
        .map(CARD_TAG_REGEX.findAllIn(_).toSet[String].map(_.replaceFirst("@", "")))
        .getOrElse(Set.empty[String])
  }

  private def getCount(line: String) = {
    CARD_COUNT_REGEX
        .findFirstIn(line)
        .getOrElse("0")
        .replaceAll("\\]|\\[", "")
        .toInt
  }
}

