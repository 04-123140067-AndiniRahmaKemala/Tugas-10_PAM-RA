package org.tugas3.project

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.tugas3.project.data.*
import org.tugas3.project.ui.*
import org.tugas3.project.viewmodel.NotesViewModel
import org.tugas3.project.viewmodel.ProfileViewModel
import org.tugas3.project.viewmodel.AiViewModel
import org.tugas3.project.viewmodel.NotesUiState

@Composable
fun App() {
    KoinContext {
        val settingsManager: SettingsManager = org.koin.compose.koinInject()
        val isDarkMode by settingsManager.isDarkMode.collectAsState(initial = isSystemInDarkTheme())

        val cuteLightTheme = lightColorScheme(
            primary = Color(0xFFE91E63),
            secondary = Color(0xFF9C27B0),
            tertiary = Color(0xFFFFB74D),
            background = Color(0xFFFFF0F5),
            surface = Color.White,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFD1DC)
        )

        val cuteDarkTheme = darkColorScheme(
            primary = Color(0xFFFF80AB),
            secondary = Color(0xFFCE93D8),
            background = Color(0xFF1A1A1A)
        )

        MaterialTheme(
            colorScheme = if (isDarkMode) cuteDarkTheme else cuteLightTheme
        ) {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen(
    notesViewModel: NotesViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel(),
    aiViewModel: AiViewModel = koinViewModel()
) {
    val profileUiState by profileViewModel.uiState.collectAsState()
    val notesUiState by notesViewModel.uiState.collectAsState()
    val aiUiState by aiViewModel.uiState.collectAsState()
    val searchQuery by notesViewModel.searchQuery.collectAsState()
    val isConnected by notesViewModel.isConnected.collectAsState()
    
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var isEditingProfile by remember { mutableStateOf(false) }

    val bottomNavItems = listOf(
        BottomNavItem.Notes,
        BottomNavItem.Favorites,
        BottomNavItem.Ai,
        BottomNavItem.Profile
    )

    val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }

    Scaffold(
        topBar = {
            if (!isConnected) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "No Internet Connection",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    item.icon,
                                    contentDescription = item.label 
                                ) 
                            },
                            label = { Text(item.label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Notes.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomNavItem.Notes.route) {
                NotesScreen(
                    uiState = notesUiState,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { notesViewModel.onSearchQueryChange(it) },
                    onNoteClick = { id -> navController.navigate(Screen.NoteDetail.createRoute(id)) },
                    onAddNoteClick = { navController.navigate(Screen.AddNote.route) },
                    onToggleFavorite = { id -> notesViewModel.toggleFavorite(id) },
                    isConnected = isConnected
                )
            }
            
            composable(BottomNavItem.Favorites.route) {
                FavoritesScreen(
                    uiState = notesUiState,
                    onNoteClick = { id -> navController.navigate(Screen.NoteDetail.createRoute(id)) },
                    onToggleFavorite = { id -> notesViewModel.toggleFavorite(id) }
                )
            }

            composable(BottomNavItem.Ai.route) {
                AiAssistantScreen(
                    uiState = aiUiState,
                    onSendMessage = { aiViewModel.sendMessage(it) },
                    onBack = { navController.popBackStack() },
                    onDismissError = { aiViewModel.clearError() }
                )
            }
            
            composable(BottomNavItem.Profile.route) {
                if (isEditingProfile) {
                    EditProfileScreen(
                        uiState = profileUiState,
                        onSave = { name, bio, photoDeleted, sortOrder ->
                            profileViewModel.updateName(name)
                            profileViewModel.updateBio(bio)
                            profileViewModel.updateSortOrder(sortOrder)
                            if (photoDeleted) {
                                profileViewModel.updateProfileImage("deleted")
                            }
                            isEditingProfile = false
                        },
                        onCancel = { isEditingProfile = false },
                        onDarkModeToggle = { profileViewModel.toggleDarkMode(it) }
                    )
                } else {
                    ProfileScreen(
                        uiState = profileUiState,
                        onEditClick = { isEditingProfile = true }
                    )
                }
            }

            composable(
                route = Screen.NoteDetail.route,
                arguments = listOf(navArgument("noteId") { type = NavType.LongType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
                val note = if (notesUiState is NotesUiState.Success) {
                    (notesUiState as NotesUiState.Success).notes.find { it.id == noteId }
                } else null
                
                NoteDetailScreen(
                    note = note,
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(Screen.EditNote.createRoute(id)) },
                    onDelete = { id -> 
                        notesViewModel.deleteNote(id)
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.AddNote.route) {
                AddEditNoteScreen(
                    onSave = { title, content, category ->
                        notesViewModel.addNote(title, content, category)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditNote.route,
                arguments = listOf(navArgument("noteId") { type = NavType.LongType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
                val note = if (notesUiState is NotesUiState.Success) {
                    (notesUiState as NotesUiState.Success).notes.find { it.id == noteId }
                } else null

                AddEditNoteScreen(
                    note = note,
                    onSave = { title, content, category ->
                        notesViewModel.updateNote(noteId, title, content, category)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
