package org.tugas3.project.data

import org.tugas3.project.DeviceInfo

data class ProfileUiState(
    val name: String = "Andinirhm",
    val bio: String = "Passionate tech enthusiast and creative soul.\nDreaming in code and living in colors. ✨",
    val email: String = "andinirahmak@gmail.com",
    val phone: String = "0895620419449",
    val location: String = "Lampung, Indonesia",
    val profileImageUri: String? = "deleted",
    val isDarkMode: Boolean = false,
    val sortOrder: String = "date_desc",
    val deviceInfo: DeviceInfo? = null,
    val skills: List<Pair<String, Double>> = listOf(
        "Kotlin" to 1.0,
        "Jetpack Compose" to 1.0,
        "UI Design" to 1.0,
        "PHP" to 1.0
    )
)
