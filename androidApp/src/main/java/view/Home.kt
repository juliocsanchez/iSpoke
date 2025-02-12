package view

import SharedViewModel
import TopBar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.ispoke.android.classes.GestureCheck
import com.example.ispoke.android.localComposables.BottomBar
import com.example.ispoke.android.localComposables.GesturePanel
import modules

@Composable
fun Home(navController: NavHostController, sharedViewModel: SharedViewModel, gestureCheck: GestureCheck) {
    LaunchedEffect(Unit) {
        sharedViewModel.modules = modules
    }
    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) }

    ) { paddingValues ->
        GesturePanel(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            modules = modules,
            navController = navController,
            gestureCheck = gestureCheck.gestureCount
        )
    }
}