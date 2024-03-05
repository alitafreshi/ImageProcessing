package com.tafreshiali.imageprocessing

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.canhub.cropper.CropImage.CancelledResult.uriContent
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.tafreshiali.imageprocessing.components.ImagePreviewComponent
import kotlinx.coroutines.launch

@Composable
fun ImageProcessingScreen(imageProcessingViewModel: ImageProcessingViewModel = ImageProcessingViewModel()) {

    val state = imageProcessingViewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val imageCropLauncher =
        rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
            if (result.isSuccessful) {
                imageProcessingViewModel.updateImageUri(result.uriContent)
            } else {
                result.error?.message?.let {
                    scope.launch {
                        snackbarHostState.showSnackbar("There Is Problem With Selected Image")
                    }
                }
            }
        }

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                ImagePreviewComponent(
                    modifier = Modifier,
                    imageUri = state.value.imageUri,
                    onSuccess = { bitmap ->
                        bitmap?.let {
                            imageProcessingViewModel.calculateRgbMatrices(image = bitmap)
                        } ?: run {
                            Log.d("ImagePreviewComponent", "There is no bitmap")
                        }
                    },
                    onAddImageClick = {
                        val cropOption =
                            CropImageContractOptions(
                                uri = uriContent,
                                cropImageOptions = CropImageOptions()
                            )
                        imageCropLauncher.launch(cropOption)
                    })
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ImageProcessingScreenPreview() {
    ImageProcessingScreen()
}
