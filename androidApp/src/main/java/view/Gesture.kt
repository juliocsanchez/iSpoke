package view


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import camPermission
import com.example.ispoke.android.localComposables.ImageGesture
import com.example.ispoke.android.localComposables.TopBarGesture


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Gesture(navController: NavController, letter: String,imageResId : Int, title: String) {
    val context = LocalContext.current
    val permission = camPermission(navController)
    Scaffold(
        topBar = { TopBarGesture(navController,letter,imageResId) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()){
            ImageGesture(letter)
            FloatingActionButton(
                onClick = {
                    when {
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            try {
                                navController.navigate("practice")
                            } catch (e: Exception) {
                                Log.e("Navigation", "Erro na navegação", e)
                            }
                        }
                        else -> permission.launch(android.Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = "Abrir câmera",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}
