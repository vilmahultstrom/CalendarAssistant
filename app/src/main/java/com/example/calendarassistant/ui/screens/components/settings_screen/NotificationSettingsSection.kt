package com.example.calendarassistant.ui.screens.components.settings_screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.calendarassistant.R
import com.example.calendarassistant.ui.viewmodels.SettingsVM

@Composable
fun NotificationSettingsSection(
    vm: SettingsVM
) {
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )
    if (!hasNotificationPermission) {
        SettingButton(
            text = "Allow notifications",
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            painterId = R.drawable.baseline_no_adult_content_24
        )
    }
    /* Dev UI
    SettingButton(
        text = "Show test notification",
        onClick = {
            if (hasNotificationPermission) {
                vm.showNotification("Hello World", "this is a test")
            }
        },
        painterId = R.drawable.baseline_notifications_24
    )
    */
}
