package com.tonentreprise.wms.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tonentreprise.wms.model.Emplacement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifierEmplacementScreen(
    navController: NavHostController,
    emplacement: Emplacement, // On reçoit l'emplacement à modifier
    onUpdateEmplacement: (Emplacement) -> Unit // Fonction de callback pour la mise à jour
) {
    // On initialise les champs avec les données actuelles
    var code by remember { mutableStateOf(emplacement.code) }
    var type by remember { mutableStateOf(emplacement.type) }
    var capaciteMax by remember { mutableStateOf(emplacement.capaciteMax) }
    var capaciteOccupe by remember { mutableStateOf(emplacement.capaciteOccupe) }
    var categorieProd by remember { mutableStateOf(emplacement.categorieProd) }
    var statut by remember { mutableStateOf(emplacement.statut) } // Boolean pour statut

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modifier Emplacement", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Modifier les informations de l'emplacement", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            // Champ pour le code de l'emplacement
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Code Emplacement") },
                modifier = Modifier.fillMaxWidth()
            )

            // Champ pour le type de l'emplacement
            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Type d'Emplacement") },
                modifier = Modifier.fillMaxWidth()
            )

            // Champ pour la capacité maximale
            OutlinedTextField(
                value = capaciteMax.toString(),
                onValueChange = { capaciteMax = it.toIntOrNull() ?: capaciteMax },
                label = { Text("Capacité Maximale") },
                modifier = Modifier.fillMaxWidth()
            )

            // Champ pour la capacité occupée
            OutlinedTextField(
                value = capaciteOccupe.toString(),
                onValueChange = { capaciteOccupe = it.toIntOrNull() ?: capaciteOccupe },
                label = { Text("Capacité Occupée") },
                modifier = Modifier.fillMaxWidth()
            )

            // Champ pour la catégorie de produits acceptés
            OutlinedTextField(
                value = categorieProd,
                onValueChange = { categorieProd = it },
                label = { Text("Catégorie de Produits Acceptés") },
                modifier = Modifier.fillMaxWidth()
            )

            // Sélection du statut : "Disponible" ou "Occupé"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Statut")
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = statut,
                        onClick = { statut = true } // Disponible = true
                    )
                    Text("Disponible")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = !statut,
                        onClick = { statut = false } // Occupé = false
                    )
                    Text("Occupé")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bouton pour enregistrer les modifications
            Button(
                onClick = {
                    val updatedEmplacement = Emplacement(
                        code = code,
                        type = type,
                        capaciteMax = capaciteMax,
                        capaciteOccupe = capaciteOccupe,
                        categorieProd = categorieProd,
                        statut = statut // Utilisation du Boolean
                    )
                    onUpdateEmplacement(updatedEmplacement)
                    navController.popBackStack()  // Retourner à la liste après modification
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Check, contentDescription = "Enregistrer")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enregistrer les modifications")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewModifierEmplacementScreen() {
    val sampleEmplacement = Emplacement(
        code = "A1",
        type = "Stockage",
        capaciteMax = 100,
        capaciteOccupe = 50,
        categorieProd = "Électroniques",
        statut = true // Disponible
    )
    ModifierEmplacementScreen(
        navController = rememberNavController(),
        emplacement = sampleEmplacement,
        onUpdateEmplacement = { updatedEmplacement -> /* Implémentation de la mise à jour de l'emplacement */ }
    )
}
