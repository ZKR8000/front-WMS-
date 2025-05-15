package com.tonentreprise.wms.ui.screens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreparationScreen(navController: NavHostController) {
    val commandesParQuantite = listOf("1001", "1003")
    val commandesParUnite = listOf("1002", "1004")

    // ✅ Gestion de l'affichage des produits
    val expandedState = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Bon de Commandes", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF6200EE))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            SectionTitle("Commandes par Quantité", Color(0xFF1E88E5))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(commandesParQuantite) { commande ->
                    CommandeCard(navController, commande, expandedState, Color(0xFF1E88E5))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Commandes par Unité", Color(0xFFD32F2F))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(commandesParUnite) { commande ->
                    CommandeCard(navController, commande, expandedState, Color(0xFFD32F2F))
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, color: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

val commandesProduits: Map<String, List<String>> = mapOf(
    "1001" to listOf("Produit A", "Produit B", "Produit C"),
    "1002" to listOf("Produit X", "Produit Y"),
    "1003" to listOf("Produit M", "Produit N", "Produit O", "Produit P"),
    "1004" to listOf("Produit Q")
)

val commandesClients = mapOf(
    "1001" to "SABRINA SUPER MARK",
    "1002" to "OLVO LOUVMANE AG",
    "1003" to "BRICO INVEST AGADIR",
    "1004" to "STE ANT NEGOCE S.A"
)

val commandesStatuts = mapOf(
    "1001" to "Affectée",
    "1002" to "Affectée",
    "1003" to "Affectée",
    "1004" to "Non affectée"
)

@Composable
fun CommandeCard(navController: NavHostController, commande: String, expandedState: MutableMap<String, Boolean>, color: Color) {
    val expanded = expandedState[commande] ?: false
    val produits = commandesProduits[commande] ?: emptyList()
    val client = commandesClients[commande] ?: "Client inconnu"
    val statut = commandesStatuts[commande] ?: "Inconnu"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable { expandedState[commande] = !expanded },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Commande #$commande", fontWeight = FontWeight.Bold)
                    Text("Client : $client", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    Text("Statut : $statut", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                }
                IconButton(onClick = { expandedState[commande] = !expanded }) {
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
                                navController.navigate("commande_details/$commande")
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
