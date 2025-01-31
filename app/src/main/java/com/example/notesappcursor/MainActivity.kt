package com.example.notesappcursor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.notesappcursor.data.NoteDatabase
import com.example.notesappcursor.data.NoteRepository
import com.example.notesappcursor.ui.NotesScreen
import com.example.notesappcursor.ui.NotesViewModel
import com.example.notesappcursor.ui.theme.NotesAppCursorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java,
            "notes.db"
        ).build()
        
        val repository = NoteRepository(database.noteDao)
        val viewModel = NotesViewModel(repository)
        
        enableEdgeToEdge()
        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            var darkTheme by remember { mutableStateOf(systemInDarkTheme) }
            
            NotesAppCursorTheme(
                darkTheme = darkTheme,
                dynamicColor = true
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotesApp(
                        viewModel = viewModel,
                        onThemeToggle = { darkTheme = !darkTheme }
                    )
                }
            }
        }
    }
}

@Composable
fun NotesApp(
    viewModel: NotesViewModel,
    onThemeToggle: () -> Unit
) {
    Scaffold { paddingValues ->
        NotesScreen(
            modifier = Modifier.padding(paddingValues),
            viewModel = viewModel,
            onThemeToggle = onThemeToggle
        )
    }
}