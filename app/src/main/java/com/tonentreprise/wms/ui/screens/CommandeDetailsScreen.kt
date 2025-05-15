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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tonentreprise.wms.ui.BarcodeScannerActivity
import com.tonentreprise.wms.ui.commandesProduits
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandeDetailsScreen(navController: NavHostController, commandeId: String) {
    val produitsCommande = commandesProduits[commandeId] ?: emptyList()
    var produitIndex by remember { mutableIntStateOf(0) }
    val produitActuel = produitsCommande.getOrNull(produitIndex)

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var cabCode by remember { mutableStateOf("") }
    var emplCode by remember { mutableStateOf("") }
    var lastScannedCab by remember { mutableStateOf("") }
    var quantiteValidee by remember { mutableIntStateOf(0) }

    val quantiteDemandee = produitActuel?.get(4)?.toIntOrNull() ?: 0

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

            if (cabCode.isEmpty()) {
                cabCode = scannedValue
                lastScannedCab = scannedValue
                vibrate(context)
            } else if (emplCode.isEmpty()) {
                if (scannedValue == lastScannedCab) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("❌ Erreur: CAB et EMPL ne peuvent pas être identiques !")
                    }
                } else {
                    emplCode = scannedValue
                    vibrate(context)

                    // ✅ Nouvelle logique de validation
                    quantiteValidee++

                    if (quantiteValidee >= quantiteDemandee) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("✅ Produit validé !")
                        }
                        produitIndex++
                        quantiteValidee = 0
                    }

                    cabCode = ""
                    emplCode = ""
                    lastScannedCab = ""
                }
            }
        }
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Commande #$commandeId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Produit à scanner :", style = MaterialTheme.typography.headlineMedium)

            if (produitActuel != null) {
                CommandeDetailRow("Support", produitActuel[0])
                CommandeDetailRow("LPN", produitActuel[1])
                CommandeDetailRow("SKU", produitActuel[2])
                CommandeDetailRow("Adresse", produitActuel[3])
                CommandeDetailRow("Quantité demandée", produitActuel[4])
                CommandeDetailRow("Quantité validée", quantiteValidee.toString())

                Spacer(modifier = Modifier.height(16.dp))

                BarcodeInputField(label = "CAB", value = cabCode) {
                    val intent = Intent(context, BarcodeScannerActivity::class.java)
                    launcher.launch(intent)
                }

                BarcodeInputField(label = "EMPL", value = emplCode) {
                    val intent = Intent(context, BarcodeScannerActivity::class.java)
                    launcher.launch(intent)
                }
            }
        }
    }
}

@Composable
fun CommandeDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun BarcodeInputField(label: String, value: String, onScan: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            readOnly = true
        )
        IconButton(onClick = onScan) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Scanner $label")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommandeDetailsScreenPreview() {
    CommandeDetailsScreen(navController = rememberNavController(), commandeId = "1001")
}
