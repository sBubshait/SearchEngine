package websearch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

const val URL_REGEX = "https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)"

class URL(val url: String) {
  fun download(): WebPage {
    return try {
      WebPage(Jsoup.connect(url).get())
    } catch (e: Exception) {
      WebPage(Jsoup.parse(""))
    }
  }

  override fun toString(): String {
    return url
  }

  override fun equals(other: Any?): Boolean {
    return url == (other as URL).url
  }
}

class WebPage(private val doc: Document) {
  fun extractWords(): List<String> {
    return doc.body().text().replace(Regex("[^a-zA-Z0-9 ]"), "").split(" ").map { it.lowercase() }.filter { it != "" }
  }

  fun extractLinks(): List<URL> {
    return doc.select("a[href]").mapNotNull { it?.attr("href") }
      .filter { Regex(URL_REGEX).matches(it) }
      .filter(::checkExtension)
      .map { URL(it) }
  }

  // handles relative links: e.g., starting with /, or directly file name, e.g., index.html
//  fun handleRelativeLinks(url: String, originalLink: String): String = TODO()

  private fun checkExtension(url: String): Boolean {
    val host = url.substringAfter("://").substringBefore("/")
    val path = url.substringAfter(host).substringBefore("?").lowercase()
    return !Regex(".*\\.[a-zA-Z0-9]+$").matches(path) || path.endsWith("html")
  }
}
