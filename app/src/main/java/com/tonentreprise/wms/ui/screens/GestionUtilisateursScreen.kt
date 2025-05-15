package com.tonentreprise.wms.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUtilisateursScreen(navController: NavHostController) {
    var utilisateurs by remember { mutableStateOf(sampleUsers) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Gestion des Utilisateurs",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("ajout_utilisateur") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Ajouter un utilisateur")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (utilisateurs.isEmpty()) {
                Text(
                    "Aucun utilisateur disponible.",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(utilisateurs) { utilisateur ->
                        UserItem(
                            utilisateur = utilisateur,
                            navController = navController,
                            onDelete = { utilisateurs = utilisateurs - utilisateur }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(utilisateur: User, navController: NavHostController, onDelete: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = utilisateur.nom, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = utilisateur.email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text(text = "Rôle : ${utilisateur.role}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { navController.navigate("modifier_utilisateur/${utilisateur.email}") }) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier")
            }
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Voulez-vous vraiment supprimer cet utilisateur ?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDelete()
                }) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

// ✅ Exemple de données utilisateur
data class User(val nom: String, val email: String, val role: String)
val sampleUsers = listOf(
    User("Alice Dupont", "alice@example.com", "Admin"),
    User("Bob Martin", "bob@example.com", "Utilisateur"),
    User("Charlie Durand", "charlie@example.com", "Superviseur")
)

@Preview(showBackground = true)
@Composable
fun PreviewGestionUtilisateursScreen() {
    GestionUtilisateursScreen(navController = rememberNavController())
}
