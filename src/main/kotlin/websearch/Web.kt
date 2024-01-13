package websearch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

typealias URL = String

const val URL_REGEX = "https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)"

class WebPage(private val doc: Document) {
  fun extractWords(): List<String> {
    return doc.body().text().replace(Regex("[^a-zA-Z0-9 ]"), "").split(" ").map { it.lowercase() }.filter { it != "" }
  }

  fun extractLinks(): List<URL> {
    return doc.select("a[href]").mapNotNull { it.absUrl("href") }
      .filter { Regex(URL_REGEX).matches(it) }
      .filter(::checkExtension)

  }

  private fun checkExtension(url: URL): Boolean {
    val host = url.substringAfter("://").substringBefore("/")
    val path = url.substringAfter(host).substringBefore("?").lowercase()
    return !Regex(".*\\.[a-zA-Z0-9]+$").matches(path) || path.endsWith("html")
  }
}
