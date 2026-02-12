package com.vishalpvijayan.theslate.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.vishalpvijayan.theslate.core.AudioPlayerManager
import com.vishalpvijayan.theslate.core.AudioRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*@Composable
fun SplashScreen(isLoggedIn: Boolean, onComplete: (Boolean) -> Unit) {
    LaunchedEffect(Unit) {
        delay(2500)
        onComplete(isLoggedIn)
    }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("TheSlate", style = MaterialTheme.typography.headlineLarge)
        Text("Capture everything, offline-first")
        Text("Version 1.0.0", style = MaterialTheme.typography.bodySmall)
    }
}*/

@Composable
fun SplashScreen(isLoggedIn: Boolean, onComplete: (Boolean) -> Unit) {

    LaunchedEffect(Unit) {
        delay(2500)
        onComplete(isLoggedIn)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "TheSlate",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(
                color = MaterialTheme.colorScheme.primary,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth(0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Capture everything, offline-first",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onProceed: () -> Unit) {

    val pages = listOf(
        Triple(
            "Capture Every Thought",
            "TheSlate helps you capture ideas instantly. Write notes, add checklists, insert tables, attach images, record audio, and even sketch your thoughts. Everything you need to organize your mind in one powerful space.",
            "✒"
        ),
        Triple(
            "More Than Just Notes",
            "Attach up to 5 images, record audio, draw on a built-in canvas, create checkbox lists, and design dynamic tables. TheSlate adapts to your thinking style — structured, creative, or somewhere in between.",
            "📜"
        ),
        Triple(
            "Safe, Synced, Always Ready",
            "Your notes are saved offline first and automatically synced to your Google Drive when internet is available. Set reminders with alarms that work even after reboot. Your ideas stay secure, accessible, and always in sync.",
            "🕯"
        )
    )

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        delay(3000)
        if (pagerState.currentPage < pages.lastIndex) {
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->

                val item = pages[page]

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = item.third,
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = item.first,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(Modifier.height(12.dp))

                    Divider(
                        modifier = Modifier.fillMaxWidth(0.4f),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = item.second,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pages.indices.forEach { idx ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == idx)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                (pagerState.currentPage - 1).coerceAtLeast(0)
                            )
                        }
                    },
                    enabled = pagerState.currentPage > 0,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Previous")
                }

                if (pagerState.currentPage == pages.lastIndex) {
                    Button(
                        onClick = onProceed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Proceed to Login")
                    }
                } else {
                    Button(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    (pagerState.currentPage + 1)
                                        .coerceAtMost(pages.lastIndex)
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}


@Composable
fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: () -> Unit) {

    val context = LocalContext.current
    val webClientId = ""
    val hasPlayServices = remember {
        GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    }

    val signInClient = remember { GoogleSignIn.getClient(context, gso) }

    val signInLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (accountTask.isSuccessful) {
                val account = accountTask.result
                if (account != null) {
                    viewModel.onGoogleSignedIn(
                        userName = account.displayName.orEmpty(),
                        email = account.email.orEmpty(),
                        photoUrl = account.photoUrl?.toString().orEmpty(),
                        id = account.id.orEmpty()
                    )
                    onLoginSuccess()
                }
            }
        }

    // Animation states
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val animatedAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        label = ""
    )

    val animatedOffset by animateDpAsState(
        targetValue = if (visible) 0.dp else 40.dp,
        label = ""
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .graphicsLayer {
                    alpha = animatedAlpha
                    translationY = animatedOffset.toPx()
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "TheSlate",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(12.dp))

            Divider(
                modifier = Modifier.fillMaxWidth(0.5f),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Sign in to continue your archive",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(32.dp))

            if (!hasPlayServices) {
                Text(
                    "Google Play Services unavailable on this device.",
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
            }

            if (webClientId.isBlank()) {
                Text(
                    "Configure Web Client ID for production use.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(16.dp))
            }

            // Animated scale effect for primary button
            val scale by animateFloatAsState(
                targetValue = if (visible) 1f else 0.8f,
                label = ""
            )

            Button(
                onClick = { signInLauncher.launch(signInClient.signInIntent) },
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                enabled = hasPlayServices,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Sign in with Google")
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    viewModel.loginAsDemo()
                    onLoginSuccess()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Continue Demo")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel, onOpenNote: (String) -> Unit, onCreate: () -> Unit, onSignedOut: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val now = remember { SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault()).format(Date()) }
    val filters = remember { listOf("All", "Starred", "Birthdays", "Todos") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredNotes = remember(state.visibleNotes, selectedFilter) {
        if (selectedFilter == "All") state.visibleNotes
        else state.visibleNotes.filter { note -> note.tags.any { it.equals(selectedFilter, true) } }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreate) { Icon(Icons.Default.Add, contentDescription = "Create note") }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            val hour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
            val greet = when (hour) { in 0..11 -> "Good Morning"; in 12..16 -> "Good Afternoon"; else -> "Good Evening" }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = state.session.photoUrl.ifBlank { null },
                    contentDescription = null,
                    modifier = Modifier.size(44.dp).clip(CircleShape)
                )
                Spacer(Modifier.size(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("$greet", fontWeight = FontWeight.SemiBold)
                    Text(state.session.userName.ifBlank { "User" }, style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = { }) {
                    Text("💡")
                }
            }

            Text(now, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEach { filter ->
                    androidx.compose.material3.FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.uppercase()) }
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            if (state.notes.isNotEmpty()) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearch,
                    label = { Text("Search") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(12.dp))
            if (state.notes.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("What's your thoughts for today?", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(12.dp))
                        Text("Use + to create your first note. You can keep labels empty or add them later.", textAlign = TextAlign.Center)
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = onCreate) { Text("Create note") }
                    }
                }
                Spacer(Modifier.weight(1f))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                    items(filteredNotes) { note ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onOpenNote(note.noteId) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(note.title.ifBlank { "Untitled" }, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    Spacer(
                                        Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(if (note.isSynced) androidx.compose.ui.graphics.Color(0xFF2E7D32) else androidx.compose.ui.graphics.Color(0xFFC62828))
                                    )
                                }
                                Text(note.description.take(120))
                                if (note.tags.isNotEmpty()) {
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        note.tags.forEach { tag ->
                                            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                                                Text("$tag", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium)
                                            }
                                        }
                                    }
                                }
                                Text(
                                    "Last edited: ${SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(note.updatedAt))}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    if (state.hasMore) {
                        item {
                            OutlinedButton(onClick = viewModel::loadNextPage, modifier = Modifier.fillMaxWidth()) { Text("Load more") }
                        }
                    }
                }
            }

            OutlinedButton(onClick = { viewModel.signOut(); onSignedOut() }, modifier = Modifier.fillMaxWidth()) {
                Text("Sign out")
            }
        }
    }
}

private enum class EditorTool(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Reminder("Alarm", Icons.Default.Notifications),
    Checklist("Checklist", Icons.Default.Checklist),
    Attachments("Attachment", Icons.Default.AttachFile),
    Audio("Audio", Icons.Default.Mic),
    Canvas("Canvas", Icons.Default.Brush)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteEditorScreen(viewModel: NoteEditorViewModel, onBack: () -> Unit) {
    val note by viewModel.note.collectAsState()
    val mode by viewModel.mode.collectAsState()
    val context = LocalContext.current
    val player = remember { AudioPlayerManager(context) }

    var checklistInput by remember { mutableStateOf("") }
    var tagInput by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var recordingPath by remember { mutableStateOf<String?>(null) }
    var activeTool by remember { mutableStateOf(EditorTool.Reminder) }
    val recorder = remember { AudioRecorder(context) }

    val strokes = remember { mutableStateListOf<List<Offset>>() }
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { viewModel.addImage(it.toString()) }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let { saveBitmap(context, it)?.let(viewModel::addImage) }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }
    val audioPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted && !isRecording) {
            recordingPath = recorder.start()
            isRecording = true
        }
    }

    DisposableEffect(Unit) { onDispose { player.release() } }

    Scaffold(
        bottomBar = {
            NavigationBar {
                EditorTool.entries.forEach { tool ->
                    NavigationBarItem(
                        selected = activeTool == tool,
                        onClick = { activeTool = tool },
                        icon = { Icon(tool.icon, contentDescription = tool.label) },
                        label = { Text(tool.label) }
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = onBack) { Text("✕") }
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Title", style = MaterialTheme.typography.titleMedium)
                    }
                    Row {
                        IconButton(onClick = { }) { Icon(Icons.Default.Visibility, contentDescription = "View") }
                        IconButton(onClick = { viewModel.setMode(if (mode == NoteMode.VIEW) NoteMode.EDIT else NoteMode.VIEW) }) { Icon(Icons.Default.Edit, contentDescription = "Toggle edit") }
                    }
                }
            }

            item { OutlinedTextField(value = note.title, onValueChange = viewModel::updateTitle, enabled = mode != NoteMode.VIEW, label = { Text("Title") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(value = note.description, onValueChange = viewModel::updateDescription, enabled = mode != NoteMode.VIEW, label = { Text("Subtitle") }, modifier = Modifier.fillMaxWidth()) }
            item { Text(SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(note.updatedAt)), style = MaterialTheme.typography.bodySmall) }

            item {
                Text("Labels / Tags")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    note.tags.forEach { tag ->
                        androidx.compose.material3.InputChip(
                            selected = false,
                            onClick = { if (mode != NoteMode.VIEW) viewModel.removeTag(tag) },
                            label = { Text(tag) },
                            enabled = mode != NoteMode.VIEW
                        )
                    }
                }
                if (mode != NoteMode.VIEW) {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(value = tagInput, onValueChange = { tagInput = it }, label = { Text("Add label") }, modifier = Modifier.weight(1f))
                        Button(onClick = { viewModel.addTag(tagInput); tagInput = "" }) { Text("Add") }
                    }
                }
            }

            item {
                when (activeTool) {
                    EditorTool.Reminder -> {
                        Text("Alarm: ${note.alarmTime?.let { Date(it) } ?: "Not set"}")
                        if (mode != NoteMode.VIEW) Button(onClick = { viewModel.setAlarm(System.currentTimeMillis() + 60_000) }) { Text("Set +1 Min") }
                    }
                    EditorTool.Checklist -> {
                        Text("Checklist")
                        note.checklistItems.forEachIndexed { index, item ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = item.isChecked, onCheckedChange = { if (mode != NoteMode.VIEW) viewModel.toggleChecklist(index) })
                                Text(item.text)
                            }
                        }
                        if (mode != NoteMode.VIEW) {
                            OutlinedTextField(value = checklistInput, onValueChange = { checklistInput = it }, label = { Text("Checklist item") }, modifier = Modifier.fillMaxWidth())
                            Button(onClick = { viewModel.addChecklist(checklistInput); checklistInput = "" }) { Text("Add checklist") }
                        }
                    }
                    EditorTool.Attachments -> {
                        Text("Images (${note.imageAttachments.size}/5)")
                        note.imageAttachments.forEach { path ->
                            AsyncImage(model = Uri.parse(path), contentDescription = null, modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                        }
                        if (mode != NoteMode.VIEW) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) { Text("Gallery") }
                                Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) { Text("Take Photo") }
                            }
                        }
                    }
                    EditorTool.Audio -> {
                        Text("Audio (${note.audioAttachments.size}/2)")
                        note.audioAttachments.forEach { audio ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = audio.substringAfterLast('/'), modifier = Modifier.weight(1f))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { player.play(audio) }) { Text("Play") }
                                    Button(onClick = { player.stop() }) { Text("Stop") }
                                }
                            }
                        }
                        if (mode != NoteMode.VIEW) {
                            Button(onClick = {
                                if (!isRecording) {
                                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                } else {
                                    recorder.stop()
                                    recordingPath?.let(viewModel::addAudio)
                                    recordingPath = null
                                    isRecording = false
                                }
                            }) { Text(if (isRecording) "Stop & Save" else "Record") }
                        }
                    }
                    EditorTool.Canvas -> {
                        Text("Canvas")
                        Text(note.drawingImagePath ?: "No drawing saved")
                        if (mode != NoteMode.VIEW) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(260.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(androidx.compose.ui.graphics.Color.White)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { start ->
                                            currentStroke = listOf(start)
                                            strokes.add(currentStroke)
                                        },
                                        onDrag = { change, _ ->
                                            if (strokes.isNotEmpty()) {
                                                currentStroke = currentStroke + change.position
                                                strokes[strokes.lastIndex] = currentStroke
                                            }
                                        }
                                    )
                                }) {
                                    strokes.forEach { stroke ->
                                        if (stroke.size > 1) {
                                            val path = Path().apply {
                                                moveTo(stroke.first().x, stroke.first().y)
                                                stroke.drop(1).forEach { lineTo(it.x, it.y) }
                                            }
                                            drawPath(path, color = androidx.compose.ui.graphics.Color.Black, style = Stroke(width = 4f))
                                        }
                                    }
                                }
                            }) {
                                Text(if (isRecording) "Stop & Save Recording" else "Record Audio")
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { strokes.clear() }) { Text("Clear") }
                                Button(onClick = { saveDrawing(context, strokes)?.let(viewModel::setDrawing) }) { Text("Save") }
                            }
                        }
                    }
                }
            }

            item {
                Text("Table Data")
                note.tableData.groupBy { it.row }.toSortedMap().forEach { (_, rowCells) ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowCells.sortedBy { it.column }.forEach { cell ->
                            OutlinedTextField(value = cell.value, onValueChange = { viewModel.updateCell(cell.row, cell.column, it) }, enabled = mode != NoteMode.VIEW, label = { Text("${cell.row},${cell.column}") }, modifier = Modifier.weight(1f))
                        }
                    }
                }
                if (mode != NoteMode.VIEW) Button(onClick = { viewModel.addTableRow(2) }) { Text("Add Row") }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { viewModel.delete(onBack) }) { Icon(Icons.Default.Delete, contentDescription = null); Text(" Delete") }
                    Button(onClick = { viewModel.save(onBack) }, enabled = mode != NoteMode.VIEW) { Icon(Icons.Default.Check, contentDescription = null); Text(" Save") }
                }
            }
        }
    }
}

private fun saveBitmap(context: Context, bitmap: Bitmap): String? = runCatching {
    val file = File(context.cacheDir, "img_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
    file.absolutePath
}.getOrNull()

private fun saveDrawing(context: Context, strokes: List<List<Offset>>): String? = runCatching {
    val width = 1200
    val height = 800
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawColor(Color.WHITE)
    val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 6f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    strokes.forEach { stroke ->
        if (stroke.size > 1) {
            for (i in 0 until stroke.size - 1) {
                val from = stroke[i]
                val to = stroke[i + 1]
                canvas.drawLine(from.x, from.y, to.x, to.y, paint)
            }
        }
    }
    saveBitmap(context, bitmap)
}.getOrNull()
