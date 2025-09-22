package org.example.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * PUBLIC_INTERFACE
 * Repository for managing CRUD operations on notes.
 */
class NotesRepository private constructor(context: Context) {
    private val dao = AppDatabase.getInstance(context).noteDao()

    // PUBLIC_INTERFACE
    suspend fun getNotes(): List<Note> = withContext(Dispatchers.IO) { dao.getAll() }

    // PUBLIC_INTERFACE
    suspend fun getNote(id: Long): Note? = withContext(Dispatchers.IO) { dao.getById(id) }

    // PUBLIC_INTERFACE
    suspend fun addNote(title: String, content: String): Long = withContext(Dispatchers.IO) {
        val now = Date().time
        dao.insert(Note(title = title, content = content, updatedAt = now))
    }

    // PUBLIC_INTERFACE
    suspend fun updateNote(id: Long, title: String, content: String) = withContext(Dispatchers.IO) {
        val existing = dao.getById(id) ?: return@withContext
        dao.update(existing.copy(title = title, content = content, updatedAt = Date().time))
    }

    // PUBLIC_INTERFACE
    suspend fun deleteNote(id: Long) = withContext(Dispatchers.IO) {
        val existing = dao.getById(id) ?: return@withContext
        dao.delete(existing)
    }

    companion object {
        @Volatile private var INSTANCE: NotesRepository? = null

        // PUBLIC_INTERFACE
        fun getInstance(context: Context): NotesRepository {
            val current = INSTANCE
            if (current != null) return current
            synchronized(this) {
                val created = NotesRepository(context.applicationContext)
                INSTANCE = created
                return created
            }
        }
    }
}
