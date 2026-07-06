package dev.saragones3.genogramia.data.remote

import dev.saragones3.genogramia.data.remote.model.Cie11ChapterDto
import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.util.getAppLanguage

fun Cie11ChapterDto.toDomainList(): List<Disease> {
    val lang = getAppLanguage()
    return subcategories.map {
        Disease(
            code = it.code,
            title = if (lang == "es") it.title.es.ifEmpty { it.title.en } else it.title.en.ifEmpty { it.title.es },
            chapterCode = code,
            chapterTitle = if (lang == "es") title.es.ifEmpty { title.en } else title.en.ifEmpty { title.es },
            isGenetic = it.isGenetic,
        )
    }
}
