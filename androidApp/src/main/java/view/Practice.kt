import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

fun showColoredToast(context: android.content.Context, message: String, backgroundColor: Int, textColor: Int) {
    val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
    toast.view?.let { view ->
        view.setBackgroundColor(backgroundColor)
        if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                if (child is TextView) {
                    child.setTextColor(textColor)
                }
            }
        }
    }
    toast.show()
}

@Composable
fun Practice(navController: NavController, letter: String) {
    val context = LocalContext.current
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Launcher para solicitar a permissão da câmera
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            showColoredToast(context, "Permissão para câmera negada", Color.Red.toArgb(), Color.White.toArgb())
        }
    }

    // Verifica e solicita permissão na inicialização
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Launcher para capturar a foto via câmera (retorna um Bitmap)
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedImage = bitmap
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Exibir a imagem capturada (se houver) ou uma mensagem padrão
        capturedImage?.let { image ->
            Image(
                bitmap = image.asImageBitmap(),
                contentDescription = "Foto Capturada",
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp)
            )
        } ?: Text(text = "Nenhuma foto capturada", modifier = Modifier.padding(16.dp))

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botão "Voltar" com bordas arredondadas e fundo vermelho com letras brancas
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Voltar", color = Color.White)
            }

            // Botão "Tirar Foto" que abre a câmera
            Button(
                onClick = { launcher.launch() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Tirar Foto", color = Color.White)
            }

            // Botão "Enviar" com fundo verde e letras brancas, habilitado somente se houver foto
            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        capturedImage?.let { bitmap ->
                            // Converter o Bitmap para um array de bytes (formato JPEG)
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            val byteArray = stream.toByteArray()

                            // Preparar a requisição multipart para enviar imagem e a letra
                            val client = OkHttpClient()
                            val requestBody = MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("letra", letter)
                                .addFormDataPart(
                                    "file",
                                    "photo.png",
                                    byteArray.toRequestBody("image/png".toMediaTypeOrNull())
                                )
                                .build()

                            print(letter)

                            val request = Request.Builder()
                                .url("http://192.168.0.8:8000/detect")
                                .post(requestBody)
                                .build()

                            try {
                                val response = client.newCall(request).execute()
                                if (response.isSuccessful) {
                                    val responseString = response.body?.string()
                                    val jsonObj = JSONObject(responseString)
                                    val detectionFound = jsonObj.getBoolean("detection_found")
                                    withContext(Dispatchers.Main) {
                                        if (detectionFound) {
                                            showColoredToast(
                                                context,
                                                "Detecção correta!",
                                                Color.Green.toArgb(),
                                                Color.White.toArgb()
                                            )
                                            navController.navigate("home") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        } else {
                                            showColoredToast(
                                                context,
                                                "Detecção incorreta!",
                                                Color.Red.toArgb(),
                                                Color.White.toArgb()
                                            )
                                        }
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        showColoredToast(
                                            context,
                                            "Erro no envio da imagem!",
                                            Color.Red.toArgb(),
                                            Color.White.toArgb()
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    showColoredToast(
                                        context,
                                        "Erro: ${e.message}",
                                        Color.Red.toArgb(),
                                        Color.White.toArgb()
                                    )
                                }
                            }
                        }
                    }
                },
                enabled = capturedImage != null,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Enviar", color = Color.White)
            }
        }
    }
}
