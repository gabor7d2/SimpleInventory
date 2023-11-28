package net.gabor7d2.simpleinventory.persistence.repository

object RepositoryManager {
    val instance: Repository by lazy {
        FirebaseRepository().apply { init() }
    }
}