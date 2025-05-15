// ✅ Écran moderne pour Ajouter un Utilisateur (Jetpack Compose)
package com.tonentreprise.wms.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjoutUtilisateurScreen(
    navController: NavHostController,
    onUserAdded: (User) -> Unit
) {
    val scope = rememberCoroutineScope()
    var nom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Utilisateur") }
    var isSuccess by remember { mutableStateOf(false) }
    val roles = listOf("Admin", "Utilisateur", "Superviseur")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajouter un Utilisateur") },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Formulaire", fontSize = 22.sp, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom complet") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {},
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = role,
                    onValueChange = {},
                    label = { Text("Rôle") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                DropdownMenu(
                    expanded = false,
                    onDismissRequest = {}
                ) {
                    roles.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = { role = it }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (nom.isNotBlank() && email.isNotBlank()) {
                        val newUser = User(nom, email, role)
                        onUserAdded(newUser)
                        isSuccess = true
                        scope.launch {
                            kotlinx.coroutines.delay(1500)
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ajouter")
            }

            if (isSuccess) {
                Text("✅ Utilisateur ajouté avec succès", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
