package websearch

import org.junit.Test
import kotlin.test.assertEquals

class CrawlerTests {
  @Test
  fun `can crawl a single page`() {
    val crawler =
      WebCrawler(
        "https://www.doc.ic.ac.uk/~sb3923/crawlerTesting",
        maximumPages = 1
      )
    crawler.run()
    val result = crawler.dump()
    assertEquals(1, result.size)
    assertEquals(
      "https://www.doc.ic.ac.uk/~sb3923/crawlerTesting",
      result.keys.first()
    )
    val searchEngine = SearchEngine(result)
    searchEngine.compileIndex()
    val summary = searchEngine.searchFor("testing")
    assertEquals("testing", summary.query)
    assertEquals(1, summary.results.size)
    assertResultsMatch(
      "https://www.doc.ic.ac.uk/~sb3923/crawlerTesting",
      2,
      summary.results[0]
    )
  }

  @Test
  fun `can extract all links recursively, with limit`() {
    val crawler =
      WebCrawler(
        "https://www.doc.ic.ac.uk/~sb3923/crawlerTesting",
        maximumPages = 4
      )
    crawler.run()
    val result = crawler.dump()
    // results now include relative links as well :)
    assertEquals(4, result.size)
    assertEquals(
      "https://www.doc.ic.ac.uk/~sb3923/crawlerTesting",
      result.keys.first()
    )
    val searchEngine = SearchEngine(result)
    searchEngine.compileIndex()
    val summary = searchEngine.searchFor("testing")
    assertEquals("testing", summary.query)
    assertEquals(1, summary.results.size) // one page only, home page.
    assertResultsMatch(
      "https://www.doc.ic.ac.uk/~sb3923/crawlerTesting",
      2,
      summary.results[0]
    )
  }

  private fun assertResultsMatch(
    url: URL,
    count: Int,
    result: SearchResult
  ) {
    assertEquals(url, result.url)
    assertEquals(count, result.numRefs)
  }
}
