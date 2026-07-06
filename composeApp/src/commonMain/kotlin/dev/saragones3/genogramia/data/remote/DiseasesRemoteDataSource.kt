package dev.saragones3.genogramia.data.remote

import dev.saragones3.genogramia.data.remote.model.Cie11ChapterDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class DiseasesRemoteDataSource(
    private val httpClient: HttpClient,
) {
    companion object {
        private const val CHAPTER_BASE = "/cie11_capitulo_"
        private val CHAPTERS =
            listOf(
                "02",
                "03",
                "04",
                "05",
                "06",
                "07",
                "08",
                "09",
                "10",
                "11",
                "12",
                "13",
                "14",
                "15",
                "16",
                "17",
                "20",
            )
    }

    fun getChapters(): List<String> = CHAPTERS

    suspend fun getChapter(chapter: String): Cie11ChapterDto = httpClient.get("$CHAPTER_BASE$chapter.json").body()
}
