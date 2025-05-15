package com.tonentreprise.wms.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tonentreprise.wms.ui.BarcodeScannerActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjouterProduitScreen(navController: NavHostController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var support by remember { mutableStateOf("") }
    var lpn by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var adresse by remember { mutableStateOf("") }
    var quantite by remember { mutableStateOf("") }
    var cab by remember { mutableStateOf("") }
    var empl by remember { mutableStateOf("") }

    var scanningField by remember { mutableStateOf("") }

    val vibrate: () -> Unit = {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val scannedValue = data?.getStringExtra("barcode_result") ?: ""
            vibrate()
            when (scanningField) {
                "CAB" -> {
                    cab = scannedValue
                    sku = "SKU-${scannedValue.takeLast(4)}"
                }
                "EMPL" -> {
                    empl = scannedValue
                    adresse = "Zone A - Emplacement $scannedValue"
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajouter un Produit") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = support, onValueChange = { support = it }, label = { Text("Support") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = lpn, onValueChange = { lpn = it }, label = { Text("LPN") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = sku, onValueChange = {}, label = { Text("SKU") }, readOnly = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = adresse, onValueChange = {}, label = { Text("Adresse (auto)") }, readOnly = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = quantite,
                onValueChange = { quantite = it },
                label = { Text("Quantité") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = cab,
                    onValueChange = {},
                    label = { Text("CAB") },
                    readOnly = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    scanningField = "CAB"
                    val intent = Intent(context, BarcodeScannerActivity::class.java)
                    scannerLauncher.launch(intent)
                }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Scanner CAB")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = empl,
                    onValueChange = {},
                    label = { Text("EMPL") },
                    readOnly = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    scanningField = "EMPL"
                    val intent = Intent(context, BarcodeScannerActivity::class.java)
                    scannerLauncher.launch(intent)
                }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Scanner EMPL")
                }
            }

            Button(onClick = {
                if (support.isBlank() || lpn.isBlank() || sku.isBlank() || adresse.isBlank() || quantite.isBlank() || cab.isBlank() || empl.isBlank()) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("❌ Veuillez remplir tous les champs.")
                    }
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("✅ Produit ajouté avec succès !")
                    }
                    navController.popBackStack()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Ajouter")
            }
        }
    }
}
