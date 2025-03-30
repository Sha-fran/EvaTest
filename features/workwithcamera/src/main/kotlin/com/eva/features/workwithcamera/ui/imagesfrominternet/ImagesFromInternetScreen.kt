package com.eva.features.workwithcamera.ui.imagesfrominternet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.eva.features.workwithcamera.R

@Composable
fun ImagesFromInternetScreen(
    viewModel: ImagesFromInternetScreenVM = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    val uiState by viewModel.listOfImagesState.collectAsState()
    val context = LocalContext.current

    Surface(color = Color.White) {

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
                            contentDescription = stringResource(id = R.string.image_from_unsplash),
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    selectedImageUrl = image.url
                                    showDialog = true
                                }
                        )
                    }
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(id = R.string.save_image)) },
            text = { Text(stringResource(id = R.string.what_save_to_gallery)) },
            confirmButton = {
                Button(
                    onClick = {
                        selectedImageUrl?.let { url ->
                            val uri = android.net.Uri.parse(url)
                            viewModel.saveToGallery(context, uri)
                        }
                        showDialog = false
                        selectedImageUrl = null
                    }
                ) {
                    Text(stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        selectedImageUrl = null
                    }
                ) {
                    Text(stringResource(id = R.string.no))
                }
            }
        )
    }
}