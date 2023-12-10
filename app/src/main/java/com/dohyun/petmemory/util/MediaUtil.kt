package com.dohyun.petmemory.util

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

class MediaUtil @Inject constructor(@ApplicationContext private val context: Context) {

    private val rootPath = Environment.getExternalStorageDirectory().toString()

    //Uri -> Path(파일경로)
    @SuppressLint("Range")
    fun convertUriToPath(contentUri: Uri?): String? {
        if (contentUri?.path?.startsWith("/document") == true) {
            return getDocumentPath(uri = contentUri)
        }

        var path: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
        cursor?.moveToNext()

        val cursorIndex = cursor?.getColumnIndex(MediaStore.MediaColumns.DATA) ?: -1

        if (cursorIndex >= 0) {
            path = cursor?.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
        }

        cursor?.close()
        return path
    }

    fun getDocumentPath(uri: Uri): String? {
        return try {
            var filePath = ""
            val fileId: String = DocumentsContract.getDocumentId(uri)
            val id = fileId.split(":".toRegex())[1]
            val column = arrayOf(MediaStore.Images.Media.DATA)
            val selector = MediaStore.Images.Media._ID + "=?"
            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, selector, arrayOf(id), null
            )
            cursor?.use {
                val columnIndex: Int = cursor.getColumnIndex(column[0])
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex)
                }
            }
            return filePath
        } catch (e: SecurityException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    //Path(파일경로) -> Uri
    @SuppressLint("Range")
    fun convertPathToUri(filePath: String): Uri? {
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
            "_data = '$filePath'", null, null
        )
        cursor!!.moveToNext()
        val cursorIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)

        var id: Int? = null

        if (cursorIndex >= 0) {
            id = cursor.getInt(cursor.getColumnIndex("_id"))
        }

        cursor.close()

        return id?.let {
            ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toLong())
        } ?: kotlin.run {
            null
        }
    }

    fun saveImageToGalleryPrior10(bitmap: Bitmap, title: String) {
        val directory = File("$rootPath/PetMemory")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "$title.jpg")
        var outputStream: OutputStream? = null

        try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
        }
    }

    fun saveImageToGallery(
        bitmap: Bitmap,
        title: String = System.currentTimeMillis().toString(),
        lifecycleOwner: LifecycleOwner? = null
    ) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, title)
            //put(MediaStore.Images.Media.DESCRIPTION, description)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PetMemory")
            values.put(MediaStore.Images.Media.IS_PENDING, true)
        }

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
                if (uri != null) {
                    // Now that the image is inserted, we can open an output stream and save the bitmap to it.
                    val outputStream = context.contentResolver.openOutputStream(uri)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream?.close()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // If using Android 10 or higher, mark the image as not pending anymore.
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, false)
                        context.contentResolver.update(uri, values, null, null)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getContentUri(uri: Uri): Uri? {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DURATION,
            MediaStore.Images.Media.SIZE
        )
        val sortOrder = "${MediaStore.Images.Media.DISPLAY_NAME} ASC"

        val query = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                return ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
            }
        }
        return null
    }

    fun getBitmapFromUri(uri: Uri): Bitmap {
        val getUri = if (uri.toString().startsWith(rootPath)) {
            convertPathToUri(filePath = uri.toString())
        } else {
            uri
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, getUri!!)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, getUri!!)
        }
    }
}