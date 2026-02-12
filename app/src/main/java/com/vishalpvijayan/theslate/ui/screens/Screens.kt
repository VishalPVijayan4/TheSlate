package com.vishalpvijayan.theslate.ui.screens

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SplashScreen(isLoggedIn: Boolean, onComplete: (Boolean) -> Unit) {
    LaunchedEffect(Unit) {
        delay(2500)
        onComplete(isLoggedIn)
    }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("TheSlate", style = MaterialTheme.typography.headlineLarge)
        Text("Capture everything, offline-first")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onProceed: () -> Unit) {
    val pages = listOf(
        Triple("Capture", "Write rich notes with checklists and tables", "📝"),
        Triple("Attach", "Add images, audio, and drawings", "🖼️"),
        Triple("Sync", "Offline-first sync with status indicators", "☁️")
    )
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        delay(3000)
        if (pagerState.currentPage < pages.lastIndex) {
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            val item = pages[page]
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(item.third, style = MaterialTheme.typography.displayMedium)
                Spacer(Modifier.height(16.dp))
                Text(item.first, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text(item.second)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            pages.indices.forEach { idx ->
                Spacer(
                    Modifier.size(10.dp).clip(CircleShape)
                        .background(if (pagerState.currentPage == idx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                scope.launch { pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0)) }
            }, enabled = pagerState.currentPage > 0) { Text("Previous") }
            if (pagerState.currentPage == pages.lastIndex) {
                Button(onClick = onProceed) { Text("Proceed to Login") }
            } else {
                Button(onClick = {
                    scope.launch { pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(pages.lastIndex)) }
                }) { Text("Next") }
            }
        }
    }
}

@Composable
fun LoginScreen(onGoogleSignIn: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Login", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))
        Text("Google Sign-In wiring is intentionally kept simple so you can plug in your own Google flow.")
        Spacer(Modifier.height(20.dp))
        Button(onClick = onGoogleSignIn, modifier = Modifier.fillMaxWidth()) { Text("Sign in with Google") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onGoogleSignIn, modifier = Modifier.fillMaxWidth()) { Text("Sign up with Google") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onOpenNote: (String) -> Unit,
    onCreate: () -> Unit,
    onSignedOut: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val now = remember { SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault()).format(Date()) }

    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = onCreate) { Icon(Icons.Default.Add, null) } }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            val hour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
            val greet = when (hour) { in 0..11 -> "Good Morning"; in 12..16 -> "Good Afternoon"; else -> "Good Evening" }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("$greet, ${state.session.userName.ifBlank { "User" }}", fontWeight = FontWeight.Bold)
                    Text(now)
                }
                AsyncImage(model = state.session.photoUrl.ifBlank { null }, contentDescription = null, modifier = Modifier.size(48.dp).clip(CircleShape))
            }
            Spacer(Modifier.height(12.dp))
            if (state.notes.isNotEmpty()) {
                OutlinedTextField(value = state.searchQuery, onValueChange = viewModel::onSearch, label = { Text("Search notes") }, modifier = Modifier.fillMaxWidth())
            }
            Spacer(Modifier.height(8.dp))
            if (state.notes.isEmpty()) {
                Text("No notes yet. Start creating.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.notes) { note ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onOpenNote(note.noteId) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(note.title.ifBlank { "Untitled" }, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    Spacer(Modifier.size(10.dp).clip(CircleShape).background(if (note.isSynced) androidx.compose.ui.graphics.Color.Green else androidx.compose.ui.graphics.Color.Red))
                                }
                                Text(note.description.take(80))
                                Text("Edited: ${SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(note.updatedAt))}")
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { viewModel.signOut(); onSignedOut() }) { Text("Sign out") }
        }
    }
}

@Composable
fun NoteEditorScreen(viewModel: NoteEditorViewModel, onBack: () -> Unit) {
    val note by viewModel.note.collectAsState()
    val mode by viewModel.mode.collectAsState()
    var imageInput by remember { mutableStateOf("") }
    var audioInput by remember { mutableStateOf("") }
    var checklistInput by remember { mutableStateOf("") }

    Scaffold { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${mode.name} Note", style = MaterialTheme.typography.titleLarge)
                    Row {
                        if (mode == NoteMode.VIEW) {
                            IconButton(onClick = { viewModel.setMode(NoteMode.EDIT) }) { Icon(Icons.Default.Edit, null) }
                        }
                        IconButton(onClick = { viewModel.delete(onBack) }) { Icon(Icons.Default.Delete, null) }
                    }
                }
            }
            item {
                OutlinedTextField(value = note.title, onValueChange = viewModel::updateTitle, enabled = mode != NoteMode.VIEW, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            }
            item {
                OutlinedTextField(value = note.description, onValueChange = viewModel::updateDescription, enabled = mode != NoteMode.VIEW, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            }
            item { Text("Images (${note.imageAttachments.size}/5)") }
            items(note.imageAttachments) { path ->
                AsyncImage(model = Uri.parse(path), contentDescription = null, modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
            }
            if (mode != NoteMode.VIEW) {
                item {
                    OutlinedTextField(value = imageInput, onValueChange = { imageInput = it }, label = { Text("Image URI/path") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { viewModel.addImage(imageInput); imageInput = "" }) { Text("Add Image") }
                }
            }

            item { Text("Audio (${note.audioAttachments.size}/2)") }
            items(note.audioAttachments) { audio -> Text("• $audio") }
            if (mode != NoteMode.VIEW) {
                item {
                    OutlinedTextField(value = audioInput, onValueChange = { audioInput = it }, label = { Text("Audio URI/path") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { viewModel.addAudio(audioInput); audioInput = "" }) { Text("Add Audio") }
                }
            }

            item {
                Text("Drawing")
                Text(note.drawingImagePath ?: "No drawing saved")
                if (mode != NoteMode.VIEW) {
                    Button(onClick = { viewModel.setDrawing("drawing_${note.noteId}.png") }) { Text("Save Sample Drawing") }
                }
            }

            item {
                Text("Checklist")
                note.checklistItems.forEachIndexed { index, item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = item.isChecked, onCheckedChange = { viewModel.toggleChecklist(index) })
                        Text(item.text)
                    }
                }
                if (mode != NoteMode.VIEW) {
                    OutlinedTextField(value = checklistInput, onValueChange = { checklistInput = it }, label = { Text("Checklist item") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { viewModel.addChecklist(checklistInput); checklistInput = "" }) { Text("Add Checklist Item") }
                }
            }

            item {
                Text("Table Data")
                note.tableData.groupBy { it.row }.toSortedMap().forEach { (_, rowCells) ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowCells.sortedBy { it.column }.forEach { cell ->
                            OutlinedTextField(
                                value = cell.value,
                                onValueChange = { viewModel.updateCell(cell.row, cell.column, it) },
                                enabled = mode != NoteMode.VIEW,
                                label = { Text("${cell.row},${cell.column}") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                if (mode != NoteMode.VIEW) {
                    Button(onClick = { viewModel.addTableRow(2) }) { Text("Add Row") }
                }
            }

            item {
                Text("Alarm: ${note.alarmTime?.let { Date(it) } ?: "Not set"}")
                if (mode != NoteMode.VIEW) Button(onClick = { viewModel.setAlarm(System.currentTimeMillis() + 60_000) }) { Text("Set +1 Min Alarm") }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onBack) { Text("Back") }
                    if (mode != NoteMode.VIEW) {
                        Button(onClick = { viewModel.save(onBack) }) { Icon(Icons.Default.Check, null); Text("Save") }
                    }
                }
            }
        }
    }
}
