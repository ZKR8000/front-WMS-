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
fun TransfertScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Transfert des stocks") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Écran de transfert", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ Ajout d'un bouton pour revenir au tableau de bord
            Button(
                onClick = { navController.navigate("dashboard") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Retour au Tableau de bord")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransfertScreenPreview() {
    TransfertScreen(navController = rememberNavController())
}
