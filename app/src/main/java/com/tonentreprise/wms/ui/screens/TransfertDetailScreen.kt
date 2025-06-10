package com.tonentreprise.wms.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransfertDetailScreen(navController: NavHostController, transfertId: String) {
    val origine = transfertsOrigine[transfertId] ?: "Origine inconnue"
    val destination = transfertsDestination[transfertId] ?: "Destination inconnue"
    val date = transfertsDates[transfertId] ?: "Date inconnue"
    val statut = transfertsStatuts[transfertId] ?: "Statut inconnu"
    val produits = transfertsProduits[transfertId] ?: emptyList()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Transfert #$transfertId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF1E88E5))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Origine : $origine", style = MaterialTheme.typography.bodyLarge)
            Text("Destination : $destination", style = MaterialTheme.typography.bodyLarge)
            Text("Date : $date", style = MaterialTheme.typography.bodyLarge)
            Text(
                "Statut : $statut",
                style = MaterialTheme.typography.bodyLarge,
                color = if (statut == "Terminé") Color(0xFF388E3C) else Color(0xFF1E88E5)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Produits transférés :", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            produits.forEach { produit ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            produit,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
