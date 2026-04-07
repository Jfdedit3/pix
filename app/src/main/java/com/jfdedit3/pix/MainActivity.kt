package com.jfdedit3.pix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jfdedit3.pix.ui.PixApp
import com.jfdedit3.pix.ui.theme.PixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PixTheme {
                PixApp()
            }
        }
    }
}
