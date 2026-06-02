package com.drivenote.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.drivenote.app.ui.navigation.DriveNoteNavHost
import com.drivenote.app.ui.theme.DriveNoteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DriveNoteTheme {
                DriveNoteNavHost()
            }
        }
    }
}
