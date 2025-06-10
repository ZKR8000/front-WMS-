package com.tonentreprise.wms.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceptionDetailScreen(navController: NavHostController, receptionId: String) {
    // Utilise les mêmes data maps que dans ta liste
    val fournisseur = receptionsFournisseurs[receptionId] ?: "Fournisseur inconnu"
    val date = receptionsDates[receptionId] ?: "Date inconnue"
    val statut = receptionsStatuts[receptionId] ?: "Statut inconnu"
    val produits = receptionsProduits[receptionId] ?: emptyList()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Réception #$receptionId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF00897B))
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
            Text("Fournisseur : $fournisseur", style = MaterialTheme.typography.bodyLarge)
            Text("Date : $date", style = MaterialTheme.typography.bodyLarge)
            Text("Statut : $statut", style = MaterialTheme.typography.bodyLarge, color = if (statut == "Validée") Color(0xFF388E3C) else Color(0xFF1E88E5))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Produits réceptionnés :", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
