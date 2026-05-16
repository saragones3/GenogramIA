package dev.saragones3.genogramia.data.repository

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.repository.TreeRepository

/**
 * Implementation of [TreeRepository] that will eventually interact with Cloud Firestore.
 * Currently saves data in a local variable for development purposes.
 */
class FirestoreTreeRepository : TreeRepository {
    // TODO: This list will be replaced by Firestore calls once integrated
    private val firestoreSimulation = mutableListOf<GenogramTree>()

    override suspend fun createTree(tree: GenogramTree): GenogramTree {
        // Simulation: In the future, this will use FirebaseProvider to save to Firestore
        firestoreSimulation.add(tree)
        return tree
    }

    override suspend fun getTree(id: String): GenogramTree? {
        // Simulation: In the future, this will use FirebaseProvider to fetch from Firestore
        return firestoreSimulation.find { it.id == id }
    }

    override suspend fun getTrees(): List<GenogramTree> = firestoreSimulation

    override suspend fun updateTree(tree: GenogramTree): GenogramTree {
        val index = firestoreSimulation.indexOfFirst { it.id == tree.id }
        if (index != -1) {
            firestoreSimulation[index] = tree
        } else {
            firestoreSimulation.add(tree)
        }
        return tree
    }
}
