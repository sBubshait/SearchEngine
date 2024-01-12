package websearch

class SearchEngine(private val data: Map<URL, WebPage>) {
  var index: Map<String, List<SearchResult>> = emptyMap()

  fun compileIndex() {
    val pairs =
      data.flatMap {
          (url, page) ->
        page.extractWords().map { it to url }
      }
    val grouped = pairs.groupBy({ it.first }, { it.second })

    index =
      grouped.map {
        it.key to
          rank(it.value).sortedByDescending {
              searchRes ->
            searchRes.numRefs
          }
      }.toMap()
  }

  private fun rank(urls: List<URL>): List<SearchResult> {
    return urls.groupingBy {
      it.url
    }.eachCount().map { SearchResult(URL(it.key), it.value) }
  }

  fun searchFor(query: String): SearchResultSummary {
    return SearchResultSummary(query, index.getOrDefault(query, listOf()))
  }
}

class SearchResult(val url: URL, val numRefs: Int) {
  override fun toString(): String {
    return "$url - $numRefs references"
  }
}

class SearchResultSummary(val query: String, val results: List<SearchResult>) {
  override fun toString(): String {
    return "Results for \"$query\":\n" + results.joinToString("\n") { " $it" }
  }
}
