@file:Suppress("MagicNumber")

import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.eachHref
import it.skrape.selects.html5.a
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

public fun main() {
    KotlinWeekly().search("ktor").forEach {
        println(it)
    }
}

public class KotlinWeekly {

    public fun search(partialHref: String): List<String> {
        var allLinks = listOf<String>()
        runBlocking {
            @Suppress("MagicNumber")
            val deferred = (223..242).map { issueNumber ->
                async {
                    scrape(issueNumber)
                }
            }
            allLinks = deferred.awaitAll().flatten()
        }
        return allLinks.filter { it.contains(partialHref) }
    }

    private fun scrape(issueNumber: Int) =
        skrape(HttpFetcher) {
            request {
                url = "https://mailchi.mp/kotlinweekly/kotlin-weekly-$issueNumber"
            }.also { println(System.currentTimeMillis()) }
            response {
                htmlDocument {
                    a {
                        findAll {
                            eachHref
                        }
                    }
                }
            }
        }
}
