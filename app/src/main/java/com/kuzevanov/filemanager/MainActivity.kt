package com.kuzevanov.filemanager

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

import com.kuzevanov.filemanager.navigation.SetupNavGraph
import com.kuzevanov.filemanager.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                onActivityResult.launch(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                    )
                )
            } else {
                onPermissionsGranted()
            }
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        } else {
            this.onPermissionsGranted()
        }
    }

    private val onActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                this.onPermissionsGranted()
            } else {
                this.onPermissionsRejected()
            }
        }
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach { actionMap ->
                when (actionMap.key) {
                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        if (actionMap.value) {
                            this.onPermissionsGranted()
                        } else {
                            this.onPermissionsRejected()
                        }
                    }

                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                        if (actionMap.value) {
                            this.onPermissionsGranted()
                        } else {
                            this.onPermissionsRejected()
                        }
                    }
                }
            }
        }

    @OptIn(ExperimentalAnimationApi::class)
    private fun onPermissionsGranted() {
        setContent() {
            MyApplicationTheme() {
                SetupNavGraph(navHostController = rememberAnimatedNavController())
            }
        }
    }

    /**
     * The ending callback for if permissions are rejected, this sends a toast error and then closes the app.
     */
    private fun onPermissionsRejected() {
        Toast.makeText(
            this,
            "This app requires external storage permissions to function.",
            Toast.LENGTH_LONG
        ).show()
        this.finishAndRemoveTask()
    }

}
