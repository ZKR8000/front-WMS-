package com.tonentreprise.wms.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun ReceptionScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Réception de Stock") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Bienvenue sur l'écran Réception de Stock")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReceptionScreenPreview() {
    ReceptionScreen(navController = rememberNavController())
}
