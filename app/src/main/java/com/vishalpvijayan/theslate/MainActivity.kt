package com.vishalpvijayan.theslate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vishalpvijayan.theslate.core.navigation.TheSlateNavGraph
import com.vishalpvijayan.theslate.ui.theme.TheSlateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheSlateTheme {
                TheSlateNavGraph()
            }
        }
    }
}
