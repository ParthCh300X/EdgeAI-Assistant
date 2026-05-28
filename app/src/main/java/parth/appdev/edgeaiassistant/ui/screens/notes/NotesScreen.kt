package parth.appdev.edgeaiassistant.ui.screens.notes

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel()
) {
    val state     = viewModel.state.collectAsState().value
    val bgColor   = Color(0xFF0B1220)
    val surface   = Color(0xFF1E293B)
    val primary   = Color(0xFF6366F1)
    val clipboard = LocalClipboardManager.current
    val context   = LocalContext.current
    val sdf       = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
    ) {
        Text(
            text  = "My Notes",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(Modifier.height(12.dp))

        // Search bar
        TextField(
            value         = state.searchQuery,
            onValueChange = viewModel::onSearchChanged,
            placeholder   = { Text("Search notes...", color = Color.Gray) },
            leadingIcon   = {
                Icon(
                    imageVector        = Icons.Default.Search,
                    contentDescription = null,
                    tint               = Color.Gray
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(14.dp),
            colors   = TextFieldDefaults.colors(
                focusedContainerColor   = surface,
                unfocusedContainerColor = surface,
                focusedTextColor        = Color.White,
                unfocusedTextColor      = Color.White,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(Modifier.height(12.dp))

        if (state.notes.isEmpty()) {
            Box(
                modifier            = Modifier.fillMaxSize(),
                contentAlignment    = Alignment.Center
            ) {
                Text(
                    text      = if (state.searchQuery.isBlank())
                        "No notes yet.\nSay \"note buy milk\" to start."
                    else
                        "No notes match \"${state.searchQuery}\"",
                    color     = Color.Gray,
                    style     = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = state.notes,
                    key   = { it.id }
                ) { note ->

                    var dismissed by remember { mutableStateOf(false) }

                    if (!dismissed) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(),
                            shape  = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = surface)
                        ) {
                            Row(
                                modifier          = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text  = note.content,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text  = sdf.format(Date(note.timestamp)),
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                // Copy
                                TextButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(note.content))
                                        Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text("Copy", color = primary)
                                }

                                // Delete
                                IconButton(
                                    onClick = {
                                        dismissed = true
                                        viewModel.deleteNote(note.id)
                                    }
                                ) {
                                    Icon(
                                        imageVector        = Icons.Default.Delete,
                                        contentDescription = "Delete note",
                                        tint               = Color(0xFFEF4444)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}