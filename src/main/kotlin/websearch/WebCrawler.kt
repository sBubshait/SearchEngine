package websearch

class WebCrawler(
  private val startingURL: URL,
  private var maximumPages: Int = 10,
  private val exclude: MutableList<URL> = mutableListOf()
) {
  private val data: MutableMap<URL, WebPage> = mutableMapOf()

  fun run() {
    println("🕷️ Starting crawling... 🕸️")
    crawl(startingURL)
    println("🎉 Crawling Ended Successfully! 🎉")
  }

  private fun crawl(url: URL) {
    val currentPage = url.download()
    crawlOnePage(url, currentPage)
    val links = currentPage.extractLinks().filterNot { exclude.contains(it) }
    links.take(if (maximumPages < 0) 0 else maximumPages).forEach {
      crawlOnePage(it)
    }
    links.take(if (maximumPages < 0) 0 else maximumPages).forEach { crawl(it) }
    // We do forEach twice instead of just once with crawl(it) because we want to crawl the pages in the order they appear in the page before we start crawling the inner pages.
  }

  private fun crawlOnePage(
    url: URL,
    wp: WebPage = url.download()
  ) {
    if (maximumPages-- <= 0 || exclude.contains(url))
      return
    data += url to wp
    exclude += url
  }

  fun dump(): Map<URL, WebPage> {
    return data.toMap()
  }
}

fun main() {
  val crawler = WebCrawler(startingURL = URL("http://www.bbc.co.uk"))
  crawler.run()
  val searchEngine = SearchEngine(crawler.dump())
  searchEngine.compileIndex()
  println(searchEngine.searchFor("news"))
}
