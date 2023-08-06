package com.yuijuhn.egg

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.google.gson.Gson
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


class CrawlingService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var tess: TessBaseAPI

    private val runnable = Runnable {
        try {
            main()
        } finally {
            continueCrawling()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        Assets.extractAssets(this)
        tess = TessBaseAPI()
        if (!tess.init(filesDir.absolutePath, "chi_tra")) {
            onDestroy()
            return
        }

        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
        continueCrawling()
    }

    private fun main() {
        captureScreenshot()

//        handler.post {
//            Toast.makeText(this, "Crawling", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun continueCrawling() {
        handler.postDelayed(runnable, 10000L)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun captureScreenshot() {
        val callback = object : TakeScreenshotCallback {
            override fun onSuccess(result: ScreenshotResult) {
                val originalBitmap = Bitmap.wrapHardwareBuffer(result.hardwareBuffer, result.colorSpace)
                val argb8888Bitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)

                if (argb8888Bitmap == null) {
                    Log.d("TINTIN", "argb8888Bitmap is null")
                    return
                }

                tess.setImage(argb8888Bitmap)
                val text = tess.getHOCRText(1)
                Log.d("TINTIN", text)

                handler.post {
                    Toast.makeText(applicationContext, tess.utF8Text, Toast.LENGTH_SHORT).show()
                }

                val json = object {
                    val pages: MutableList<String> = mutableListOf(text)
                }
                val body = Gson().toJson(json).toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("https://script.google.com/macros/s/AKfycbyAMg7YzDSMWGk0cAePOYEIX4aZsfj_m6W5xPQdZXDdye0l0XsGIaT-cCsPGHIUqT6x/exec")
                    .post(body)

                GlobalScope.launch(Dispatchers.IO) {
                    val response = OkHttpClient().newCall(request.build()).execute()
                    Log.d("TINTIN", "Response: " + response.body?.string())
                }
            }

            override fun onFailure(errorCode: Int) {
                Log.d("TINTIN", "onFailure")
            }
        }

        takeScreenshot(Display.DEFAULT_DISPLAY, applicationContext.mainExecutor, callback)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
//        Log.d("TINTIN", "onAccessibilityEvent")
    }

    override fun onInterrupt() {
        Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show()
        onDestroy()
    }

    override fun onDestroy() {
        Toast.makeText(this, "Bye bye", Toast.LENGTH_SHORT).show()
        handler.removeCallbacks(runnable)
        tess.recycle()
        super.onDestroy()
    }
}