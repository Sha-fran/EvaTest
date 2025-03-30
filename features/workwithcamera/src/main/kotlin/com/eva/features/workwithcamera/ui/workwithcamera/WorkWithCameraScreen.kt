package com.eva.features.workwithcamera.ui.workwithcamera

import android.Manifest
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.eva.core.utils.extensions.getCameraProvider
import com.eva.core.utils.filters.ImageFilter
import com.eva.core.utils.photo.takePhoto


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkWithCameraScreen(
    viewModel: WorkWithCameraScreenVM = hiltViewModel(),
    onNavigate: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onEvent(
            if (isGranted) CameraEvent.PermissionGranted(Manifest.permission.CAMERA)
            else CameraEvent.PermissionDenied(Manifest.permission.CAMERA)
        )
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onEvent(
            if (isGranted) CameraEvent.PermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            else CameraEvent.PermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        )
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.onEvent(CameraEvent.ImageCaptured(it)) }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            viewModel.onEvent(CameraEvent.PermissionGranted(Manifest.permission.CAMERA))
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                galleryPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                viewModel.onEvent(CameraEvent.PermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            }
        } else {
            viewModel.onEvent(CameraEvent.PermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Camera & Gallery") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (uiState.showCameraView && uiState.hasCameraPermission) {
                    CameraView(
                        lensFacing = uiState.lensFacing,
                        onImageCaptured = { uri -> viewModel.onEvent(CameraEvent.ImageCaptured(uri)) },
                        onError = { exception ->
                            Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                        },
                        onTakePhoto = { imageCapture -> viewModel.onEvent(CameraEvent.TakePhoto(imageCapture)) }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(
                            onClick = { viewModel.onEvent(CameraEvent.SwitchCamera) },
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0x88000000))
                        ) {
                            Text(
                                text = "↺",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                        }

                        IconButton(
                            onClick = { viewModel.onEvent(CameraEvent.OpenGallery) },
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0x88000000))
                        ) {
                            Text(
                                text = "×",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color.LightGray, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.imageUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(uiState.imageUri),
                                    contentDescription = "Captured image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Text(
                                    "No image selected",
                                    textAlign = TextAlign.Center,
                                    color = Color.DarkGray
                                )
                            }
                        }

                        if (uiState.imageUri != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = { viewModel.onEvent(CameraEvent.ApplyFilter(ImageFilter.GRAYSCALE)) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                ) {
                                    Text("Gray")
                                }
                                Button(
                                    onClick = { viewModel.onEvent(CameraEvent.ApplyFilter(ImageFilter.SEPIA)) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                ) {
                                    Text("Sepia")
                                }
                                Button(
                                    onClick = { viewModel.onEvent(CameraEvent.ApplyFilter(ImageFilter.BRIGHTNESS)) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                ) {
                                    Text("Bright")
                                }
                                Button(
                                    onClick = { viewModel.onEvent(CameraEvent.ApplyFilter(ImageFilter.NONE)) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                ) {
                                    Text("Reset")
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ElevatedButton(
                                onClick = { viewModel.onEvent(CameraEvent.OpenCamera) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text("Camera")
                            }

                            ElevatedButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text("Gallery")
                            }

                            ElevatedButton(
                                onClick = { onNavigate() },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text("Internet")
                            }
                        }

                        Button(
                            onClick = { viewModel.onEvent(CameraEvent.SaveToGallery) },
                            enabled = uiState.imageUri != null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save to Gallery")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CameraView(
    lensFacing: Int,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    onTakePhoto: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = remember(lensFacing) {
        CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
    }

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = previewView.surfaceProvider
                },
            imageCapture
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {
                takePhoto(
                    imageCapture = imageCapture,
                    context = context,
                    onImageCaptured = onImageCaptured,
                    onError = onError
                )
                onTakePhoto(imageCapture)
            },
            modifier = Modifier
                .padding(bottom = 100.dp)
                .size(80.dp)
                .clip(CircleShape),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
            )
        }
    }
}
