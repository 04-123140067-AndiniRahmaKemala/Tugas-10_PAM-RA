package org.tugas3.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.tugas3.project.DeviceInfo
import org.tugas3.project.data.ProfileUiState
import org.tugas3.project.data.SettingsManager

class ProfileViewModel(
    private val settingsManager: SettingsManager,
    private val deviceInfo: DeviceInfo
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState(deviceInfo = deviceInfo))
    
    val uiState: StateFlow<ProfileUiState> = combine(
        _uiState,
        settingsManager.isDarkMode,
        settingsManager.sortOrder
    ) { state, isDark, sortOrder ->
        state.copy(
            isDarkMode = isDark, 
            sortOrder = sortOrder,
            deviceInfo = deviceInfo // Ensure deviceInfo is always preserved
        )
    }.stateIn(
        scope = viewModelScope, 
        started = SharingStarted.Eagerly, 
        initialValue = ProfileUiState(deviceInfo = deviceInfo)
    )

    fun updateName(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun updateBio(newBio: String) {
        _uiState.update { it.copy(bio = newBio) }
    }

    fun updateProfileImage(uri: String?) {
        _uiState.update { it.copy(profileImageUri = uri) }
    }

    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            settingsManager.setTheme(isDark)
        }
    }

    fun updateSortOrder(order: String) {
        viewModelScope.launch {
            settingsManager.setSortOrder(order)
        }
    }
}
