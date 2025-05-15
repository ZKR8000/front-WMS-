package com.tonentreprise.wms.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockSuiviScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    var emplacementFilter by remember { mutableStateOf("") }

    val produits = listOf(
        ProduitStock("Produit A", "Type A", 100, "Rayon 1", "LOT-A123"),
        ProduitStock("Produit B", "Type B", 50, "Palette 2", "LOT-B456"),
        ProduitStock("Produit C", "Type C", 75, "Réserve 3", null),
        ProduitStock("Produit D", "Type A", 20, "Entrepôt 4", null)
    )

    val produitsFiltres = produits.filter {
        (it.nom.contains(searchQuery, ignoreCase = true) ||
                it.lot?.contains(searchQuery, ignoreCase = true) == true)
                && it.emplacement.contains(emplacementFilter, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Suivi des Stocks", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { /* Scanner */ }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Scan")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFD0A5F7))
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Rechercher par nom ou lot") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = emplacementFilter,
                onValueChange = { emplacementFilter = it },
                label = { Text("Filtrer par emplacement") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(produitsFiltres) { produit ->
                    StockCard(produit)
                }
            }
        }
    }
}

@Composable
fun StockCard(produit: ProduitStock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3E3C43).copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Produit : ${produit.nom}", fontWeight = FontWeight.Bold)
            Text("Type : ${produit.type}")
            Text("Quantité : ${produit.quantite}")
            Text("Emplacement : ${produit.emplacement}")
            produit.lot?.let {
                Text("Lot : $it")
            }
        }
    }
}

// Data class modélisant un produit en stock
data class ProduitStock(
    val nom: String,
    val type: String,
    val quantite: Int,
    val emplacement: String,
    val lot: String?
)
