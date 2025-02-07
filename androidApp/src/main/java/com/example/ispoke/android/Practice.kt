package com.example.ispoke.android

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size as ComposeSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.metadata.MetadataExtractor
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max
import kotlin.math.min

// --- Estruturas de dados para armazenar as detecções ---
data class BoundingBox(val left: Float, val top: Float, val right: Float, val bottom: Float)

data class DetectionResult(
    val className: String,
    val confidence: Float,
    val boundingBox: BoundingBox
)

// Flag para controle de processamento dos frames
private val isProcessingFrame = AtomicBoolean(false)


// --- Tela principal que exibe as detecções e a letra estável ---
@Composable
fun Practice(navController: NavController) {
    var detectionResults by remember { mutableStateOf<List<DetectionResult>>(emptyList()) }
    // Histórico das letras detectadas para votação majoritária
    val recentDetections = remember { mutableStateListOf<String>() }
    val maxHistorySize = 10
    var currentStableLetter by remember { mutableStateOf("") }

    // A cada novo conjunto de resultados, atualiza o histórico e computa a letra estável
    LaunchedEffect(detectionResults) {
        if (detectionResults.isNotEmpty()) {
            // Seleciona a letra com maior confiança do frame atual
            val bestDetection = detectionResults.maxByOrNull { it.confidence }
            bestDetection?.let { detection ->
                recentDetections.add(detection.className)
                if (recentDetections.size > maxHistorySize) {
                    recentDetections.removeAt(0)
                }
                // Votação majoritária: conta quantas vezes cada letra apareceu
                val frequencyMap = recentDetections.groupingBy { it }.eachCount()
                currentStableLetter = frequencyMap.maxByOrNull { it.value }?.key ?: ""
            }
        } else {
            recentDetections.clear()
            currentStableLetter = ""
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        YOLOCameraHandler(
            onDetectionResults = { results ->
                detectionResults = results
            }
        )
        DetectionOverlay(detectionResults, navController)
        DetectedLetterDisplay(letter = currentStableLetter)
    }
}

// Composable que exibe a letra estável na parte superior da tela
@Composable
fun DetectedLetterDisplay(letter: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        if (letter.isNotEmpty()) {
            Text(
                text = "Letra: $letter",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

// --- Composable que desenha os bounding boxes e o botão de navegação ---
@Composable
private fun DetectionOverlay(results: List<DetectionResult>, navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Canvas para desenhar os bounding boxes e os rótulos
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Escala para converter as coordenadas do modelo (640x640) para o tamanho atual da tela
            val scaleX = size.width / 640f
            val scaleY = size.height / 640f
            results.forEach { result ->
                val box = result.boundingBox
                drawRect(
                    color = Color.Red,
                    topLeft = Offset(box.left * scaleX, box.top * scaleY),
                    size = ComposeSize((box.right - box.left) * scaleX, (box.bottom - box.top) * scaleY),
                    style = Stroke(width = 4f)
                )
                // Desenha o rótulo com a letra e a confiança
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "${result.className} ${(result.confidence * 100).toInt()}%",
                        box.left * scaleX,
                        box.top * scaleY - 10,
                        Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 36f
                        }
                    )
                }
            }
        }
        // Botão para voltar para o menu
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Voltar para o Menu")
            }
        }
    }
}

// --- Gerencia a captura da câmera e a execução do modelo ---
@Composable
fun YOLOCameraHandler(
    onDetectionResults: (List<DetectionResult>) -> Unit,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val yoloModel = remember { YOLOModel(context) }

    Box(modifier = modifier.fillMaxSize()) {
        CameraPreview(cameraExecutor, lifecycleOwner, yoloModel, onDetectionResults)
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            yoloModel.close()
        }
    }
}

@Composable
private fun CameraPreview(
    cameraExecutor: ExecutorService,
    lifecycleOwner: LifecycleOwner,
    yoloModel: YOLOModel,
    onDetectionResults: (List<DetectionResult>) -> Unit
) {
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                setupCamera(lifecycleOwner, yoloModel, cameraExecutor, onDetectionResults)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

private fun PreviewView.setupCamera(
    lifecycleOwner: LifecycleOwner,
    yoloModel: YOLOModel,
    cameraExecutor: ExecutorService,
    onResults: (List<DetectionResult>) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val executor = ContextCompat.getMainExecutor(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        bindCameraUseCases(cameraProvider, lifecycleOwner, yoloModel, executor, onResults)
    }, executor)
}

private fun PreviewView.bindCameraUseCases(
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    yoloModel: YOLOModel,
    executor: java.util.concurrent.Executor,
    onResults: (List<DetectionResult>) -> Unit
) {
    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(surfaceProvider)
    }
    val imageAnalysis = ImageAnalysis.Builder()
        .setTargetResolution(Size(640, 640))
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build().also {
            it.setAnalyzer(executor) { image ->
                processYOLOFrame(image, yoloModel, onResults)
            }
        }
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        CameraSelector.DEFAULT_BACK_CAMERA,
        preview,
        imageAnalysis
    )
}

@OptIn(ExperimentalGetImage::class)
private fun processYOLOFrame(
    imageProxy: ImageProxy,
    yoloModel: YOLOModel,
    onResults: (List<DetectionResult>) -> Unit
) {
    // Verifica se já há um frame sendo processado
    if (!isProcessingFrame.compareAndSet(false, true)) {
        imageProxy.close()
        return
    }
    try {
        val tensorImage = imageProxy.toYOLOInputTensor()
        val outputBuffer = yoloModel.createOutputBuffer()
        yoloModel.predict(tensorImage, outputBuffer)
        val results = processYOLOOutput(outputBuffer, yoloModel.classNames)

        // Log para debug: mostra todas as detecções
        results.forEach { detection ->
            Log.d("Detection", "Letra: ${detection.className}, Confiança: ${detection.confidence}")
        }

        Handler(Looper.getMainLooper()).post {
            onResults(results)
        }
    } catch (e: Exception) {
        Log.e("YOLO", "Erro no processamento: ${e.message}")
    } finally {
        imageProxy.close()
        isProcessingFrame.set(false)
    }
}

@OptIn(ExperimentalGetImage::class)
private fun ImageProxy.toYOLOInputTensor(): TensorImage {
    val image = this.image ?: throw IllegalStateException("Imagem inválida")
    if (image.format != ImageFormat.YUV_420_888) {
        throw IllegalArgumentException("Formato de imagem não suportado")
    }
    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer

    val inputBuffer = ByteBuffer.allocateDirect(640 * 640 * 3 * 4).apply {
        order(ByteOrder.nativeOrder())
        rewind()
    }
    val yRowStride = image.planes[0].rowStride
    val uvRowStride = image.planes[1].rowStride
    val uvPixelStride = image.planes[1].pixelStride

    // Converte pixel a pixel de YUV para RGB e normaliza para [0,1]
    for (y in 0 until 640) {
        for (x in 0 until 640) {
            val yIndex = y * yRowStride + x
            val yValue = (yBuffer.get(yIndex).toInt() and 0xFF).toFloat()

            val uvIndex = (y / 2) * uvRowStride + (x / 2) * uvPixelStride
            val uValue = (uBuffer.get(uvIndex).toInt() and 0xFF).toFloat() - 128
            val vValue = (vBuffer.get(uvIndex).toInt() and 0xFF).toFloat() - 128

            val r = (yValue + 1.402f * vValue).coerceIn(0f, 255f) / 255.0f
            val g = (yValue - 0.34414f * uValue - 0.71414f * vValue).coerceIn(0f, 255f) / 255.0f
            val b = (yValue + 1.772f * uValue).coerceIn(0f, 255f) / 255.0f

            inputBuffer.putFloat(r)
            inputBuffer.putFloat(g)
            inputBuffer.putFloat(b)
        }
    }
    val tensorBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 640, 640, 3), DataType.FLOAT32)
    inputBuffer.rewind()
    tensorBuffer.loadBuffer(inputBuffer)
    return TensorImage(DataType.FLOAT32).apply {
        load(tensorBuffer)
    }
}

// --- Processamento do tensor de saída para extrair múltiplas detecções com NMS ---
private fun processYOLOOutput(
    outputBuffer: ByteBuffer,
    classNames: Map<Int, String>
): List<DetectionResult> {
    outputBuffer.rewind()
    val detections = mutableListOf<DetectionResult>()
    val detectionSize = 27  // 4 (bounding box) + 1 (objectness) + 22 (classes)
    val floatSize = 4
    val numDetections = outputBuffer.capacity() / (detectionSize * floatSize)

    for (i in 0 until numDetections) {
        val offset = i * detectionSize * floatSize
        val objConfidence = outputBuffer.getFloat(offset + 4 * floatSize)
        var bestClassScore = 0f
        var classId = -1
        for (j in 5 until detectionSize) {
            val classScore = outputBuffer.getFloat(offset + j * floatSize)
            if (classScore > bestClassScore) {
                bestClassScore = classScore
                classId = j - 5
            }
        }
        val finalConfidence = objConfidence * bestClassScore
        if (finalConfidence > 0.7f && classId != -1) { // Threshold exigindo maior confiança
            val centerX = outputBuffer.getFloat(offset + 0 * floatSize)
            val centerY = outputBuffer.getFloat(offset + 1 * floatSize)
            val boxWidth = outputBuffer.getFloat(offset + 2 * floatSize)
            val boxHeight = outputBuffer.getFloat(offset + 3 * floatSize)
            val left = centerX - boxWidth / 2
            val top = centerY - boxHeight / 2
            val right = centerX + boxWidth / 2
            val bottom = centerY + boxHeight / 2

            detections.add(
                DetectionResult(
                    className = classNames[classId] ?: "Desconhecido",
                    confidence = finalConfidence,
                    boundingBox = BoundingBox(left, top, right, bottom)
                )
            )
        }
    }
    // Aplica Non-Maximum Suppression para reduzir detecções duplicadas
    return nonMaximumSuppression(detections, iouThreshold = 0.5f)
}

// Função para calcular Intersection over Union (IoU) entre duas bounding boxes
private fun iou(a: BoundingBox, b: BoundingBox): Float {
    val interLeft = max(a.left, b.left)
    val interTop = max(a.top, b.top)
    val interRight = min(a.right, b.right)
    val interBottom = min(a.bottom, b.bottom)
    val interArea = max(0f, interRight - interLeft) * max(0f, interBottom - interTop)
    val areaA = (a.right - a.left) * (a.bottom - a.top)
    val areaB = (b.right - b.left) * (b.bottom - b.top)
    return interArea / (areaA + areaB - interArea)
}

// Função de Non-Maximum Suppression
private fun nonMaximumSuppression(
    detections: List<DetectionResult>,
    iouThreshold: Float
): List<DetectionResult> {
    val sortedDetections = detections.sortedByDescending { it.confidence }
    val selected = mutableListOf<DetectionResult>()
    val active = BooleanArray(sortedDetections.size) { true }

    for (i in sortedDetections.indices) {
        if (!active[i]) continue
        val current = sortedDetections[i]
        selected.add(current)
        for (j in i + 1 until sortedDetections.size) {
            if (active[j] && iou(current.boundingBox, sortedDetections[j].boundingBox) > iouThreshold) {
                active[j] = false
            }
        }
    }
    return selected
}

// --- Classe que gerencia o modelo TFLite com GPU Delegate ---
class YOLOModel(context: Context) {
    val classNames: Map<Int, String>
    private val interpreter: Interpreter
    private val inputShape: IntArray

    init {
        val assetManager = context.assets
        val modelFile = assetManager.open("modelo_libras.tflite")
        val modelBytes = modelFile.readBytes()
        val modelBuffer = ByteBuffer.allocateDirect(modelBytes.size)
            .order(ByteOrder.nativeOrder())
            .apply {
                put(modelBytes)
                rewind()
            }
        val metadata = MetadataExtractor(modelBuffer)
        classNames = loadClassNames(metadata)

        // Configuração do Interpreter com GPU Delegate (se disponível)
        val options = Interpreter.Options()
        try {
            val gpuDelegate = GpuDelegate()
            options.addDelegate(gpuDelegate)
            Log.d("YOLOModel", "GPU Delegate adicionado com sucesso.")
        } catch (e: Exception) {
            Log.e("YOLOModel", "GPU Delegate não disponível, utilizando CPU.")
        }
        interpreter = Interpreter(modelBuffer, options)
        inputShape = interpreter.getInputTensor(0).shape()
    }

    private fun loadClassNames(metadata: MetadataExtractor): Map<Int, String> {
        return mapOf(
            0 to "A", 1 to "B", 2 to "C", 3 to "D1", 4 to "D2",
            5 to "E", 6 to "F", 7 to "G", 8 to "I", 9 to "K",
            10 to "L", 11 to "M", 12 to "N", 13 to "O", 14 to "P",
            15 to "Q", 16 to "R", 17 to "S", 18 to "T", 19 to "U",
            20 to "V", 21 to "W"
        )
    }

    fun createOutputBuffer() = ByteBuffer.allocateDirect(
        interpreter.getOutputTensor(0).numBytes()
    ).apply {
        order(ByteOrder.nativeOrder())
        rewind()
    }

    fun predict(input: TensorImage, output: ByteBuffer) {
        interpreter.run(input.buffer, output)
    }

    fun close() {
        interpreter.close()
    }
}
