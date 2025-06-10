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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransfertScreen(navController: NavHostController) {
    val transfertsEnCours = listOf("TR001", "TR003")
    val transfertsTermines = listOf("TR002", "TR004")

    val expandedState = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Transfert des stocks", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF1E88E5))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            SectionTitleTransfert("Transferts en cours", Color(0xFF1E88E5))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(transfertsEnCours) { transfert ->
                    TransfertCard(navController, transfert, expandedState, Color(0xFF1E88E5))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitleTransfert("Transferts terminés", Color(0xFF388E3C))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(transfertsTermines) { transfert ->
                    TransfertCard(navController, transfert, expandedState, Color(0xFF388E3C))
                }
            }
        }
    }
}

@Composable
fun SectionTitleTransfert(title: String, color: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun TransfertCard(
    navController: NavHostController,
    transfert: String,
    expandedState: MutableMap<String, Boolean>,
    color: Color
) {
    val expanded = expandedState[transfert] ?: false
    val produits = transfertsProduits[transfert] ?: emptyList()
    val origine = transfertsOrigine[transfert] ?: "Origine inconnue"
    val destination = transfertsDestination[transfert] ?: "Destination inconnue"
    val date = transfertsDates[transfert] ?: "Date inconnue"
    val statut = transfertsStatuts[transfert] ?: "Statut inconnu"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable { expandedState[transfert] = !expanded },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Transfert #$transfert", fontWeight = FontWeight.Bold)
                    Text("Origine : $origine", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    Text("Destination : $destination", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    Text("Date : $date", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    Text("Statut : $statut", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                }
                IconButton(onClick = { expandedState[transfert] = !expanded }) {
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
                                navController.navigate("transfert_details/$transfert")
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
