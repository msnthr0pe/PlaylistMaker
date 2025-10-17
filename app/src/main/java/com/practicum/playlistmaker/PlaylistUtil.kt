package com.practicum.playlistmaker

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

object PlaylistUtil {
    fun getFormattedTime(time: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

    fun getHigherResolutionPic(url: String): String {
        return url.replaceAfterLast('/', "512x512bb.jpg")
    }

    fun loadPicInto(context: Context, url: String, image: ImageView) {
        Glide.with(context)
            .load(url)
            .centerCrop()
            .transform(RoundedCorners(6))
            .placeholder(R.drawable.ic_placeholder)
            .into(image)
    }
}