// ✅ Version professionnelle de l'écran AjouterEmplacementScreen
package com.tonentreprise.wms.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tonentreprise.wms.ui.BarcodeScannerActivity
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalGetImage::class)
@Composable
fun AjouterEmplacementScreen(navController: NavHostController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var codeEmplacement by remember { mutableStateOf("") }
    var typeEmplacement by remember { mutableStateOf("") }
    var capaciteMax by remember { mutableStateOf("") }
    var selectedCategorie by remember { mutableStateOf("") }
    var statut by remember { mutableStateOf("Disponible") }

    val types = listOf("Palette", "Rayonnage", "Réfrigéré", "Sol", "Rack")
    val categories = listOf("Boissons", "Produits frais", "Médicaments", "Électronique")
    val statuts = listOf("Disponible", "Indisponible")

    val typeToCapacite = mapOf(
        "Palette" to "1000 kg",
        "Rayonnage" to "300 kg",
        "Réfrigéré" to "500 kg",
        "Sol" to "1500 kg",
        "Rack" to "800 kg"
    )

    var expandedType by remember { mutableStateOf(false) }
    var expandedCategorie by remember { mutableStateOf(false) }
    var expandedStatut by remember { mutableStateOf(false) }

    fun vibrate(context: Context) {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scannedValue = result.data?.getStringExtra("barcode_result") ?: ""
            codeEmplacement = scannedValue
            vibrate(context)
        }
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Ajouter un Emplacement",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Remplissez les informations de l'emplacement", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = codeEmplacement,
                onValueChange = {},
                readOnly = true,
                label = { Text("Code Emplacement") },
                trailingIcon = {
                    IconButton(onClick = {
                        val intent = Intent(context, BarcodeScannerActivity::class.java)
                        launcher.launch(intent)
                    }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Scanner")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(expanded = expandedType, onExpandedChange = { expandedType = !expandedType }) {
                OutlinedTextField(
                    value = typeEmplacement,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type d'Emplacement") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                    types.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            typeEmplacement = it
                            capaciteMax = typeToCapacite[it] ?: ""
                            expandedType = false
                        })
                    }
                }
            }

            OutlinedTextField(
                value = capaciteMax,
                onValueChange = {},
                readOnly = true,
                label = { Text("Capacité Maximale") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(expanded = expandedCategorie, onExpandedChange = { expandedCategorie = !expandedCategorie }) {
                OutlinedTextField(
                    value = selectedCategorie,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Catégorie de Produits Acceptés") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedCategorie, onDismissRequest = { expandedCategorie = false }) {
                    categories.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            selectedCategorie = it
                            expandedCategorie = false
                        })
                    }
                }
            }

            ExposedDropdownMenuBox(expanded = expandedStatut, onExpandedChange = { expandedStatut = !expandedStatut }) {
                OutlinedTextField(
                    value = statut,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Statut") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedStatut, onDismissRequest = { expandedStatut = false }) {
                    statuts.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            statut = it
                            expandedStatut = false
                        })
                    }
                }
            }

            Button(
                onClick = {
                    if (codeEmplacement.isBlank() || typeEmplacement.isBlank() || capaciteMax.isBlank() || selectedCategorie.isBlank() || statut.isBlank()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("❌ Veuillez remplir tous les champs.")
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("✅ Emplacement ajouté avec succès !")
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ajouter l'Emplacement")
            }
        }
    }
}
