# SearchEngine

## Description

A simple web search engine in Kotlin, including a web crawler, indexer, and query engine.

## Demo
![searchengine-demo](https://github.com/sBubshait/SearchEngine/assets/44058159/845e8e6b-ef91-4a73-85c6-3a0a1a8957aa)



## Features

- [x] Simple Web Crawler: Crawls the web from a a given seed URL with a maximum number of pages to crawl, and generates a list of URLs and their corresponding page contents.
- [x] Indexer: Indexes the crawled pages and generates an inverted index. The index is stored in a memory.
- [x] Save and Load Index: Can save and load the index from a file.
- [x] Query Engine: Given a query, returns a list of URLs that match the query. The query can be a single word or a phrase.
- [x] Multi word queries: Supports multi word queries. Removes all stop words and punctuation from the query.
- [x] CLI Interface: A command line interface to interact with the search engine.

## To Do

- [ ] Web Interface: A web interface to search the index.
- [ ] Improved Ranking: Use more sophisticated ranking algorithms to improve the search results.
- [ ] Support crawling more file types (e.g. PDF, DOC, etc.): Currently only crawls HTML files.
- [ ] Multi threading support: Currently only uses a single thread to crawl the web.
- [ ] Support for more languages: Currently only supports English.
- [ ] Currently only supports a single index. Support multiple indexes. Allow for distributed indexing.

## Usage

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew run --console=plain 
```
