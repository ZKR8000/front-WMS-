package com.tonentreprise.wms.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack // Icône ArrowBack classique
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tonentreprise.wms.model.SalesOrderLightDTO
import com.tonentreprise.wms.network.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreparationScreen(navController: NavHostController) {
    val commandes = remember { mutableStateListOf<SalesOrderLightDTO>() }
    val expandedState = remember { mutableStateMapOf<String, Boolean>() }

    // API Call pour charger les commandes
    val context = LocalContext.current
    val apiService = RetrofitClient.getInstance(context)

    LaunchedEffect(Unit) {
        try {
            val result = apiService.getAllSalesOrdersLight() // Appel API pour récupérer les commandes
            Log.d("API", "Réponse des commandes: $result")
            commandes.clear()
            commandes.addAll(result) // Ajouter les commandes récupérées
        } catch (e: Exception) {
            Log.e("API", "Erreur lors du chargement : ${e.message}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Bon de Commandes",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            SectionTitle("Commandes en attente", Color(0xFF1E88E5))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(commandes.filter { it.status == "EN_ATTENTE" }) { commande ->
                    CommandeCard(navController, commande, expandedState, Color(0xFF1E88E5))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Commandes validées", Color(0xFF388E3C))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(commandes.filter { it.status == "VALIDEE" }) { commande ->
                    CommandeCard(navController, commande, expandedState, Color(0xFF388E3C))
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

@Composable
fun CommandeCard(
    navController: NavHostController,
    commande: SalesOrderLightDTO,
    expandedState: MutableMap<String, Boolean>,
    color: Color
) {
    val expanded = expandedState[commande.sohNum] == true // Vérification booléenne
    val client = commande.clientName // Utilisation de données dynamiques du client
    val statut = commande.status // Utilisation de données dynamiques du statut
    val produits = commande.salesOrderDetails.map { it.productName } // Récupération des produits dynamiquement

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandedState[commande.sohNum] = !expanded }
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Commande #${commande.sohNum}", fontWeight = FontWeight.Bold)
                    Text("Client : $client", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    Text("Statut : $statut", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                }
                IconButton(onClick = { expandedState[commande.sohNum] = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Afficher/Masquer les produits"
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
                                navController.navigate("commande_details/${commande.sohNum}") // Lien vers les détails
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = produit ?: "Valeur par défaut", // Si 'produit' est null, il affichera "Valeur par défaut"
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

