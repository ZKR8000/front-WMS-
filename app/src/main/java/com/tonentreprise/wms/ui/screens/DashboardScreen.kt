package com.tonentreprise.wms.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tonentreprise.wms.viewmodel.UserViewModel
import com.tonentreprise.wms.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val userRole by userViewModel.userRole.collectAsState()
    val context = LocalContext.current

    // 🔐 Rediriger si pas connecté ou pas un utilisateur normal
    LaunchedEffect(userRole) {
        if (userRole != UserRole.USER) {
            navController.navigate("login") {
                popUpTo("dashboard_screen") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tableau de Bord", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Bienvenue, Utilisateur", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

            // 📦 Boutons spécifiques à l'utilisateur
            UserMenuItem("Préparation", Icons.Filled.Warehouse) {
                navController.navigate("preparation")
            }

            UserMenuItem("Transfert", Icons.Filled.CompareArrows) {
                navController.navigate("transfert")
            }

            UserMenuItem("Réception", Icons.Filled.Receipt) {
                navController.navigate("reception")
            }

            // 🔒 Déconnexion
            UserMenuItem("Déconnexion", Icons.Filled.ExitToApp) {
                val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit().remove("user_role").apply()
                userViewModel.setUserRole(null)

                navController.navigate("login") {
                    popUpTo("dashboard_screen") { inclusive = true }
                }
            }
        }
    }
}

@Composable
fun UserMenuItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
        Text(label)
    }
}
