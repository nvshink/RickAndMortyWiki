package com.nvshink.rickandmortywiki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.nvshink.rickandmortywiki.ui.RickAndMortyWikiApp
import com.nvshink.rickandmortywiki.ui.theme.RickAndMortyWikiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RickAndMortyWikiTheme {
                Surface {
                    val windowSize = calculateWindowSizeClass(this)
                    RickAndMortyWikiApp(windowSize = windowSize.widthSizeClass)
                }
            }
        }
    }
}