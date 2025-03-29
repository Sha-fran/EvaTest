package com.eva.evatest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eva.evatest.ui.theme.EvaTestTheme
import com.eva.features.workwithcamera.ui.WorkWithCameraScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EvaTestTheme {
                WorkWithCameraScreen()
            }
        }
    }
}