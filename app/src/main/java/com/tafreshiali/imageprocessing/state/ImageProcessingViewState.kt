package com.tafreshiali.imageprocessing.state

import android.graphics.Bitmap
import android.net.Uri

data class ImageProcessingViewState(
    val selectedImageBitmap: Bitmap? = null,
    val imageUri: Uri? = null,
    val imageColorMatrix: RGBMatrix? = null
)
