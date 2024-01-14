package websearch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

typealias URL = String

const val URL_REGEX = "https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)"

class WebPage(private val doc: Document) {
  fun extractWords(): List<String> {
    return doc.body().text().replace(Regex("[^a-zA-Z0-9 ]"), "").split(" ").map { it.lowercase() }.filter { it != "" }
  }

  fun extractLinks(): List<URL> {
    return doc.select("a[href]")
      .mapNotNull { it.absUrl("href") }
      .map(::removeFragment)
      .filter { Regex(URL_REGEX).matches(it) }
      .filter(::checkExtension)
  }

  private fun removeFragment(url: URL): URL {
    return url.substringBefore("#")
              .substringBefore("?")
              .removeSuffix("/")
  }

  private fun checkExtension(url: URL): Boolean {
    val host = url.substringAfter("://").substringBefore("/")
    val path = url.substringAfter(host).substringBefore("?").lowercase()
    return !Regex(".*\\.[a-zA-Z0-9]+$").matches(path) || path.endsWith("html")
  }
}

  fun fromFile(filename: String): WebPage {
    return WebPage(Jsoup.parse(File(filename), "UTF-8"))
  }

// use if you want to index downloaded HTML pages without directly crawling them using this tool.
  fun fromDirectory(directory: String, prefix: String = ""): Map<URL, WebPage> {
    return File(directory).walkTopDown()
      .filter { it.isFile }
      .filter { it.extension == "html" }
      .map { prefix + it.nameWithoutExtension to fromFile(it.absolutePath) }
      .toMap()
  }

 fun filterWords(words: List<String>): List<String> {
  return words.filterNot { it.isEmpty() }
    .filterNot { it.length < 3 }
    .filterNot { stopWords.contains(it) }
    .filterNot { it.contains(Regex("[^a-zA-Z0-9]")) } // only English words
    .filterNot { punctuation.contains(it) }
}


private val punctuation = listOf(
  ",", ".", "!", "?", ";", ":", "(", ")", "[", "]", "{", "}", "<", ">", "/", "\\", "|", "\"", "'", "`", "~", "@", "#", "$", "%", "^", "&", "*", "-", "_", "+", "=", "–", "—"
)

private val stopWords =  listOf(
  "a", "about", "above", "across", "after", "afterwards", "again", "against",
  "all", "almost", "alone", "along", "already", "also", "although", "always",
  "am", "among", "amongst", "amoungst", "amount", "an", "and", "another",
  "any", "anyhow", "anyone", "anything", "anyway", "anywhere", "are",
  "around", "as", "at", "back", "be", "became", "because", "become",
  "becomes", "becoming", "been", "before", "beforehand", "behind", "being",
  "below", "beside", "besides", "between", "beyond", "bill", "both",
  "bottom", "but", "by", "call", "can", "cannot", "cant", "co", "con",
  "could", "couldnt", "cry", "de", "describe", "detail", "do", "done",
  "down", "due", "during", "each", "eg", "eight", "either", "eleven", "else",
  "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone",
  "everything", "everywhere", "except", "few", "fifteen", "fify", "fill",
  "find", "fire", "first", "five", "for", "former", "formerly", "forty",
  "found", "four", "from", "front", "full", "further", "get", "give", "go",
  "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter",
  "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his",
  "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest",
  "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly",
  "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might",
  "mill", "mine", "more", "moreover", "most", "mostly", "move", "much",
  "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless",
  "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing",
  "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto",
  "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out",
  "over", "own", "part", "per", "perhaps", "please", "put", "rather", "re",
  "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several",
  "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so",
  "some", "somehow", "someone", "something", "sometime", "sometimes",
  "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the",
  "their", "them", "themselves", "then", "thence", "there", "thereafter",
  "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv",
  "the", "thickget", "thin", "think", "third", "this", "those", "though",
  "three", "through", "throughout", "thru", "thus", "to", "together", "too",
  "top", "toward", "towards", "twelve", "twenty", "two", "un", "under",
  "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were",
  "what", "whatever", "when", "whence", "whenever", "where", "whereafter",
  "whereas", "whereby", "wherein", "whereupon", "wherever", "whether",
  "which", "while", "whither", "who", "whoever", "whole", "whom", "whose",
  "why", "will", "with", "within", "without", "would", "yet", "you", "your",
  "yours", "yourself", "yourselves", "the"
)