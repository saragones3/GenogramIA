package dev.saragones3.genogramia.fakes

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.repository.TreeRepository

class FakeTreeRepository : TreeRepository {
    private val trees = mutableListOf<GenogramTree>()
    var shouldReturnError = false
    var errorToReturn = Exception("Fake tree repository error")

    override suspend fun createTree(tree: GenogramTree): GenogramTree {
        if (shouldReturnError) {
            throw errorToReturn
        }
        trees.add(tree)
        return tree
    }

    override suspend fun getTree(id: String): GenogramTree? {
        if (shouldReturnError) {
            throw errorToReturn
        }
        return trees.find { it.id == id }
    }

    override suspend fun getTrees(): List<GenogramTree> {
        if (shouldReturnError) {
            throw errorToReturn
        }
        return trees.toList()
    }

    override suspend fun updateTree(tree: GenogramTree): GenogramTree {
        if (shouldReturnError) {
            throw errorToReturn
        }
        val index = trees.indexOfFirst { it.id == tree.id }
        if (index != -1) {
            trees[index] = tree
        } else {
            trees.add(tree)
        }
        return tree
    }

    override suspend fun deleteTree(id: String) {
        if (shouldReturnError) {
            throw errorToReturn
        }
        trees.removeAll { it.id == id }
    }
}
