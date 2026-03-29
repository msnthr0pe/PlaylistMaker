package com.practicum.playlistmaker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionHelper(private val fragment: Fragment) {

    private val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private val permissionLauncher: ActivityResultLauncher<Array<String>> =
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                onPermissionsGranted()
            } else {
                onPermissionsDenied()
            }
        }

    fun checkAndRequestPermissions(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        this.onPermissionsGranted = onGranted
        this.onPermissionsDenied = onDenied

        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            onGranted()
        } else {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    private var onPermissionsGranted: () -> Unit = {}
    private var onPermissionsDenied: () -> Unit = {}
}