package com.vasant.pillpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.vasant.pillpal.data.sharedPref.ThemePrefs
import com.vasant.pillpal.ui.theme.PillPalTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePrefs: ThemePrefs

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeName by themePrefs.theme.collectAsState()
            PillPalTheme(themeName = themeName) {
                val windowSizeClass: WindowSizeClass = calculateWindowSizeClass(this@MainActivity)
                KiriReminder(windowSizeClass = windowSizeClass)
            }
        }
    }
}
