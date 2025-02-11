package com.example.ispoke.android.localComposables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ispoke.android.utils.getDrawableID

@SuppressLint("SuspiciousIndentation")
@Composable
fun ImageGesture(letter: String) {
    val letterToLowerCase = letter.lowercase()
    val context = LocalContext.current
    val drawableId = getDrawableID(context, letterToLowerCase)

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .size(300.dp)
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(25.dp),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (drawableId != 0) {
                                Icon(
                                    painter = painterResource(drawableId),
                                    contentDescription = "Letra $letter",
                                    modifier = Modifier
                                        .size(250.dp)
                                        .padding(5.dp),
                                    tint = Color.Unspecified
                                )
                            } else {
                                Text("Imagem não encontrada", color = Color.Red)
                            }
                        }
                    }
                    Text(
                        text = "Letra $letter",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(5.dp)
                    )
                }

            }
        }
}

