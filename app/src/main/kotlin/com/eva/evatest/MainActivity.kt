package com.eva.evatest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.eva.evatest.ui.theme.EvaTestTheme
import com.eva.features.workwithcamera.ui.imagesfrominternet.ImagesFromInternetScreen
import com.eva.features.workwithcamera.ui.workwithcamera.WorkWithCameraScreen
import com.eva.navigation.Route
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(35)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EvaTestTheme {
                val stack: SnapshotStateList<Route> = remember { mutableStateListOf(Route.WorkWithCameraScreen) }
                val currentRoute = stack.last()
                val isRoot = stack.size == 1

                when (currentRoute) {
                    Route.WorkWithCameraScreen -> WorkWithCameraScreen(onNavigate = { stack.add(Route.ImagesFromInternet) })
                    Route.ImagesFromInternet -> ImagesFromInternetScreen(onBack = { if (!isRoot) stack.removeLast() })
                }
            }
        }
    }
}