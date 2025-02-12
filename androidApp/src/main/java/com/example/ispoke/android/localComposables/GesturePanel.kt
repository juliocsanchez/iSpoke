package com.example.ispoke.android.localComposables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ispoke.android.R
import com.example.ispoke.android.classes.ModuleItem

@Composable
fun GesturePanel(
    modifier: Modifier = Modifier,
    modules: List<ModuleItem>,
    navController: NavController,
    gestureCheck: Int
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ok),
                    contentDescription = "Imagem Principal",
                    modifier = Modifier.size(90.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = gestureCheck.toString(), // Exibe o contador atualizado
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 80.sp
                    )
                    Text(
                        text = "Gestos aprendidos",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 20.sp
                    )
                }
            }
        }
        ModuleGrid(modules, navController)
    }
}
