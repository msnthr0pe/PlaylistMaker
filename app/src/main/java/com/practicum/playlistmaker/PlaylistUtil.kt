package com.practicum.playlistmaker

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
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

    enum class SnackbarLayoutTypes {
        FRAME_LAYOUT,
        COORDINATOR_LAYOUT,
    }

    fun showSnackbar(
        rootView: View, message: String,
        snackbarLayoutType: SnackbarLayoutTypes = SnackbarLayoutTypes.FRAME_LAYOUT
    ) {
        val sb = Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT)

        val container = sb.view as ViewGroup
        container.setPadding(0, 0, 0, 0)
        container.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        container.removeAllViews()

        val custom = LayoutInflater.from(rootView.context)
            .inflate(R.layout.custom_snackbar, container, false)

        custom.findViewById<TextView>(R.id.toast_text).text = message
        container.addView(custom)

        when (snackbarLayoutType) {
            SnackbarLayoutTypes.FRAME_LAYOUT -> {
                sb.view.updateLayoutParams<FrameLayout.LayoutParams> {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    gravity = Gravity.BOTTOM
                    val m = rootView.resources.getDimensionPixelSize(R.dimen.small_margin)
                    val g = rootView.resources.getDimensionPixelSize(R.dimen.snackbar_bottombar_margin)
                    setMargins(m, 0, m, g)
                }
            }
            SnackbarLayoutTypes.COORDINATOR_LAYOUT -> {
                sb.view.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    gravity = Gravity.BOTTOM
                    val m = rootView.resources.getDimensionPixelSize(R.dimen.small_margin)
                    val g = rootView.resources.getDimensionPixelSize(R.dimen.snackbar_bottombar_margin)
                    setMargins(m, 0, m, g)
                }
            }
        }
        sb.show()
    }

    fun showAlertDialog(
        context: Context,
        title: String,
        message: String,
        negativeBtnTitle: String,
        positiveBtnTitle: String,
        negativeBtnAction: () -> Unit,
        positiveBtnAction: () -> Unit,
    ) {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(negativeBtnTitle) { dialog, which ->
                negativeBtnAction()
            }
            .setPositiveButton(positiveBtnTitle) { dialog, which ->
                positiveBtnAction()
            }
            .show()

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)?.apply {
            setTextColor(context.getColor(R.color.blue))
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)?.apply {
            setTextColor(context.getColor(R.color.blue))
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
    }
}