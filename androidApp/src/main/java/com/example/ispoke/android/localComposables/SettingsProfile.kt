package com.example.ispoke.android.localComposables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsProfile( navController: NavController){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        InfoItem(
            icon = Icons.Default.Person,
            label = "Nome",
            value = "John Doe"
        )
        InfoItem(
            icon = Icons.Default.Star,
            label = "Nível",
            value = "1"
        )
        InfoItem(
            icon = Icons.Default.ExitToApp,
            label = "Conta",
            value = "Sair",
            onClick = { navController.navigate("login") }
        )
    }
}