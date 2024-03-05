package com.tafreshiali.imageprocessing

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log.d
import androidx.compose.runtime.internal.composableLambdaInstance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canhub.cropper.CropImage.CancelledResult.bitmap
import com.tafreshiali.imageprocessing.state.ImageProcessingViewState
import com.tafreshiali.imageprocessing.state.RGBMatrix
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImageProcessingViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(ImageProcessingViewState())
    val viewState = _viewState.asStateFlow()

    fun updateImageUri(imageUri: Uri?) {
        d("IMAGE_URI", "uri is = $imageUri")
        _viewState.update {
            it.copy(imageUri = imageUri)
        }
    }

    private fun Bitmap.isBitmapValid(): Boolean = !this.isRecycled

    fun calculateRgbMatrices(image: Bitmap): RGBMatrix {
        if (image.isBitmapValid()) {
            d("calculateRgbMatrices", "bitmap is valid")
        }

        val matrixRowCount = image.width
        val matrixColumnCount = image.height

        d("MATRIX_SIZE", "rowCount = $matrixRowCount | columnCount = $matrixColumnCount")

        val deferredList = mutableListOf<Deferred<Unit>>()

        val redMatrix = Array(matrixRowCount) { IntArray(matrixColumnCount) }
        val greenMatrix = Array(matrixRowCount) { IntArray(matrixColumnCount) }
        val blueMatrix = Array(matrixRowCount) { IntArray(matrixColumnCount) }
        viewModelScope.launch {
            for (row in 0 until matrixRowCount) {
                val heightDeferred = viewModelScope.async(Dispatchers.Default) {
                    for (column in 0 until matrixColumnCount) {
                        val widthDeferred = viewModelScope.async(Dispatchers.Default) {
                            val pixel = image.getPixel(row, column)
                            redMatrix[row][column] = Color.red(pixel)
                            greenMatrix[row][column] = Color.green(pixel)
                            blueMatrix[row][column] = Color.blue(pixel)
                        }
                        deferredList.add(widthDeferred)
                    }
                }
                deferredList.add(heightDeferred)
            }
            deferredList.awaitAll()
        }
        redMatrix.printMathematicalMatrix("Red_Matrix")
        greenMatrix.printMathematicalMatrix("Green_Matrix")
        blueMatrix.printMathematicalMatrix("Blue_Matrix")

        return RGBMatrix(red = redMatrix, green = greenMatrix, blue = blueMatrix)
    }

    private fun Array<IntArray>.printMathematicalMatrix(matrixName: String) {
        val stringBuilder = StringBuilder()
        stringBuilder.append("$matrixName:\n")
        //Print Header
        this[0].forEachIndexed { columnIndex, _ ->
            stringBuilder.append("$columnIndex ")
        }

        stringBuilder.append("\n------------------------------------------------------------------\n")

        this.forEachIndexed { rowIndex, _ ->
            this[rowIndex].forEachIndexed { columnIndex, _ ->
                if (columnIndex == this[rowIndex].lastIndex) {
                    stringBuilder.append("\n")
                }
                stringBuilder.append("${this[rowIndex][columnIndex]} ")
            }
        }
        d(matrixName, stringBuilder.toString())
    }
}




