package com.tonentreprise.wms.ui.screens

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.tonentreprise.wms.model.UserRole
import com.tonentreprise.wms.notifications.NotificationHelper
import com.tonentreprise.wms.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    val userRole by userViewModel.userRole.collectAsState()
    val context = LocalContext.current
    val sharedPrefs = remember {
        context.getSharedPreferences("WMS_PREFS", Context.MODE_PRIVATE)
    }

    // ✅ Redirection si non authentifié
    LaunchedEffect(userRole) {
        if (userRole != UserRole.ADMIN) {
            navController.navigate("login") {
                popUpTo("admin_dashboard") { inclusive = true }
            }
        }
    }

    // ✅ Notification Android 13+ - Permission
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // ✅ Notification une seule fois
    var notificationSent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!notificationSent) {
            notificationSent = true
            NotificationHelper.showStockAlertWithNavigation(
                context = context,
                title = "Alerte de Stock",
                message = "Produit A est en rupture imminente !",
                destination = "vue_globale_stock"
            )
        }
    }

    // ✅ UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tableau de Bord Administrateur", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E2A38))
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
            Text(
                text = "Bienvenue, Administrateur",
                fontSize = 24.sp,
                color = Color(0xFF1E2A38),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            AdminMenuItem("Gérer les Utilisateurs", Icons.Default.Person) {
                navController.navigate("gestion_utilisateurs")
            }
            AdminMenuItem("Ajouter un Emplacement", Icons.Default.AddLocation) {
                navController.navigate("ajouter_emplacement")
            }
            AdminMenuItem("Ajouter un Produit", Icons.Default.Inventory) {
                navController.navigate("ajouter_produit")
            }
            AdminMenuItem("Alertes de Stock", Icons.Default.Notifications) {
                navController.navigate("stock_alerts")
            }
            AdminMenuItem("Voir les Emplacements", Icons.Default.Place) {
                navController.navigate("liste_emplacements")
            }
            AdminMenuItem("Déconnexion", Icons.AutoMirrored.Filled.ExitToApp) {
                sharedPrefs.edit().remove("user_role").apply()
                userViewModel.setUserRole(null)
                navController.navigate("login") {
                    popUpTo("admin_dashboard") { inclusive = true }
                }
            }
        }
    }
}
