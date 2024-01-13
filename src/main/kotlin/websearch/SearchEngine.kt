package websearch

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.JsonArray
import java.io.File

class SearchEngine(private val data: Map<URL, WebPage> = emptyMap()) {
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
      it
    }.eachCount().map { SearchResult(it.key, it.value) }
  }

  fun searchFor(query: String): SearchResultSummary {
    return SearchResultSummary(query, index.getOrDefault(query, listOf()))
  }

  fun saveIndex(filename : String) {
    val jsonObject = JsonObject(index)
    File("$filename.json").writeText(jsonObject.toJsonString())
  }

  fun loadIndex(filename: String): SearchEngine {
    val parser: Parser = Parser.default()
    val json: JsonObject = parser.parse(StringBuilder(File("$filename.json").readText())) as JsonObject
    val result = mutableMapOf<String, List<SearchResult>>()

    json.forEach { (key, value) ->
      if (value is JsonArray<*>) {
        val list = value.value.map {
          val obj = it as JsonObject
          SearchResult(
            url = obj.string("url") ?: throw IllegalArgumentException("URL missing"),
            numRefs = obj.int("numRefs") ?: throw IllegalArgumentException("numRefs missing")
          )
        }
        result[key] = list
      }
    }
    index = result
    return this
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
