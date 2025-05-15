package com.tonentreprise.wms

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.tonentreprise.wms.navigation.NavigationGraph
import com.tonentreprise.wms.ui.theme.WMSScannerTheme
import com.tonentreprise.wms.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 Récupération des données persistées
        val sharedPrefs = getSharedPreferences("WMS_PREFS", Context.MODE_PRIVATE)
        val savedRole = sharedPrefs.getString("user_role", null)
        val intentDestination = intent?.getStringExtra("navigate_to")

        setContent {
            WMSScannerTheme {
                val navController = rememberNavController()
                val userViewModel = remember { UserViewModel() }
                val snackbarHostState = remember { SnackbarHostState() }
                var redirectionDone by remember { mutableStateOf(false) }

                // ✅ Redirection depuis notification si admin connecté
                LaunchedEffect(Unit) {
                    if (!redirectionDone && savedRole == "admin" && !intentDestination.isNullOrEmpty()) {
                        redirectionDone = true

                        // 🔁 Nettoie la pile et revient à admin_dashboard avant redirection
                        navController.navigate("admin_dashboard") {
                            popUpTo("login") { inclusive = true }
                        }

                        navController.navigate(intentDestination)

                        snackbarHostState.showSnackbar(
                            "🔔 Redirection vers : ${
                                intentDestination.replace("_", " ")
                                    .replaceFirstChar { it.uppercaseChar() }
                            }"
                        )
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    NavigationGraph(
                        navController = navController,
                        userViewModel = userViewModel,
                        sharedPrefs = sharedPrefs,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
