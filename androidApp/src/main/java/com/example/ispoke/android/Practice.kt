    import androidx.compose.foundation.BorderStroke
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.Card
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.unit.dp
    import androidx.navigation.NavController
    import android.util.Log
    import androidx.camera.core.CameraSelector
    import androidx.camera.core.Preview
    import androidx.camera.lifecycle.ProcessCameraProvider
    import androidx.camera.view.PreviewView
    import androidx.compose.runtime.*
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.platform.LocalLifecycleOwner
    import androidx.compose.ui.viewinterop.AndroidView
    import androidx.core.content.ContextCompat
    import java.util.concurrent.ExecutorService
    import java.util.concurrent.Executors

    @Composable
    fun Practice(navController: NavController) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        var cameraExecutor by remember { mutableStateOf<ExecutorService?>(null) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(4.dp, Color(0xFF6A1B9A))
            ) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            layoutParams = android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            )

                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                try {
                                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                                    // Configurar preview
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(surfaceProvider)
                                    }

                                    // Selecionar câmera traseira
                                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                                    // Vincular ao ciclo de vida
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview
                                    )

                                } catch (e: Exception) {
                                    Log.e("CameraError", "Erro na configuração da câmera", e)
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        DisposableEffect(Unit) {
            cameraExecutor = Executors.newSingleThreadExecutor()
            onDispose {
                cameraExecutor?.shutdown()
            }
        }
    }