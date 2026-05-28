package org.tugas3.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.tugas3.project.data.ProfileUiState
import org.tugas3.project.db.NoteEntity
import org.tugas3.project.viewmodel.NotesUiState
import tugasindividu3.composeapp.generated.resources.Res
import tugasindividu3.composeapp.generated.resources.my_photo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    uiState: NotesUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNoteClick: (Long) -> Unit,
    onAddNoteClick: () -> Unit,
    onToggleFavorite: (Long) -> Unit,
    isConnected: Boolean
) {
    val categories = listOf("All", "Work", "Education", "Personal", "General")
    var selectedCategory by remember { mutableStateOf("All") }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                LargeTopAppBar(
                    title = { 
                        Column {
                            Text("My Notes", fontWeight = FontWeight.ExtraBold)
                            if (uiState is NotesUiState.Success) {
                                Text("You have ${uiState.notes.size} notes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                )
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search your notes...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNoteClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("New Note")
            }
        }
    ) { padding ->
        when (uiState) {
            is NotesUiState.Loading -> {
                Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is NotesUiState.Empty -> {
                Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Text("No notes found", color = Color.Gray)
                    }
                }
            }
            is NotesUiState.Success -> {
                val finalNotes = if (selectedCategory == "All") uiState.notes 
                                else uiState.notes.filter { it.category == selectedCategory }
                
                if (finalNotes.isEmpty()) {
                    Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No notes in this category", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(padding).fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(finalNotes) { note ->
                            NoteCard(note, onNoteClick, onToggleFavorite)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteCard(note: NoteEntity, onNoteClick: (Long) -> Unit, onToggleFavorite: (Long) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onNoteClick(note.id) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        note.category, 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(note.date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            
            Spacer(Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(
                        note.title, 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        note.content, 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                IconButton(onClick = { onToggleFavorite(note.id) }) {
                    Icon(
                        if (note.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (note.isFavorite) MaterialTheme.colorScheme.primary else Color.LightGray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    note: NoteEntity?,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Read Note", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (note != null) {
                        IconButton(onClick = { onEdit(note.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { onDelete(note.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (note != null) {
            Column(
                modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(Modifier.width(8.dp))
                    Text(note.date, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Spacer(Modifier.width(16.dp))
                    Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(note.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text(note.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.width(40.dp), thickness = 4.dp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(24.dp))
                Text(note.content, style = MaterialTheme.typography.bodyLarge, lineHeight = 28.sp)
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Note not found")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    note: NoteEntity? = null,
    onSave: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var category by remember { mutableStateOf(note?.category ?: "General") }
    
    val categories = listOf("General", "Work", "Education", "Personal")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (note == null) "New Story" else "Edit Story", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(24.dp)) {
            Text("Category", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Enter title here...", style = MaterialTheme.typography.headlineSmall) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Write your thoughts...") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onSave(title, content, category) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text("Save My Note", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    uiState: NotesUiState,
    onNoteClick: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit
) {
    val favoriteNotes = if (uiState is NotesUiState.Success) {
        uiState.notes.filter { it.isFavorite }
    } else emptyList()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { 
                    Column {
                        Text("Favorites", fontWeight = FontWeight.ExtraBold)
                        Text("${favoriteNotes.size} saved items", style = MaterialTheme.typography.bodySmall)
                    }
                }
            )
        }
    ) { padding ->
        if (uiState is NotesUiState.Loading) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (favoriteNotes.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Text("No favorites yet", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteNotes) { note ->
                    NoteCard(note, onNoteClick, onToggleFavorite)
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onEditClick: () -> Unit
) {
    val profilePainter = if (uiState.profileImageUri == "deleted") {
        null
    } else {
        painterResource(Res.drawable.my_photo)
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        Box {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp).background(
                    Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer))
                )
            )
            
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileHeader(
                    name = uiState.name,
                    bio = uiState.bio,
                    imagePainter = profilePainter,
                    skills = uiState.skills.map { it.first }
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        
        Text(
            "Contact Information", 
            modifier = Modifier.padding(horizontal = 24.dp),
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )

        org.tugas3.project.ProfileCard {
            InfoItem(icon = Icons.Default.Email, label = "Email Address", value = uiState.email)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray.copy(alpha = 0.1f))
            InfoItem(icon = Icons.Default.Phone, label = "Phone Number", value = uiState.phone)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray.copy(alpha = 0.1f))
            InfoItem(icon = Icons.Default.LocationOn, label = "Current Location", value = uiState.location)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Device Information", 
            modifier = Modifier.padding(horizontal = 24.dp),
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )

        org.tugas3.project.ProfileCard {
            uiState.deviceInfo?.let { info ->
                InfoItem(icon = Icons.Default.Devices, label = "Platform", value = info.platform)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray.copy(alpha = 0.1f))
                InfoItem(icon = Icons.Default.Smartphone, label = "Model", value = info.model)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray.copy(alpha = 0.1f))
                InfoItem(icon = Icons.Default.Info, label = "OS Version", value = info.osVersion)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray.copy(alpha = 0.1f))
                InfoItem(icon = Icons.Default.Factory, label = "Manufacturer", value = info.manufacturer)
            }
        }

        Button(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth().padding(24.dp).height(56.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Personal Settings", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    uiState: ProfileUiState,
    onSave: (String, String, Boolean, String) -> Unit,
    onCancel: () -> Unit,
    onDarkModeToggle: (Boolean) -> Unit
) {
    var name by remember { mutableStateOf(uiState.name) }
    var bio by remember { mutableStateOf(uiState.bio) }
    var photoDeleted by remember { mutableStateOf(uiState.profileImageUri == "deleted") }
    var sortOrder by remember { mutableStateOf(uiState.sortOrder) }

    val sortOptions = listOf(
        "date_desc" to "Date (Newest)",
        "date_asc" to "Date (Oldest)",
        "title_asc" to "Title (A-Z)",
        "title_desc" to "Title (Z-A)"
    )

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CenterAlignedTopAppBar(
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = Color.White)
            }
            
            Row(
                modifier = Modifier.offset(y = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { photoDeleted = false },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                }
                SmallFloatingActionButton(
                    onClick = { photoDeleted = true },
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DarkModeToggleCard(
                isDarkMode = uiState.isDarkMode,
                onDarkModeToggle = onDarkModeToggle
            )

            Text("Sort Order", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sortOptions.forEach { option ->
                    FilterChip(
                        selected = sortOrder == option.first,
                        onClick = { sortOrder = option.first },
                        label = { Text(option.second) }
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display Name") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null) }
            )

            Button(
                onClick = { onSave(name, bio, photoDeleted, sortOrder) },
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DarkModeToggleCard(
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode, 
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Dark Appearance", fontWeight = FontWeight.Medium)
            }
            Switch(checked = isDarkMode, onCheckedChange = onDarkModeToggle)
        }
    }
}
