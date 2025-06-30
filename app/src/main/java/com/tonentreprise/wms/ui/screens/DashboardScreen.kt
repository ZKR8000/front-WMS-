package com.tonentreprise.wms.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tonentreprise.wms.viewmodel.UserViewModel
import com.tonentreprise.wms.model.UserRole
import androidx.core.content.edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val userRole by userViewModel.userRole.collectAsState()
    val context = LocalContext.current

    // Redirection automatique si l'utilisateur n'est pas authentifié ou n'est pas un utilisateur
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
                title = {
                    Text(
                        "Tableau de Bord",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212)) // Couleur de fond sombre
            )
        },
        containerColor = Color(0xFF121212) // Fond sombre
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF121212)), // Fond sombre
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Bienvenue 👋",
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                color = Color(0xFFBB86FC), // Texte violet clair
                modifier = Modifier.padding(vertical = 32.dp)
            )

            DashboardMenuCard(
                label = "Préparation",
                icon = Icons.Filled.Warehouse,
                color = Color(0xFF03DAC5), // Teal clair
                onClick = { navController.navigate("preparation") }
            )
            DashboardMenuCard(
                label = "Transfert",
                icon = Icons.AutoMirrored.Filled.CompareArrows,
                color = Color(0xFF018786), // Teal foncé
                onClick = { navController.navigate("transfert") }
            )
            DashboardMenuCard(
                label = "Réception",
                icon = Icons.Filled.Receipt,
                color = Color(0xFFCF6679), // Rouge clair
                onClick = { navController.navigate("reception") }
            )
            DashboardMenuCard(
                label = "Déconnexion",
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                color = Color(0xFFBB86FC), // Violet clair
                onClick = {
                    // Effacer les données d'utilisateur de SharedPreferences
                    val prefs = context.getSharedPreferences("WMS_PREFS", Context.MODE_PRIVATE)
                    prefs.edit {
                        remove("jwt_token") // Supprimer le token
                        remove("user_email") // Supprimer l'email
                        remove("user_role") // Supprimer le rôle
                    }

                    // Réinitialiser le rôle utilisateur dans ViewModel
                    userViewModel.setUserRole(null)

                    // Rediriger vers l'écran de connexion
                    navController.navigate("login") {
                        popUpTo("dashboard_screen") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun DashboardMenuCard(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 24.dp)
            .height(72.dp)  // Carte légèrement plus grande
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(8.dp),  // Ombres plus douces
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333)) // Fond sombre de la carte
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(32.dp) // Icône légèrement plus grande
            )
            Spacer(Modifier.width(18.dp))
            Text(
                label,
                fontWeight = FontWeight.SemiBold,
                color = Color.White, // Texte blanc pour le contraste
                fontSize = 22.sp,  // Taille de police plus grande
            )
        }
    }
}
