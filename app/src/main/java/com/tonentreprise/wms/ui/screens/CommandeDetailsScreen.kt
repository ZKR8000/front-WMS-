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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.runtime.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Modifier
import androidx.compose.material3.Card
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tonentreprise.wms.model.SalesOrderLightDTO
import com.tonentreprise.wms.network.RetrofitClient
import com.tonentreprise.wms.ui.BarcodeScannerActivity
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalGetImage::class, ExperimentalMaterial3Api::class)
@Composable
fun CommandeDetailsScreen(
    navController: NavHostController,
    commandeId: String
) {
    val produitsCommande = remember { mutableStateOf<List<SalesOrderLightDTO>>(emptyList()) }
    var produitIndex by remember { mutableIntStateOf(0) }
    val produitActuel = produitsCommande.value.getOrNull(produitIndex)

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var cabCode by remember { mutableStateOf("") }
    var emplCode by remember { mutableStateOf("") }
    var lastScannedCab by remember { mutableStateOf("") }
    var quantiteValidee by remember { mutableIntStateOf(0) }

    val apiService = RetrofitClient.getInstance(context)

    LaunchedEffect(commandeId) {
        try {
            val result = apiService.getAllSalesOrdersLight()

            if (result.isNotEmpty()) {
                produitsCommande.value = result
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Aucune commande disponible.")
                }
            }
        } catch (e: Exception) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Erreur lors du chargement des commandes.")
            }
        }
    }

    val quantiteDemandee = produitActuel?.salesOrderDetails?.firstOrNull()?.quantityToPrepare ?: 0

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

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanned = result.data?.getStringExtra("barcode_result") ?: ""
            if (cabCode.isEmpty()) {
                cabCode = scanned
                lastScannedCab = scanned
                vibrate(context)
            } else if (emplCode.isEmpty()) {
                if (scanned == lastScannedCab) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("❌ Erreur : CAB et EMPL ne peuvent pas être identiques !")
                    }
                } else {
                    emplCode = scanned
                    vibrate(context)

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
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text(
                    "Produit à scanner :",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            produitsCommande.value.forEach { commande ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .shadow(8.dp, shape = MaterialTheme.shapes.medium),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            CommandeDetailRow("Numéro de commande", commande.sohNum ?: "Non spécifié")
                            CommandeDetailRow("Client", commande.clientName ?: "Inconnu")
                            CommandeDetailRow("Statut", commande.status ?: "Inconnu")

                            Spacer(Modifier.height(16.dp))

                            commande.salesOrderDetails.forEach { detail ->
                                CommandeDetailRow("Produit", detail.product.artdes ?: "Inconnu")
                                CommandeDetailRow("Quantité demandée", detail.quantityToPrepare.toString())
                                CommandeDetailRow("Statut de ligne", detail.lineStatus ?: "Inconnu")
                                CommandeDetailRow("Date de livraison", detail.dlvdat)
                                CommandeDetailRow("Unité de mesure", detail.uom)
                            }
                        }
                    }
                }

                item {
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
}

@Composable
fun CommandeDetailRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
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

@Preview(showBackground = true)
@Composable
fun CommandeDetailsScreenPreview() {
    CommandeDetailsScreen(
        navController = rememberNavController(),
        commandeId = "1001"
    )
}
