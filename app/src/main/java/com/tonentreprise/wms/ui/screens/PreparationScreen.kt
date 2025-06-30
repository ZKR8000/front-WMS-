package com.tonentreprise.wms.ui.screens

import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tonentreprise.wms.model.SalesOrderLightDTO
import com.tonentreprise.wms.network.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreparationScreen(navController: NavHostController) {
    val commandes = remember { mutableStateListOf<SalesOrderLightDTO>() }
    val expandedState = remember { mutableStateMapOf<Long, Boolean>() }
    val context = LocalContext.current
    val apiService = RetrofitClient.getInstance(context)

    // Appel API dans LaunchedEffect
    LaunchedEffect(Unit) {
        try {
            val result = apiService.getAllSalesOrdersLight()
            Log.d("API", "Réponse des commandes: $result")  // Affiche la réponse dans les logs
            commandes.clear()
            commandes.addAll(result)
        } catch (e: Exception) {
            Log.e("API", "Erreur lors du chargement : ${e.message}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Bon de Commandes", fontWeight = FontWeight.Bold, color = Color.White)
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
        // Vérifie si la liste de commandes est vide
        if (commandes.isEmpty()) {
            Text("Aucune commande à afficher", color = Color.Red, modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(commandes) { commande ->
                    CommandeCard(navController, commande, expandedState)
                }
            }
        }
    }
}

@Composable
fun CommandeCard(
    navController: NavHostController,
    commande: SalesOrderLightDTO,
    expandedState: MutableMap<Long, Boolean>
) {
    val expanded = expandedState[commande.id] ?: false
    val produits = commande.salesOrderDetails.map { it.productName }

    // Vérifie si les données sont valides avant d'afficher
    if (commande.sohNum.isNullOrEmpty()) {
        Log.e("Commande", "Commande sans numéro !")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandedState[commande.id] = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    // Affichage avec vérification des valeurs nulles
                    Text("Commande: ${commande.sohNum ?: "Non renseigné"}", fontWeight = FontWeight.Bold)
                    Text("Client: ${commande.clientName ?: "Non renseigné"}", style = MaterialTheme.typography.bodySmall)
                    Text("Statut: ${commande.status ?: "Non renseigné"}", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = { expandedState[commande.id] = !expanded }) {
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
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(produit, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
