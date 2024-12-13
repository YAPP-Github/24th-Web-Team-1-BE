package storage

interface RemoveObjectProvider {
    fun execute(name: String): Boolean
}