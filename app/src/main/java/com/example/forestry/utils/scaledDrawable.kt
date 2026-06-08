package com.example.forestry.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.createBitmap

fun scaledDrawable(context: Context, resId: Int, sizeDp: Int): Drawable {
    val drawable = ContextCompat.getDrawable(context, resId)!!
    val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()

    val bitmap = createBitmap(sizePx, sizePx)
    val canvas = Canvas(bitmap)

    drawable.setBounds(0, 0, sizePx, sizePx)
    drawable.draw(canvas)

    return bitmap.toDrawable(context.resources)
}