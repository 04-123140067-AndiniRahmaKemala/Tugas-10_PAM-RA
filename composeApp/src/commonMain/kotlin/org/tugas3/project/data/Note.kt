package org.tugas3.project.data

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val date: String,
    val isFavorite: Boolean = false,
    val category: String = "General"
)
