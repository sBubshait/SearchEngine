package websearch

import org.jsoup.Jsoup

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
    val currentPage = download(url)
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
    wp: WebPage = download(url)
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

private fun download(url: URL): WebPage {
  return try {
    WebPage(Jsoup.connect(url).get())
  } catch (e: Exception) {
    WebPage(Jsoup.parse(""))
  }
}
fun main() {
//  val crawler = WebCrawler(startingURL = "http://www.bbc.co.uk")
//  crawler.run()
//  val searchEngine = SearchEngine(crawler.dump())
//  searchEngine.compileIndex()
//  val searchEngine = SearchEngine().loadIndex("index")
//  println(searchEngine.searchFor("news"))
//  println(searchEngine.searchFor("news"))
//  searchEngine.saveIndex("index")
//  val crawler = WebCrawler(startingURL = "https://www.bbc.co.uk/news", maximumPages = 20)
//  crawler.run()
//  println(crawler.dump())
}
