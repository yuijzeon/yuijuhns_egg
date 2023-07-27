package com.yuijuhn.egg

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class CrawlingService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        Toast.makeText(this, "Do Something", Toast.LENGTH_SHORT).show()
        rootInActiveWindow
        continueCrawling()
    }

    private fun continueCrawling() {
        handler.postDelayed(runnable, 5000L)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
        continueCrawling()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d("TINTIN", "onAccessibilityEvent")
    }

    override fun onInterrupt() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        handler.removeCallbacks(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Bye bye", Toast.LENGTH_SHORT).show()
        handler.removeCallbacks(runnable)
    }
}