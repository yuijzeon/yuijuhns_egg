package com.yuijuhn.egg

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CrawlingService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var tess: TessBaseAPI

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

    override fun onServiceConnected() {
        super.onServiceConnected()

        Assets.extractAssets(this);
        tess = TessBaseAPI()
        if (!tess.init(filesDir.absolutePath, "eng")) {
            onDestroy()
            return
        }

        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
        continueCrawling()
    }

    private suspend fun main() {
        captureScreenshot()

        handler.post {
            Toast.makeText(this, "Crawling", Toast.LENGTH_SHORT).show()
        }
    }

    private fun continueCrawling() {
        handler.postDelayed(runnable, 10000L)
    }

    private fun captureScreenshot() {
        val callback = object : TakeScreenshotCallback {
            override fun onSuccess(result: ScreenshotResult) {
                val originalBitmap = Bitmap.wrapHardwareBuffer(result.hardwareBuffer, result.colorSpace)
                val argb8888Bitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)

                tess.setImage(argb8888Bitmap)
                val text = tess.getHOCRText(0)
                Log.d("TINTIN", text)

                handler.post {
                    Toast.makeText(applicationContext, "Got Screenshot", Toast.LENGTH_SHORT).show()
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