package dev.saragones3.genogramia.domain.repository

import dev.saragones3.genogramia.domain.model.GenogramTree

interface TreeRepository {
    suspend fun createTree(tree: GenogramTree): GenogramTree
}
