package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.repository.TreeRepository
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.domain.util.DateProvider

class UpdateTreeUseCase(
    private val repository: TreeRepository,
    private val dateProvider: DateProvider,
    private val dateFormatter: DateFormatter,
) {
    suspend operator fun invoke(tree: GenogramTree) {
        repository.updateTree(tree.copy(
            lastUpdated =
                dateFormatter.formatDate(
                    millis = dateProvider.nowEpochMilliseconds(),
                    pattern = GenogramTree.DATE_FORMAT,
                ),
        ))
    }
}
