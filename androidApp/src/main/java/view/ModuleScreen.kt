package view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ispoke.android.localComposables.LetterItem
import com.example.ispoke.android.localComposables.TopBarModule
import com.example.ispoke.android.utils.onCLickModule


@Composable
fun ModuleScreen(navController: NavController, title: String, imageResId: Int) {
    val letters = ('A'..'Z').toList()

    Scaffold(topBar = {
        TopBarModule(navController,title,imageResId) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(letters) { letter ->
                LetterItem(
                    letter = letter.toString(),
                    onClick = { onCLickModule(navController, letter, imageResId, title ) }
                )
            }
        }
    }
}



