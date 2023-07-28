package com.yuijuhn.egg

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.google.gson.Gson
import com.yuijuhn.egg.extensions.toEggNode
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


class CrawlingService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private var times = 1

    @OptIn(DelicateCoroutinesApi::class)
    private val runnable = Runnable {
        GlobalScope.launch {
            try {
                main()
            } finally {
                continueCrawling()
            }
        }
    }

    private suspend fun main() {
        val json = object {
            val pages: MutableList<String> =
                mutableListOf(Gson().toJson(rootInActiveWindow?.toEggNode()))
        }

        val request = Request.Builder()
            .url("https://script.google.com/macros/s/AKfycbyAMg7YzDSMWGk0cAePOYEIX4aZsfj_m6W5xPQdZXDdye0l0XsGIaT-cCsPGHIUqT6x/exec")
            .post(Gson().toJson(json).toRequestBody("application/json".toMediaType()))

        withContext(Dispatchers.IO) {
            return@withContext OkHttpClient().newCall(request.build()).execute()
        }

        handler.post {
            Toast.makeText(applicationContext, "$times Crawling", Toast.LENGTH_SHORT).show()
        }

        times++
    }

    private fun continueCrawling() {
        handler.postDelayed(runnable, 10000L)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
        continueCrawling()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
//        Log.d("TINTIN", event?.className.toString())
    }

    override fun onInterrupt() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        handler.removeCallbacks(runnable)
    }

    override fun onDestroy() {
        Toast.makeText(this, "Bye bye", Toast.LENGTH_SHORT).show()
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }
}