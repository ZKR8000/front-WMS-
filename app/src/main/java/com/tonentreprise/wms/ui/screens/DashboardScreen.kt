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

    // Redirect to login if the user is not authenticated or is not a regular user
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212)) // Dark header color
            )
        },
        containerColor = Color(0xFF121212) // Dark background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF121212)), // Dark background
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Bienvenue 👋",
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                color = Color(0xFFBB86FC), // Light purple text
                modifier = Modifier.padding(vertical = 32.dp)
            )

            DashboardMenuCard(
                label = "Préparation",
                icon = Icons.Filled.Warehouse,
                color = Color(0xFF03DAC5), // Light Teal
                onClick = { navController.navigate("preparation") }
            )
            DashboardMenuCard(
                label = "Transfert",
                icon = Icons.AutoMirrored.Filled.CompareArrows,
                color = Color(0xFF018786), // Dark Teal
                onClick = { navController.navigate("transfert") }
            )
            DashboardMenuCard(
                label = "Réception",
                icon = Icons.Filled.Receipt,
                color = Color(0xFFCF6679), // Light Red
                onClick = { navController.navigate("reception") }
            )
            DashboardMenuCard(
                label = "Déconnexion",
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                color = Color(0xFFBB86FC), // Light Purple
                onClick = {
                    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    prefs.edit { remove("user_role") }
                    userViewModel.setUserRole(null)
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
            .height(72.dp)  // Slightly bigger card
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(8.dp),  // Softer shadows
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333)) // Dark card background
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
                modifier = Modifier.size(32.dp) // Slightly bigger icon
            )
            Spacer(Modifier.width(18.dp))
            Text(
                label,
                fontWeight = FontWeight.SemiBold,
                color = Color.White, // White text for contrast
                fontSize = 22.sp,  // Larger font size
            )
        }
    }
}
