package dev.saragones3.genogramia.fakes

import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.repository.AuthRepository

class FakeAuthRepository(private var currentUser: User? = null) : AuthRepository {
    override fun getCurrentUser(): User? = currentUser

    fun setCurrentUser(user: User?) {
        currentUser = user
    }
}
