package view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.ispoke.android.localComposables.BottomBar
import com.example.ispoke.android.localComposables.ImageProfile
import com.example.ispoke.android.localComposables.SettingsProfile

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Profile(navController: NavController) {
    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageProfile()
                SettingsProfile(navController)
            }
        }
    }
}

