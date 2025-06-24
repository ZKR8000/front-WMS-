package com.tonentreprise.wms.ui.screens

/* ---------------- IMPORTS ---------------- */
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tonentreprise.wms.data.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* -------------- COULEURS PERSO -------------- */
private val Lavender = Color(0xFFD0BFFF)
private val Indigo   = Color(0xFF7868E6)

/* -------------- ÉCRAN -------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjoutUtilisateurScreen(
    navController: NavHostController,
    onUserAdded: (User) -> Unit
) {
    /* -- États du formulaire -- */
    var nom   by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val roles = listOf("Admin", "Superviseur", "Utilisateur")
    var role  by remember { mutableStateOf(roles.last()) }
    var expanded by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                    Text("Ajouter un Utilisateur",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Lavender
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Formulaire", fontSize = 22.sp, fontWeight = FontWeight.Bold)

            /* ---- Nom ---- */
            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom complet") },
                leadingIcon = { Icon(Icons.Filled.Person, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            /* ---- Email ---- */
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Filled.Email, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            /* ---- Rôle ---- */
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = role,
                    onValueChange = {},
                    label = { Text("Rôle") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }) {
                    roles.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                role = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            /* ---- Bouton Ajouter ---- */
            Button(
                onClick = {
                    if (nom.isNotBlank() && email.isNotBlank()) {
                        onUserAdded(User(nom, email, role))
                        showSuccess = true
                        scope.launch {
                            delay(1200)
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(
                        Brush.horizontalGradient(listOf(Lavender, Indigo)),
                        RoundedCornerShape(40)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor   = Color(0xFF1A1A1A)
                ),
                shape = RoundedCornerShape(40),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Filled.PersonAdd, null)
                Spacer(Modifier.width(8.dp))
                Text("Ajouter", fontWeight = FontWeight.SemiBold)
            }

            if (showSuccess) {
                Text("✅ Utilisateur ajouté", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

/* ----------- PREVIEW ----------- */
@Preview(showBackground = true)
@Composable
fun PreviewAjoutUtilisateur() {
    AjoutUtilisateurScreen(
        navController = rememberNavController(),
        onUserAdded = {}
    )
}
