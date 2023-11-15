package net.gabor7d2.simpleinventory.db.repository

object RepositoryManager {
    val instance: Repository by lazy {
        MemoryRepository().apply { init() }
    }
}