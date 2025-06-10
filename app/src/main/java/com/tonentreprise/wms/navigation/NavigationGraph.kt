package com.tonentreprise.wms.navigation

import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tonentreprise.wms.notifications.NotificationHelper
import com.tonentreprise.wms.ui.screens.*
import com.tonentreprise.wms.viewmodel.UserViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    userViewModel: UserViewModel,
    sharedPrefs: SharedPreferences,
    modifier: Modifier = Modifier
) {
    var utilisateurs by remember { mutableStateOf(sampleUsers) }
    val destinationTriggeredFromNotif = remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(navController, userViewModel)
        }

        composable("dashboard") {
            val role = sharedPrefs.getString("user_role", null)
            LaunchedEffect(role) {
                if (!destinationTriggeredFromNotif.value) {
                    when (role) {
                        "admin" -> navController.navigate("admin_dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                        "user" -> navController.navigate("dashboard_screen") {
                            popUpTo("login") { inclusive = true }
                        }
                        else -> navController.navigate("login") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    }
                }
            }
        }

        composable("admin_dashboard") {
            val isLoggedIn = sharedPrefs.getString("user_role", null) == "admin"
            if (!isLoggedIn) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("admin_dashboard") { inclusive = true }
                    }
                }
            } else {
                AdminDashboardScreen(navController, userViewModel)

                var hasSentNotification by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    if (!hasSentNotification) {
                        hasSentNotification = true
                        NotificationHelper.showStockAlertWithNavigation(
                            context = navController.context,
                            title = "Stock Critique",
                            message = "Produit A est en rupture imminente !",
                            destination = "vue_globale_stock"
                        )
                    }
                }
            }
        }

        composable("dashboard_screen") {
            val isLoggedIn = sharedPrefs.getString("user_role", null) == "user"
            if (!isLoggedIn) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("dashboard_screen") { inclusive = true }
                    }
                }
            } else {
                DashboardScreen(navController, userViewModel)
            }
        }

        composable("gestion_utilisateurs") {
            GestionUtilisateursScreen(navController)
        }
        composable("reception_details/{receptionId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("receptionId") ?: ""
            ReceptionDetailScreen(navController, id)
        }

        composable("transfert_details/{transfertId}") { backStackEntry ->
            val transfertId = backStackEntry.arguments?.getString("transfertId") ?: ""
            TransfertDetailScreen(navController, transfertId)
        }

        composable("ajout_utilisateur") {
            AjoutUtilisateurScreen(navController) { newUser ->
                utilisateurs = utilisateurs + newUser
                navController.popBackStack()
            }
        }

        composable("modifier_utilisateur/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ModifierUtilisateurScreen(navController, email)
        }

        composable("supprimer_utilisateur/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            SupprimerUtilisateurScreen(navController, email)
        }

        composable("preparation") { PreparationScreen(navController) }
        composable("reception") { ReceptionScreen(navController) }
        composable("transfert") { TransfertScreen(navController) }
        composable("ajouter_emplacement") { AjouterEmplacementScreen(navController) }
        composable("stock_tracking") { StockSuiviScreen(navController) }
        composable("ajouter_produit") { AjouterProduitScreen(navController) }
        composable("entry_exit_control") { ControleEntreeSortieScreen(navController) }
        composable("commande_details/{commandeId}") { backStackEntry ->
            val commandeId = backStackEntry.arguments?.getString("commandeId") ?: "Inconnu"
            CommandeDetailsScreen(navController, commandeId)
        }
        composable("stock_alerts") { StockAlertsScreen() }

        composable("vue_globale_stock") {
            destinationTriggeredFromNotif.value = true
            VueGlobaleStockScreen(navController = navController) // ✅ Corrigé ici
        }
    }
}
