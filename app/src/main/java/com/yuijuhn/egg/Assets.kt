package com.yuijuhn.egg

import android.content.Context
import android.content.res.AssetManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

// It is from the sample of Tesseract4Android.
// https://github.com/adaptech-cz/Tesseract4Android/blob/master/sample/src/main/java/cz/adaptech/tesseract4android/sample/Assets.java
class Assets {
    companion object {
        fun extractAssets(context: Context) {
            val am = context.assets
            val localDir = context.filesDir
            if (!localDir.exists() && !localDir.mkdir()) {
                throw RuntimeException("Can't create directory $localDir")
            }
            val tessDir = File(context.filesDir.absolutePath, "tessdata")
            if (!tessDir.exists() && !tessDir.mkdir()) {
                throw RuntimeException("Can't create directory $tessDir")
            }

            try {
                for (assetName in am.list("")!!) {
                    val targetFile = if (assetName.endsWith(".traineddata")) {
                        File(tessDir, assetName)
                    } else {
                        File(localDir, assetName)
                    }
                    if (!targetFile.exists()) {
                        copyFile(am, assetName, targetFile)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        private fun copyFile(am: AssetManager, assetName: String, outFile: File) {
            try {
                am.open(assetName).use { input ->
                    FileOutputStream(outFile).use { output ->
                        val buffer = ByteArray(1024)
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
