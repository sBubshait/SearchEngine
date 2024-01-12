package websearch

import org.jsoup.Jsoup
import org.junit.Test
import kotlin.test.assertEquals

class SearchEngineTest {
  private val testingMain =
    """<html>
        <head>
          <title>Crawler Testing</title>
        </head>
        <body>
          <h1>Welcome to Crawler Testing!</h1>
          <p>Explore these sections:</p>
          <ul>
                  <!-- We try to make the links different -->
                  <li><a href="https://www.doc.ic.ac.uk/~sb3923/crawlerTesting/nature.html">Nature</a></li>
            <li><a href="/~sb3923/crawlerTesting/tech.html">Technology</a></li>
            <li><a href="art.html">Art</a></li>
          </ul>
        
          <p> please note, this is for testing purposes only, please return to the main page if you want. </p>
          <p>The following link should not be crawled for example:</p>
          <a href="https://www.doc.ic.ac.uk/~sb3923/crawlerTesting/notForCrawler.pdf">Download my nice document</a>
        </body>
    </html>"""

  private val testingNature =
    """<html>
        <head>
          <title>Nature</title>
        </head>
        <body>
          <h1>Appreciating Nature's Beauty</h1>
          <p>Discover the wonders of the natural world and its serene landscapes.</p>
          <p>Common words: nature, beauty</p>
        </body>
    </html>"""

  private val testingTech =
    """<html>
          <head>
            <title>Technology</title>
          </head>
          <body>
            <h1>Embracing Innovation</h1>
            <p>Explore the latest advancements in technology and how they shape our future.</p>
            <p>Common words: technology, innovation</p>
            <a href="https://www.doc.ic.ac.uk/~sb3923//crawlerTesting/ai.html">Go To AI</a>
          </body>
    </html>"""

  private val testingMainPage = WebPage(Jsoup.parse(testingMain))
  private val testingNaturePage = WebPage(Jsoup.parse(testingNature))
  private val testingTechPage = WebPage(Jsoup.parse(testingTech))

  private val downloadedPages =
    mapOf(
      URL("https://www.doc.ic.ac.uk/~sb3923/crawlerTesting") to testingMainPage,
      URL("https://www.doc.ic.ac.uk/~sb3923/crawlerTesting/nature.html") to testingNaturePage,
      URL("https://www.doc.ic.ac.uk/~sb3923/crawlerTesting/tech.html") to testingTechPage
    )

  @Test
  fun `can index downloaded pages`() {
    val searchEngine = SearchEngine(downloadedPages)
    searchEngine.compileIndex()
    val summary = searchEngine.searchFor("technology")

    assertEquals("technology", summary.query)
    assertEquals(2, summary.results.size)
    // notice the order (sorted, by default, by number of references)
    // also no case sensitivity
    assertResultsMatch("https://www.doc.ic.ac.uk/~sb3923/crawlerTesting/tech.html", 2, summary.results[0])
    assertResultsMatch("https://www.doc.ic.ac.uk/~sb3923/crawlerTesting", 1, summary.results[1])
  }

  private fun assertResultsMatch(
    expectedUrl: String,
    numRefs: Int,
    searchResult: SearchResult
  ) {
    assertEquals(expectedUrl, searchResult.url.toString())
    assertEquals(numRefs, searchResult.numRefs)
  }
}
