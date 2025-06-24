package com.tonentreprise.wms.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tonentreprise.wms.model.Emplacement
import com.tonentreprise.wms.data.sampleEmplacements

@OptIn(ExperimentalMaterial3Api::class)  // Ajout de l'annotation ici

@Composable
fun ListeEmplacementsScreen() {
    var emplacements by remember { mutableStateOf(sampleEmplacements) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Liste des Emplacements") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(emplacements) { emp ->
                EmplacementCard(
                    emplacement = emp,
                    onEdit = { /* À implémenter */ },
                    onDelete = { emplacements = emplacements - emp }
                )
            }
        }
    }
}

@Composable
fun EmplacementCard(
    emplacement: Emplacement,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Code : ${emplacement.code}", fontWeight = FontWeight.Bold)
            Text("Type : ${emplacement.type}")
            Text("Capacité : ${emplacement.capaciteOccupe}/${emplacement.capaciteMax}")
            Text("Catégorie : ${emplacement.categorieProd}")
            Text("Statut : ${if (emplacement.statut) "Disponible" else "Occupé"}")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifier")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
