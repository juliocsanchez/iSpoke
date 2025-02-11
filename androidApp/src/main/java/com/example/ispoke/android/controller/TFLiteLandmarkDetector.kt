package com.example.ispoke.android.controller

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteLandmarkDetector(
    private val context: Context,
    private val threshold: Float = 0.01f
) : LandmarkDetector {

    private var interpreter: Interpreter? = null
    private val inputSize = 352
    // Lista de rótulos (letras) conforme seu metadata
    private val labels = listOf("A", "B", "C", "D1", "D2", "E", "F", "G",
        "I", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W")
    private val numClasses = labels.size
    private val numDetections = 2541

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val modelBuffer: ByteBuffer = loadModelFile(context, "best_float32M.tflite")
            interpreter = Interpreter(modelBuffer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun detect(bitmap: Bitmap, rotation: Int): List<Detection> {
        val inputBuffer = preprocess(bitmap)
        // Cria um buffer de saída com formato [1, 26, numDetections]
        val output = Array(1) { Array(26) { FloatArray(numDetections) } }
        interpreter?.run(inputBuffer, output)

        // Log para depuração: mostra os valores da primeira detecção
        val firstDetection = FloatArray(26) { channel -> output[0][channel][0] }
        Log.d("TFLite", "Primeira detecção: ${firstDetection.joinToString(", ")}")

        var bestClassIndex = -1
        var bestScore = 0f
        // Assumindo que os canais 4 a 25 correspondem às scores para cada classe
        for (i in 0 until 26) {
            val classMax = output[0][i].maxOrNull() ?: 0f
            if (classMax > bestScore) {
                bestScore = classMax
                bestClassIndex = i - 4
            }
        }

        return if (bestScore > threshold && bestClassIndex != -1) {
            val detectedLabel = labels[bestClassIndex]
            // Obtém as coordenadas da bounding box (assumindo que os canais 0 a 3 são [x, y, w, h])
            val x = output[0][0][0]
            val y = output[0][1][0]
            val w = output[0][2][0]
            val h = output[0][3][0]
            val boundingBox = RectF(
                x * bitmap.width,
                y * bitmap.height,
                (x + w) * bitmap.width,
                (y + h) * bitmap.height
            )
            Log.d("TFLite", "Detecção válida: Label = $detectedLabel, Confiança = $bestScore")
            listOf(Detection(detectedLabel, bestScore, boundingBox))
        } else {
            Log.d("TFLite", "Nenhuma detecção acima do threshold. BestScore = $bestScore, Threshold = $threshold")
            emptyList()
        }
    }

    private fun preprocess(bitmap: Bitmap): ByteBuffer {
        val inputBuffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val value = intValues[pixel++]
                inputBuffer.putFloat(((value shr 16 and 0xFF) / 255.0f))
                inputBuffer.putFloat(((value shr 8 and 0xFF) / 255.0f))
                inputBuffer.putFloat(((value and 0xFF) / 255.0f))
            }
        }
        inputBuffer.rewind()
        return inputBuffer
    }

    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
