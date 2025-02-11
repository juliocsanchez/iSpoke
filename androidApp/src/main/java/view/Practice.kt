package view

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.ispoke.android.controller.Detection
import com.example.ispoke.android.controller.LandmarkImageAnalyzer
import com.example.ispoke.android.controller.TFLiteLandmarkDetector
import com.example.ispoke.android.localComposables.CameraPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun Practice(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var detections by remember { mutableStateOf(emptyList<Detection>()) }

    val detector = remember { TFLiteLandmarkDetector(context) }
    val analyzer = remember {
        LandmarkImageAnalyzer(detector) { results ->
            detections = results
        }
    }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                analyzer
            )
        }
    }

    LaunchedEffect(cameraController) {
        val cameraProvider = withContext(Dispatchers.IO) {
            ProcessCameraProvider.getInstance(context).get()
        }
        cameraController.bindToLifecycle(lifecycleOwner)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(controller = cameraController, modifier = Modifier.fillMaxSize())

        // Se houver detecções, seleciona a de maior confiança e exibe na tela
        if (detections.isNotEmpty()) {
            val detection = detections.maxByOrNull { it.confidence }
            detection?.let {
                // Registra a letra e a confiança no logcat
                Log.d("Detection", "Letra detectada: ${it.label} com confiança ${it.confidence}")
                BasicText(
                    text = "Letra detectada: ${it.label}",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
