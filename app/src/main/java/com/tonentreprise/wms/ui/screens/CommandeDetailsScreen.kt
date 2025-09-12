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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tonentreprise.wms.model.SalesOrderLightDTO
import com.tonentreprise.wms.model.SalesOrderDetailLightDTO
import com.tonentreprise.wms.network.RetrofitClient
import com.tonentreprise.wms.ui.BarcodeScannerActivity
import kotlinx.coroutines.launch
import kotlin.math.min

data class ProduitValidation(
    val sku: String,
    val quantiteDemandee: Int,
    val quantiteDisponible: Int,
    val quantiteSaisie: Int,
    val motif: String,
    val cabCode: String,
    val emplCode: String,
    val estValide: Boolean
)

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CommandeDetailsScreen(
    navController: NavHostController,
    commandeId: String
) {
    val commandesState = remember { mutableStateOf<List<SalesOrderLightDTO>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val apiService = RetrofitClient.getInstance(context)

    LaunchedEffect(commandeId) {
        try {
            val result = apiService.getAllSalesOrdersLight()
            commandesState.value = result.filter { it.sohNum == commandeId }
        } catch (e: Exception) {
            coroutineScope.launch { snackbarHostState.showSnackbar("Erreur lors du chargement des commandes.") }
        }
    }

    val commandes = commandesState.value
    val allDetails: List<SalesOrderDetailLightDTO> =
        remember(commandes) { commandes.flatMap { it.salesOrderDetails.orEmpty() } }

    // états par produit
    val cabCodes  = remember(allDetails) { mutableStateListOf<String>().apply { repeat(allDetails.size) { add("") } } }
    val emplCodes = remember(allDetails) { mutableStateListOf<String>().apply { repeat(allDetails.size) { add("") } } }
    val qteSaisie = remember(allDetails) { mutableStateListOf<String>().apply { repeat(allDetails.size) { add("") } } }
    val motifs    = remember(allDetails) { mutableStateListOf<String>().apply { repeat(allDetails.size) { add("") } } }
    // lecture seule pour le champ "Quantité saisie"
    val saisieReadOnly = remember(allDetails) { mutableStateListOf<Boolean>().apply { repeat(allDetails.size) { add(false) } } }

    val validationsProduits = remember(allDetails) { mutableStateMapOf<Int, ProduitValidation>() }

    var scanTarget by remember { mutableStateOf<Pair<Int, String>?>(null) }

    fun vibrate(ctx: Context) {
        val vib = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vm = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vib.vibrate(VibrationEffect.createOneShot(160, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val target = scanTarget
        if (result.resultCode == Activity.RESULT_OK && target != null) {
            val scanned = result.data?.getStringExtra("barcode_result").orEmpty()
            val (idx, type) = target
            if (idx in allDetails.indices) {
                if (type == "CAB") cabCodes[idx] = scanned else emplCodes[idx] = scanned
                vibrate(context)
            }
        }
        scanTarget = null
    }

    val totalPages = (allDetails.size.takeIf { it > 0 } ?: 0) + 1
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { totalPages })

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

        if (allDetails.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (pagerState.currentPage < allDetails.size) {
                LinearProgressIndicator(
                    progress = { (pagerState.currentPage + 1f) / allDetails.size.toFloat() },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Produit ${pagerState.currentPage + 1} sur ${allDetails.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Récapitulatif",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                if (page < allDetails.size) {
                    val d = allDetails[page]
                    val qDemandee = d.quantityToPrepare ?: 0
                    val name = d.product?.artdes ?: "Produit"
                    val sku  = d.product?.sku ?: "SKU inconnu"
                    val stockDisponible = d.product?.stockQty ?: 0
                    val needsMotif = qDemandee > stockDisponible

                    // ✅ règle : quantité saisie = min(demandée, stock) et champ bloqué
                    LaunchedEffect(page, qDemandee, stockDisponible) {
                        val auto = min(qDemandee, stockDisponible)
                        qteSaisie[page] = auto.toString()
                        saisieReadOnly[page] = true
                        if (!needsMotif && motifs[page].isNotEmpty()) motifs[page] = ""
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .shadow(8.dp, shape = MaterialTheme.shapes.medium),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Informations du produit",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(8.dp))
                                CommandeDetailRow("Numéro de commande", commandeId)
                                CommandeDetailRow("Produit", name)
                                CommandeDetailRow("SKU", sku)
                                CommandeDetailRow("Unité de mesure", d.uom ?: "—")
                                CommandeDetailRow("Quantité demandée", qDemandee.toString())
                            }
                        }

                        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Scanner les codes", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(12.dp))
                                BarcodeInputField(
                                    label = "CAB",
                                    value = cabCodes[page]
                                ) {
                                    scanTarget = page to "CAB"
                                    launcher.launch(Intent(context, BarcodeScannerActivity::class.java))
                                }
                                Spacer(Modifier.height(12.dp))
                                BarcodeInputField(
                                    label = "EMPL",
                                    value = emplCodes[page]
                                ) {
                                    scanTarget = page to "EMPL"
                                    launcher.launch(Intent(context, BarcodeScannerActivity::class.java))
                                }
                            }
                        }

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Quantités et motif", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = stockDisponible.toString(),
                                    onValueChange = {},
                                    label = { Text("Quantité disponible (stock)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    readOnly = true,
                                    singleLine = true
                                )
                                Spacer(Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = qteSaisie[page],
                                    onValueChange = { /* bloqué */ },
                                    label = { Text("Quantité saisie") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    readOnly = saisieReadOnly[page],
                                    enabled = !saisieReadOnly[page] // visuel grisé quand bloqué
                                )
                                Spacer(Modifier.height(12.dp))

                                MotifSelect(
                                    enabled = needsMotif,
                                    selected = motifs[page],
                                    onSelected = { motifs[page] = it }
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val saisie = qteSaisie[page].toIntOrNull() ?: 0
                                validationsProduits[page] = ProduitValidation(
                                    sku = sku,
                                    quantiteDemandee = qDemandee,
                                    quantiteDisponible = stockDisponible,
                                    quantiteSaisie = saisie,
                                    motif = motifs[page],
                                    cabCode = cabCodes[page],
                                    emplCode = emplCodes[page],
                                    estValide = stockDisponible >= qDemandee
                                )

                                coroutineScope.launch {
                                    if (page < allDetails.size - 1) {
                                        pagerState.animateScrollToPage(page + 1)
                                    } else {
                                        pagerState.animateScrollToPage(allDetails.size)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = cabCodes[page].isNotEmpty() &&
                                    emplCodes[page].isNotEmpty() &&
                                    (!needsMotif || motifs[page].isNotEmpty())
                        ) {
                            Text(if (page < allDetails.size - 1) "Produit suivant" else "Voir le résumé")
                            Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                } else {
                    SummaryPage(
                        details = allDetails,
                        validations = validationsProduits
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (pagerState.currentPage > 0) {
                    OutlinedButton(onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    }) { Text("Précédent") }
                } else {
                    Spacer(Modifier.width(8.dp))
                }

                if (pagerState.currentPage < totalPages - 1) {
                    Button(onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }) { Text("Suivant") }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.Close, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Annuler")
                        }
                        Button(onClick = {
                            coroutineScope.launch { snackbarHostState.showSnackbar("✅ Commande validée !") }
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Valider")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryPage(
    details: List<SalesOrderDetailLightDTO>,
    validations: Map<Int, ProduitValidation>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        details.indices.forEach { idx ->
            val d = details[idx]
            val sku = d.product?.sku ?: "SKU inconnu"
            val demandee = d.quantityToPrepare ?: 0
            val v = validations[idx]
            val ok = v?.estValide ?: false
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(sku, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "Demandé: $demandee | Disponible: ${v?.quantiteDisponible ?: 0}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!v?.motif.isNullOrEmpty()) {
                            Text("Motif: ${v?.motif}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }
                    }
                    Icon(
                        imageVector = if (ok) Icons.Filled.Check else Icons.Filled.Close,
                        contentDescription = if (ok) "OK" else "KO",
                        tint = if (ok) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Coche verte = stock suffisant. Croix rouge = stock insuffisant (motif requis).",
            style = MaterialTheme.typography.bodySmall
        )
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
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            readOnly = true,
            singleLine = true,
            trailingIcon = {
                if (value.isNotEmpty()) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = "Scanné", tint = Color(0xFF2E7D32))
                }
            }
        )
        IconButton(onClick = onScan) {
            Icon(Icons.Filled.CameraAlt, contentDescription = "Scanner $label")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotifSelect(
    enabled: Boolean,
    selected: String,
    onSelected: (String) -> Unit
) {
    val options = listOf("Rupture de stock", "Stock insuffisant")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text("Motif") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onSelected(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommandeDetailsScreenPreview() {
    CommandeDetailsScreen(
        navController = rememberNavController(),
        commandeId = "PREP-001"
    )
}