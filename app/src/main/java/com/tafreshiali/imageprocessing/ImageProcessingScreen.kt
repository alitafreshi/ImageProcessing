package com.tafreshiali.imageprocessing

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.canhub.cropper.CropImageContract
import com.tafreshiali.imageprocessing.components.ImagePreviewComponent


@Composable
fun ImageProcessingScreen(imageProcessingViewModel: ImageProcessingViewModel = ImageProcessingViewModel()) {

    val imageCropLauncher =
        rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
            if (result.isSuccessful) {
                imageProcessingViewModel.updateImageUri(result.uriContent)
            } else {

            }
        }

    Scaffold { innerPadding ->

        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            item {
                ImagePreviewComponent(
                    modifier = Modifier,
                    imageUrl = userInput,
                    onSuccess = { bitmap ->
                        bitmap?.let {
                            imageProcessingViewModel.calculateRgbMatrices(image = bitmap)
                        } ?: run {
                            Log.d("ImagePreviewComponent", "There is no bitmap")
                        }
                    },
                    onAddImageClick = {

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
