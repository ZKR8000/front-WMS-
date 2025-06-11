package com.tonentreprise.wms.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceptionScreen(navController: NavHostController) {
    val receptionsEnAttente = listOf("2001", "2003")
    val receptionsValidees = listOf("2002", "2004")

    val expandedState = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Réceptions de Stock",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium // taille réduite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00897B))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            SectionTitleReception("Réceptions en attente", Color(0xFF1E88E5))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(receptionsEnAttente) { reception ->
                    ReceptionCard(navController, reception, expandedState, Color(0xFF1E88E5))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitleReception("Réceptions validées", Color(0xFF388E3C))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(receptionsValidees) { reception ->
                    ReceptionCard(navController, reception, expandedState, Color(0xFF388E3C))
                }
            }
        }
    }
}

@Composable
fun SectionTitleReception(title: String, color: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

val receptionsFournisseurs = mapOf(
    "2001" to "SUPPLIER A",
    "2002" to "SUPPLIER B",
    "2003" to "SUPPLIER C",
    "2004" to "SUPPLIER D"
)

val receptionsDates = mapOf(
    "2001" to "10/06/2025",
    "2002" to "08/06/2025",
    "2003" to "09/06/2025",
    "2004" to "07/06/2025"
)

val receptionsStatuts = mapOf(
    "2001" to "En attente",
    "2002" to "Validée",
    "2003" to "En attente",
    "2004" to "Validée"
)

val receptionsProduits: Map<String, List<String>> = mapOf(
    "2001" to listOf("Produit AA (Qté : 50, Lot : L123)", "Produit AB (Qté : 30, Lot : L124)"),
    "2002" to listOf("Produit AC (Qté : 40, Lot : L125)"),
    "2003" to listOf("Produit AD (Qté : 20, Lot : L126)", "Produit AE (Qté : 60, Lot : L127)"),
    "2004" to listOf("Produit AF (Qté : 90, Lot : L128)")
)

@Composable
fun ReceptionCard(
    navController: NavHostController,
    reception: String,
    expandedState: MutableMap<String, Boolean>,
    color: Color
) {
    val expanded = expandedState[reception] ?: false
    val produits = receptionsProduits[reception] ?: emptyList()
    val fournisseur = receptionsFournisseurs[reception] ?: "Fournisseur inconnu"
    val date = receptionsDates[reception] ?: "Date inconnue"
    val statut = receptionsStatuts[reception] ?: "Statut inconnu"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable { expandedState[reception] = !expanded },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Réception #$reception", fontWeight = FontWeight.Bold)
                    Text("Fournisseur : $fournisseur", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    Text("Date : $date", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    Text("Statut : $statut", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                }
                IconButton(onClick = { expandedState[reception] = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Afficher produits"
                    )
                }
            }

            if (expanded) {
                produits.forEach { produit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable {
                                navController.navigate("reception_details/$reception")
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = produit,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
