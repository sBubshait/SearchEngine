package websearch

var searchEngine = SearchEngine()
fun main(args: Array<String>) {
  println("Welcome to the Web Search Engine! 🕸️")

  while (true) {
    if (searchEngine.index.isEmpty()) {
      println("No Index loaded.")
    } else {
      println("Index loaded.")
    }

    println(
      "Choose an option:" +
        "\n1. Start Crawling" +
        "\n2. Search for a word" +
        "\n3. Load Index from file" +
        "\n4. Save Index to file" +
        "\n5. Exit"
    )

    val input = readLine()
    when (input) {
      "1" -> crawl()
      "2" -> while (true) {
        if (searchEngine.index.isEmpty()) {
          println("No Index loaded. Please crawl a website first.")
          break
        }
        val query = prompt(
          message = "Enter the word you want to search for (Or write '\$exit' to go back to the main menu)",
          validator = { it.isNotBlank() },
          errorMessage = "Invalid word provided. Please enter a valid word."
        )
        if (query == "\$exit") {
          break
        }
        val result = searchEngine.searchFor(query)
        println(result)
      }
      "3" -> {
        val filename = prompt(
          message = "Enter the filename you want to load the index from",
          validator = { it.isNotBlank() },
          errorMessage = "Invalid filename provided. Please enter a valid filename (without .json)."
        )
        searchEngine = searchEngine.loadIndex(filename)
      }
      "4" -> {
        val filename = prompt(
          message = "Enter the filename you want to save the index to",
          validator = { it.isNotBlank() },
          errorMessage = "Invalid filename provided. Please enter a valid filename (without .json)."
        )
        searchEngine.saveIndex(filename)
      }
      "5" -> {
        println("Exiting...")
        break
      }
      else -> println("Invalid option, please enter a number between 1 and 3.")
    }
  }
}
private fun crawl() {
  val startingUrl = prompt(
    message = "Enter the starting URL",
    validator = { Regex(URL_REGEX).matches(it)  },
    errorMessage = "Invalid URL provided. Please enter a valid URL."
  )

  val maxPages = prompt(
    message = "Enter the maximum number of pages to crawl",
    validator = { it.toIntOrNull()?.let { num -> num > 0 } == true },
    errorMessage = "Invalid number provided. Please enter a positive integer."
  ).toInt()

  println("Crawling from $startingUrl with a maximum of $maxPages pages...")
  val crawler = WebCrawler(startingUrl, maxPages)
  crawler.run()
  searchEngine = SearchEngine(crawler.dump())
  searchEngine.compileIndex()
}

private fun prompt(message: String, validator: (String) -> Boolean, errorMessage: String = "Invalid Input. Try again."): String {
  while (true) {
    println(message)
    val input = readLine()
    if (input != null && validator(input)) {
      return input
    } else {
      println(errorMessage)
    }
  }
}

//fun main(args: Array<String>) {


//  var currentlyCrawling = false
//
//  println("Welcome to the Web Search Engine! 🕸️")
//  println("Current Index: ")
//  println("Choose an option:" +
//    "\n1. Crawl a website" +
//    "\n2. Search for a word" +
//    "\n3. Exit")
//  args.forEachIndexed { index, arg ->
//    println("Arg $index: $arg")
//  }
//
//  fun crawl() {
//    println("Enter the starting URL you want to crawl:")
//    val url = readLine()!!
//    println("Enter the maximum number of pages you want to crawl:")
//    val maxPages = readLine()!!.toInt()
//    val crawler = WebCrawler(url, maxPages)
//    crawler.run()
//    val searchEngine = SearchEngine(crawler.dump())
//    searchEngine.compileIndex()
//    searchEngine.saveIndex("index")
//    println("Crawling completed successfully!")
//  }
//}