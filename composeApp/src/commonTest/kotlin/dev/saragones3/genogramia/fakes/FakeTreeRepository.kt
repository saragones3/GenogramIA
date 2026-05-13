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
}
