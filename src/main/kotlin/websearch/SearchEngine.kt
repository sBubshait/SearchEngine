package websearch

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.JsonArray
import java.io.File

class SearchEngine(private val data: Map<URL, WebPage> = emptyMap(),
                   private var pageCount : Int = data.size) {

  private var index: Map<String, List<SearchResult>> = emptyMap()

  fun compileIndex() {
    val pairs =
      data.flatMap { (url, page) ->
        filterWords(page.extractWords()).map { it to url }
      }
    val grouped = pairs.groupBy({ it.first }, { it.second })

    index =
      grouped.map {
        it.key to
          rank(it.value).sortedByDescending { searchRes ->
            searchRes.numRefs
          }
      }.toMap()
  }

  private fun rank(urls: List<URL>): List<SearchResult> {
    return urls.groupingBy {
      it
    }.eachCount().map { SearchResult(it.key, it.value) }
  }

  fun searchFor(query: String): SearchResultSummary {
    val start = System.currentTimeMillis()

    val words = filterWords (query.lowercase().split(" ").map { it.trim() })

    val aggregatedResults = words.flatMap { word ->
      index.getOrDefault(word, listOf())
    }.groupBy { it.url }
      .mapValues { (_, results) ->
        results.fold(0) { sum, result -> sum + result.numRefs }
      }
      .map { SearchResult(it.key, it.value) }

    val result = aggregatedResults.sortedByDescending { it.numRefs }

    val end = System.currentTimeMillis()
    return SearchResultSummary(query, result, pageCount, end - start)
  }

  fun saveIndex(filename: String) {
    val json = JsonObject().apply {
      put("pageCount", pageCount)
      put("index", JsonObject(index))
    }

    File("$filename.json").writeText(json.toJsonString())
  }

  fun loadIndex(filename: String): SearchEngine {
    val parser: Parser = Parser.default()
    val json: JsonObject =
      parser.parse(StringBuilder(File("$filename.json").readText())) as JsonObject

    pageCount = json.int("pageCount")
      ?: throw IllegalArgumentException("Page count missing")
    val jsonIndex =
      json.obj("index") ?: throw IllegalArgumentException("Index missing")

    val result = mutableMapOf<String, List<SearchResult>>()
    jsonIndex.forEach { (key, value) ->
      if (value is JsonArray<*>) {
        val list = value.value.map {
          val obj = it as JsonObject
          SearchResult(
            url = obj.string("url")
              ?: throw IllegalArgumentException("URL missing"),
            numRefs = obj.int("numRefs")
              ?: throw IllegalArgumentException("numRefs missing")
          )
        }
        result[key] = list
      }
    }
    index = result
    return this
  }

  fun getPageCount(): Int {
    return pageCount
  }

  fun isEmpty(): Boolean {
    return index.isEmpty()
  }
}

class SearchResult(val url: URL, val numRefs: Int) {
  override fun toString(): String {
    return "$url - $numRefs references"
  }
}

class SearchResultSummary(val query: String,
                          val results: List<SearchResult>,
                          val pagesCount: Int = 0,
                          val timeTaken: Long = 0) {
  override fun toString(): String {
    if (pagesCount == 0) {
      return "Results for \"$query\":\n" + results.joinToString("\n") { " $it" }
    }

    return "Searched $pagesCount pages (${"%.2f".format(timeTaken.toDouble() / 1000)} seconds). About ${results.size} Results for \"$query\". Top 5 results:\n" + results
      .take(5)
      .joinToString("\n") { " $it" }

  }
}