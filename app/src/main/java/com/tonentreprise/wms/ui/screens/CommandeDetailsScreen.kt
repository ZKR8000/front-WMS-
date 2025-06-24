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
fun CommandeDetailsScreen(
    navController: NavHostController,
    commandeId: String
) {
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

    /* ---------- vibration utilitaire ---------- */
    fun vibrate(ctx: Context) {
        val vib = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vm = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vib.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    /* ---------- launcher caméra / scan ---------- */
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanned = result.data?.getStringExtra("barcode_result") ?: ""
            if (cabCode.isEmpty()) {
                cabCode = scanned
                lastScannedCab = scanned
                vibrate(context)
            } else if (emplCode.isEmpty()) {
                if (scanned == lastScannedCab) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            "❌ Erreur : CAB et EMPL ne peuvent pas être identiques !"
                        )
                    }
                } else {
                    emplCode = scanned
                    vibrate(context)

                    /* mise à jour quantité */
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

    /* ----------------------------- UI ----------------------------- */
    Scaffold(
        topBar = {
            /* Barre centrée (56 dp) : le titre est légèrement plus bas et centré */
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    Text(
                        text = "Commande #$commandeId",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Produit à scanner :",
                style = MaterialTheme.typography.headlineSmall
            )

            if (produitActuel != null) {
                CommandeDetailRow("Support", produitActuel[0])
                CommandeDetailRow("LPN", produitActuel[1])
                CommandeDetailRow("SKU", produitActuel[2])
                CommandeDetailRow("Adresse", produitActuel[3])
                CommandeDetailRow("Quantité demandée", produitActuel[4])
                CommandeDetailRow("Quantité validée", quantiteValidee.toString())

                Spacer(Modifier.height(20.dp))

                BarcodeInputField(label = "CAB", value = cabCode) {
                    launcher.launch(Intent(context, BarcodeScannerActivity::class.java))
                }
                Spacer(Modifier.height(12.dp))
                BarcodeInputField(label = "EMPL", value = emplCode) {
                    launcher.launch(Intent(context, BarcodeScannerActivity::class.java))
                }
            }
        }
    }
}

/* ------------------ composables aide ------------------ */

@Composable
fun CommandeDetailRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun BarcodeInputField(
    label: String,
    value: String,
    onScan: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            readOnly = true,
            singleLine = true
        )
        IconButton(onClick = onScan) {
            Icon(Icons.Filled.CameraAlt, contentDescription = "Scanner $label")
        }
    }
}

/* ------------------ preview ------------------ */
@Preview(showBackground = true)
@Composable
fun CommandeDetailsScreenPreview() {
    CommandeDetailsScreen(
        navController = rememberNavController(),
        commandeId = "1001"
    )
}
