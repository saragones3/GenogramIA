package dev.saragones3.genogramia.domain.model

data class GenogramTree(
    val id: String,
    val name: String,
    val ancestorCount: Int,
    val lastUpdated: String,
    val centralPerson: Person,
    val persons: List<Person> = emptyList(),
    val relationships: List<Relationship> = emptyList(),
) {
    fun calculateAncestorCount(): Int {
        val ancestors = mutableSetOf<String>()
        val queue = mutableListOf(centralPerson.id)
        val visited = mutableSetOf<String>()

        while (queue.isNotEmpty()) {
            val currentId = queue.removeAt(0)
            if (currentId in visited) continue
            visited.add(currentId)

            relationships
                .filter { it.type.isDescendant && it.personId2 == currentId }
                .forEach { rel ->
                    ancestors.add(rel.personId1)
                    queue.add(rel.personId1)
                }
        }
        return ancestors.size
    }

    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    }
}
