package com.example.ispoke.android.localComposables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginButton(navController: NavController){
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = { navController.navigate("home")},
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                "Entrar",
                modifier = Modifier.padding(horizontal = 24.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        OutlinedButton(
            onClick = { navController.navigate("register")},
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Cadastrar",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}