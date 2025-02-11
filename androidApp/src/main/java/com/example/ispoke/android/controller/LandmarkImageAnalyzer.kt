package com.example.ispoke.android.controller

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.ispoke.android.utils.asBitmap
import com.example.ispoke.android.utils.centerCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LandmarkImageAnalyzer(
    private val detector: LandmarkDetector,
    private val onResults: (List<Detection>) -> Unit
) : ImageAnalysis.Analyzer {

    @Volatile
    private var isProcessing = false
    private var frameSkipCounter = 0

    override fun analyze(image: ImageProxy) {
        if (frameSkipCounter % 60 == 0 && !isProcessing) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            // Converte a imagem para Bitmap; certifique-se de que `asBitmap()` não retorne null.
            val bitmap = image.asBitmap()?.centerCrop(352, 352)
            image.close() // Liberar o recurso logo após a conversão

            if (bitmap != null) {
                isProcessing = true
                // Despacha a inferência para um thread de background
                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        val results = detector.detect(bitmap, rotationDegrees)
                        withContext(Dispatchers.Main) {
                            onResults(results)
                        }
                    } catch (e: Exception) {
                        // Registre o erro para debugar
                        e.printStackTrace()
                    } finally {
                        isProcessing = false
                    }
                }
            }
        } else {
            frameSkipCounter++
            image.close()
        }
    }
}
