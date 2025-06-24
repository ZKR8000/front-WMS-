package com.tonentreprise.wms.ui.screens

/* ------------------------- IMPORTS EXPLICITES ------------------------- */
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tonentreprise.wms.data.User
import com.tonentreprise.wms.data.sampleUsers     // <-- provient du package data

/* ----------------------------- UI SCREEN ----------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUtilisateursScreen(navController: NavHostController) {
    var users by remember { mutableStateOf(sampleUsers) }   // <- utilise ceux du data package

    val lavender = Color(0xFFD0BFFF)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(48.dp),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    Text(
                        "Gestion des Utilisateurs",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = lavender
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("ajout_utilisateur") },
                containerColor = lavender
            ) { Icon(Icons.Default.PersonAdd, contentDescription = "Ajouter un utilisateur") }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (users.isEmpty()) {
                Text(
                    "Aucun utilisateur disponible.",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(users) { u ->
                        UserCard(
                            user = u,
                            onEdit   = { navController.navigate("modifier_utilisateur/${u.email}") },
                            onDelete = { users = users - u }
                        )
                    }
                }
            }
        }
    }
}

/* --------------------------- USER CARD --------------------------- */
@Composable
private fun UserCard(
    user: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(user.nom, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(user.email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(Modifier.height(4.dp))
                AssistChip(
                    onClick = {},
                    label = { Text(user.role) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFF35334D),
                        labelColor = Color.White
                    )
                )
            }
            IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, contentDescription = "Modifier") }
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            icon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Supprimer l'utilisateur ?") },
            text = { Text("Cette action est irréversible.") },
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false; onDelete() }) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Annuler") }
            }
        )
    }
}

/* --------------------------- PREVIEW --------------------------- */
@Preview(showBackground = true)
@Composable
fun PreviewGestionUtilisateurs() {
    GestionUtilisateursScreen(rememberNavController())
}
