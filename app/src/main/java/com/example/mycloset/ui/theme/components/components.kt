package com.example.mycloset.ui.theme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.graphics.BitmapFactory
import android.net.Uri
import coil.compose.AsyncImage
import java.io.File

@Composable
fun ClothingItemImage(
    imageUri: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current

    if (imageUri.startsWith("/")) {
        // Internal storage file path
        var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(imageUri) {
            try {
                val file = File(imageUri)
                if (file.exists()) {
                    bitmap = BitmapFactory.decodeFile(imageUri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }

        Box(modifier = modifier) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                bitmap?.let {
                    Image(
                        painter = BitmapPainter(it.asImageBitmap()),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = contentScale
                    )
                }
            }
        }
    } else {
        // Use Coil for content URIs or other URIs
        AsyncImage(
            model = imageUri,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}