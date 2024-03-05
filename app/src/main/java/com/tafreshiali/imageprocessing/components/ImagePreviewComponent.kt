package com.tafreshiali.imageprocessing.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmapOrNull
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.tafreshiali.imageprocessing.R
import com.tafreshiali.imageprocessing.util.border.dashedBorder
import com.tafreshiali.imageprocessing.util.effect.shimmerEffect

@Composable
fun ImagePreviewComponent(
    modifier: Modifier,
    imageUri: Uri?,
    onSuccess: (Bitmap?) -> Unit,
    onAddImageClick: () -> Unit
) {
    var retryHash by remember { mutableIntStateOf(0) }
    val request = ImageRequest.Builder(LocalContext.current)
        .size(100)
        .data(imageUri)
        .setParameter("retry_hash", retryHash)
        .allowConversionToBitmap(true)
        .allowHardware(false)
        .bitmapConfig(Bitmap.Config.ARGB_8888)
        .diskCachePolicy(CachePolicy.DISABLED)
        .memoryCachePolicy(CachePolicy.DISABLED)

        .build()


    if (imageUri?.path != null) {
        ImagePreviewEmptyState(onAddImageClick = onAddImageClick)
    } else {
        SubcomposeAsyncImage(
            model = request,
            contentDescription = "Image_Preview",
            modifier = modifier,
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Error -> ImagePreviewErrorState(onRetryClick = {
                    retryHash++
                })

                is AsyncImagePainter.State.Loading -> ImagePreviewLoadingState()
                is AsyncImagePainter.State.Success -> {
                    SubcomposeAsyncImageContent(
                        modifier = Modifier
                            .padding(20.dp)
                            .dashedBorder(
                                color = Color(0xFFD8D8D8),
                                shape = RoundedCornerShape(12.dp)
                            )
                            //.aspectRatio(1f/1f)
                            .padding(1.dp),
                    )
                    onSuccess((painter.state as AsyncImagePainter.State.Success).result.drawable.toBitmapOrNull())
                }

                AsyncImagePainter.State.Empty -> {}
            }
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun ImagePreviewComponentPreview() {
    ImagePreviewComponent(modifier = Modifier, imageUri = null, {}, {})
}

@Composable
private fun ImagePreviewEmptyState(onAddImageClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(20.dp)
            .dashedBorder(
                color = Color(0xFFD8D8D8),
                shape = RoundedCornerShape(12.dp)
            )
            .aspectRatio(16f / 9f)
            .padding(1.dp)
            .background(color = Color(0x59D6D6D6), shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onAddImageClick),
        contentAlignment = Alignment.Center,
        content = {
            Text(text = " + Add Image")
        }
    )
}

@Preview(showSystemUi = true)
@Composable
private fun ImagePreviewEmptyStatePreview() {
    ImagePreviewEmptyState({})
}

@Composable
private fun ImagePreviewErrorState(onRetryClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(20.dp)
            .dashedBorder(
                color = Color(0xFFD8D8D8),
                shape = RoundedCornerShape(12.dp)
            )
            .aspectRatio(16f / 9f)
            .padding(1.dp)
            .background(color = Color(0x59D6D6D6), shape = RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_no_internet_24),
                    contentDescription = "Image_Error_Icon",
                    tint = Color(0xFFFF0000)
                )

                Button(onClick = onRetryClick) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(text = "Retry")
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_retry),
                            contentDescription = "Image_Retry"
                        )
                    }
                }
            }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
private fun ImagePreviewErrorStatePreview() {
    ImagePreviewErrorState({})
}

@Composable
private fun ImagePreviewLoadingState() {
    Box(
        modifier = Modifier
            .padding(20.dp)
            .dashedBorder(
                color = Color(0xFFD8D8D8),
                shape = RoundedCornerShape(12.dp)
            )
            .aspectRatio(16f / 9f)
            .padding(1.dp)
            .background(color = Color(0x59D6D6D6), shape = RoundedCornerShape(12.dp))
            .shimmerEffect()
    )
}

@Preview(showSystemUi = true)
@Composable
fun ImagePreviewLoadingStatePreview() {
    ImagePreviewLoadingState()
}





