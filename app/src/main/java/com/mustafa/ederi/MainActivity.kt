package com.mustafa.ederi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mustafa.ederi.presentation.navigation.EderiNavHost
import com.mustafa.ederi.presentation.theme.EderiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EderiTheme {
                EderiNavHost()
            }
        }
    }
}
