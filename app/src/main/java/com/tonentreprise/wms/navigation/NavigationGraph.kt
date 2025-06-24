package com.tonentreprise.wms.navigation

/* ------------------------------------------------------------------ */
/* ------------------------------ IMPORTS --------------------------- */
/* ------------------------------------------------------------------ */
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tonentreprise.wms.data.User
import com.tonentreprise.wms.data.sampleUsers
import com.tonentreprise.wms.notifications.NotificationHelper
import com.tonentreprise.wms.ui.screens.*
import com.tonentreprise.wms.viewmodel.UserViewModel
import com.tonentreprise.wms.model.Emplacement  // Importer le modèle Emplacement

/* ------------------------------------------------------------------ */
/* --------------------------- NAVIGRAPH ---------------------------- */
/* ------------------------------------------------------------------ */
@Composable
fun NavigationGraph(
    navController: NavHostController,
    userViewModel: UserViewModel,
    sharedPrefs: SharedPreferences,
    modifier: Modifier = Modifier
) {
    /* --- état local de démonstration : liste d’utilisateurs --- */
    var utilisateurs by remember { mutableStateOf<List<User>>(sampleUsers) }

    /* --- Flag pour éviter navigation double quand notif --- */
    val destinationTriggeredFromNotif = remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {

        /* ---------------------- Auth ---------------------- */
        composable("login") {
            LoginScreen(navController, userViewModel)
        }

        /* --------- Simple dispatcher (user / admin) -------- */
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
                        else   -> navController.navigate("login") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    }
                }
            }
        }

        /* --------------------- Admin ---------------------- */
        composable("admin_dashboard") {
            val isAdmin = sharedPrefs.getString("user_role", null) == "admin"
            if (!isAdmin) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") { popUpTo("admin_dashboard") { inclusive = true } }
                }
            } else {
                AdminDashboardScreen(navController, userViewModel)

                /* Notification demo : stock critique */
                var sent by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    if (!sent) {
                        sent = true
                        NotificationHelper.showStockAlertWithNavigation(
                            context      = navController.context,
                            title        = "Stock Critique",
                            message      = "Produit A est en rupture imminente !",
                            destination  = "vue_globale_stock"
                        )
                    }
                }
            }
        }

        /* ------------------ Dashboard User ---------------- */
        composable("dashboard_screen") {
            val isUser = sharedPrefs.getString("user_role", null) == "user"
            if (!isUser) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") { popUpTo("dashboard_screen") { inclusive = true } }
                }
            } else {
                DashboardScreen(navController, userViewModel)
            }
        }

        /* ---------------- Gestion Utilisateurs ------------ */
        composable("gestion_utilisateurs") {
            GestionUtilisateursScreen(navController)
        }

        /* -------- Détails réception / transfert ---------- */
        composable("reception_details/{receptionId}") { backStack ->
            ReceptionDetailScreen(
                navController,
                backStack.arguments?.getString("receptionId") ?: ""
            )
        }
        composable("transfert_details/{transfertId}") { backStack ->
            TransfertDetailScreen(
                navController,
                backStack.arguments?.getString("transfertId") ?: ""
            )
        }

        /* --------------- CRUD Utilisateurs --------------- */
        composable("ajout_utilisateur") {
            AjoutUtilisateurScreen(navController) { newUser: User ->
                utilisateurs = utilisateurs + newUser
                navController.popBackStack()
            }
        }

        /* ----------------- Liste des Emplacements -------------- */
        composable("liste_emplacements") {
            ListeEmplacementsScreen()
        }

        /* ---------------- Modifier Emplacement ---------------- */
        composable("modifier_emplacement/{emplacementCode}") { backStackEntry ->
            val emplacementCode = backStackEntry.arguments?.getString("emplacementCode") ?: ""
            // Récupère l'emplacement via le code
            val emplacement = getEmplacementByCode(emplacementCode) // Méthode à définir
            ModifierEmplacementScreen(
                navController = navController,
                emplacement = emplacement,
                onUpdateEmplacement = { updatedEmplacement ->
                    // Implémente la logique de mise à jour de l'emplacement
                    updateEmplacement(updatedEmplacement) // Méthode à définir
                }
            )
        }

        composable("modifier_utilisateur/{email}") { backStack ->
            ModifierUtilisateurScreen(
                navController,
                backStack.arguments?.getString("email") ?: ""
            )
        }
        composable("supprimer_utilisateur/{email}") { backStack ->
            SupprimerUtilisateurScreen(
                navController,
                backStack.arguments?.getString("email") ?: ""
            )
        }

        /* ----------------- Autres écrans ------------------ */
        composable("preparation") { PreparationScreen(navController) }
        composable("reception") { ReceptionScreen(navController) }
        composable("transfert") { TransfertScreen(navController) }
        composable("ajouter_emplacement") { AjouterEmplacementScreen(navController) }
        composable("stock_tracking") { StockSuiviScreen(navController) }
        composable("ajouter_produit") { AjouterProduitScreen(navController) }
        composable("entry_exit_control") { ControleEntreeSortieScreen(navController) }
        composable("commande_details/{commandeId}") { backStack ->
            CommandeDetailsScreen(
                navController,
                backStack.arguments?.getString("commandeId") ?: "Inconnu"
            )
        }
        composable("stock_alerts") { StockAlertsScreen() }

        /* ----------- Destination depuis notification ------- */
        composable("vue_globale_stock") {
            destinationTriggeredFromNotif.value = true
            VueGlobaleStockScreen(navController)
        }
    }
}

/* --- Méthodes fictives pour la récupération et mise à jour des emplacements --- */
fun getEmplacementByCode(code: String): Emplacement {
    // Logique pour récupérer l'emplacement par son code
    return Emplacement(code = code, type = "Stockage", capaciteMax = 100, capaciteOccupe = 50, categorieProd = "Électroniques", statut = true)
}

fun updateEmplacement(emplacement: Emplacement) {
    // Logique pour mettre à jour l'emplacement dans la base de données ou la liste
}
