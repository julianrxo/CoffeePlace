package com.example.coffeeplace.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

object RealPathUtil {
    fun getRealPath(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.moveToFirst()
        val index = cursor?.getColumnIndex(projection[0]) ?: return null
        val path = cursor.getString(index)
        cursor.close()
        return path
    }
}
