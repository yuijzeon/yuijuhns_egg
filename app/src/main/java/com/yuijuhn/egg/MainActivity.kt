package com.yuijuhn.egg

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        super.onCreate(savedInstanceState)
    }
}