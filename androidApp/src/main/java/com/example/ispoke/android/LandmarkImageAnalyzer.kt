package com.example.ispoke.android

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class LandmarkImageAnalyzer(
    private val classificator: LandmarkClassificator,
    private val onResults: (List<Classificator>) -> Unit
): ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0

    override fun analyze(image: ImageProxy) {

        if(frameSkipCounter % 60 == 0){
            val rotationDegrees =   image.imageInfo.rotationDegrees
            val bitmap = image
                .toBitmap()
                .centerCrop(640,640)

            val results = classificator.classify(bitmap, rotationDegrees)
            onResults(results)

        }
        frameSkipCounter++

        image.close()
    }
}