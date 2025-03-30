package com.eva.features.workwithcamera.ui.imagesfrominternet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun ImagesFromInternetScreen(
    viewModel: ImagesFromInternetScreenVM = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    Surface(color = Color.White) {
        val uiState by viewModel.listOfImagesState.collectAsState()

        BackHandler(onBack = onBack)

        Scaffold(modifier = Modifier.systemBarsPadding()) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.padding(paddingValues))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = uiState.listOfImages) { image ->
                        AsyncImage(
                            model = image.url,
                            contentDescription = "Image from Unsplash",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}