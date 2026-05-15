package dev.saragones3.genogramia.data.repository

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.repository.TreeRepository

class InMemoryTreeRepository : TreeRepository {
    private val trees = mutableListOf<GenogramTree>()

    override suspend fun createTree(tree: GenogramTree): GenogramTree {
        trees.add(tree)
        return tree
    }

    override suspend fun getTree(id: String): GenogramTree? = trees.find { it.id == id }

    override suspend fun getTrees(): List<GenogramTree> = trees
}
