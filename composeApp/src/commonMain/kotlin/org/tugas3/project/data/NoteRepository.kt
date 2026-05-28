package org.tugas3.project.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import org.tugas3.project.db.NoteDatabase
import org.tugas3.project.db.NoteEntity

class NoteRepository(database: NoteDatabase) {
    private val queries = database.noteDatabaseQueries

    fun getAllNotes(): Flow<List<NoteEntity>> =
        queries.getAllNotes().asFlow().mapToList(Dispatchers.IO)

    fun searchNotes(query: String): Flow<List<NoteEntity>> =
        queries.searchNotes(query, query).asFlow().mapToList(Dispatchers.IO)

    fun getNoteById(id: Long): NoteEntity? =
        queries.getNoteById(id).executeAsOneOrNull()

    suspend fun insertNote(title: String, content: String, date: String, category: String) {
        queries.insertNote(
            title = title,
            content = content,
            date = date,
            isFavorite = false,
            category = category
        )
    }

    suspend fun updateNote(id: Long, title: String, content: String, date: String, category: String) {
        queries.updateNote(
            id = id,
            title = title,
            content = content,
            date = date,
            category = category
        )
    }

    suspend fun deleteNote(id: Long) {
        queries.deleteNote(id)
    }

    suspend fun updateFavorite(id: Long, isFavorite: Boolean) {
        queries.updateFavorite(isFavorite, id)
    }
}
