package com.example.mycloset.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

object ImageUtils {

    /**
     * Copy an image from URI to app's internal storage
     */
    fun copyImageToInternalStorage(context: Context, sourceUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Create unique filename
            val filename = "clothing_item_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, "clothing_images")

            // Create directory if it doesn't exist
            if (!file.exists()) {
                file.mkdirs()
            }

            val imageFile = File(file, filename)
            val outputStream = FileOutputStream(imageFile)

            // Compress and save bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            outputStream.flush()
            outputStream.close()

            // Return the internal file path
            imageFile.absolutePath

        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Delete an image from internal storage
     */
    fun deleteImageFromInternalStorage(imagePath: String): Boolean {
        return try {
            val file = File(imagePath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Check if image file exists
     */
    fun imageExists(imagePath: String): Boolean {
        return File(imagePath).exists()
    }
}