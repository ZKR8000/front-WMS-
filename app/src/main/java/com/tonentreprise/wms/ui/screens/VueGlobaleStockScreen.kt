
package com.tonentreprise.wms.ui.screens
import android.app.DatePickerDialog
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.widget.DatePicker
import androidx.navigation.NavHostController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

// 📊 Statistique simple
@Composable
fun StatCard(title: String, content: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            content.forEach { Text(it) }
        }
    }
}

// 📈 Charts Tabs
@Composable
fun StockChartsTabs(operations: List<Operation>, stockParProduit: Map<String, Int>) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Évolution", "Répartition")

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        when (selectedTab) {
            0 -> LineChartGlobalStockEvolution(operations)
            1 -> PieChartStockDistribution(stockParProduit)
        }
    }
}

// 📊 Line chart
@Composable
fun LineChartGlobalStockEvolution(operations: List<Operation>) {
    val grouped = operations.groupBy { it.date }
        .mapValues { entry ->
            val totalEntree = entry.value.filter { it.type == "Entrée" }.sumOf { it.quantite }
            val totalSortie = entry.value.filter { it.type == "Sortie" }.sumOf { it.quantite }
            totalEntree - totalSortie
        }
        .toSortedMap()

    val points = grouped.entries.fold(mutableListOf<Pair<String, Int>>()) { acc, (date, delta) ->
        val prev = acc.lastOrNull()?.second ?: 0
        acc.add(date.format(DateTimeFormatter.ofPattern("dd/MM")) to (prev + delta))
        acc
    }

    val maxY = (points.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp)
    ) {
        val spacing = size.width / (points.size - 1).coerceAtLeast(1)
        val pathPoints = points.mapIndexed { index, point ->
            Offset(x = index * spacing, y = size.height - (point.second / maxY.toFloat()) * size.height)
        }

        for (i in 1 until pathPoints.size) {
            drawLine(
                color = Color(0xFF9370DB),
                start = pathPoints[i - 1],
                end = pathPoints[i],
                strokeWidth = 4f
            )
            drawCircle(
                color = Color(0xFF9370DB),
                radius = 6f,
                center = pathPoints[i]
            )
        }
    }
}

// 🍰 Pie chart
@Composable
fun PieChartStockDistribution(stockMap: Map<String, Int>) {
    val total = stockMap.values.sum().takeIf { it > 0 } ?: 1
    val proportions = stockMap.mapValues { it.value.toFloat() / total }
    val colors = listOf(Color.Blue, Color.Green, Color(0xFFFFC107), Color.Red, Color.Magenta)

    Canvas(modifier = Modifier.fillMaxWidth().height(220.dp)) {
        var startAngle = -90f
        proportions.entries.forEachIndexed { index, (_, value) ->
            val sweep = value * 360f
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true
            )
            startAngle += sweep
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Column {
        proportions.entries.forEachIndexed { index, (label, value) ->
            Text("• $label : ${(value * 100).roundToInt()}%", color = colors[index % colors.size])
        }
    }
}

// 🧠 Écran principal VueGlobaleStock
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VueGlobaleStockScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPrefs = remember {
        context.getSharedPreferences("WMS_PREFS", Context.MODE_PRIVATE)
    }

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val isAdmin = remember { sharedPrefs.getString("user_role", null) == "admin" }

    LaunchedEffect(Unit) {
        if (!isAdmin) {
            navController.navigate("login") {
                popUpTo("vue_globale_stock") { inclusive = true }
            }
        }
    }

    BackHandler(enabled = true) {
        navController.navigate("admin_dashboard") {
            popUpTo("vue_globale_stock") { inclusive = true }
        }
    }

    if (!isAdmin) return

    var searchQuery by remember { mutableStateOf("") }
    var dateDebut by remember { mutableStateOf<LocalDate?>(null) }
    var dateFin by remember { mutableStateOf<LocalDate?>(null) }

    val operations = listOf(
        Operation("Entrée", "Produit A", "Fournisseur X", 100, "Réception", LocalDate.of(2025, 3, 10)),
        Operation("Sortie", "Produit A", "Client Z", 40, "Commande", LocalDate.of(2025, 3, 12)),
        Operation("Entrée", "Produit B", "Fournisseur Y", 80, "Réapprovisionnement", LocalDate.of(2025, 3, 14)),
        Operation("Sortie", "Produit B", "Client Y", 20, "Transfert", LocalDate.of(2025, 3, 15)),
        Operation("Entrée", "Produit C", "Fournisseur Z", 60, "Réception", LocalDate.of(2025, 3, 16))
    )

    val filteredOperations = operations.filter { op ->
        val matchProduit = op.produit.contains(searchQuery, ignoreCase = true)
        val matchDate = (dateDebut == null || !op.date.isBefore(dateDebut)) &&
                (dateFin == null || !op.date.isAfter(dateFin))
        matchProduit && matchDate
    }

    val totalEntrees = filteredOperations.filter { it.type == "Entrée" }.sumOf { it.quantite }
    val totalSorties = filteredOperations.filter { it.type == "Sortie" }.sumOf { it.quantite }
    val balance = totalEntrees - totalSorties

    val produits = filteredOperations.map { it.produit }.toSet()
    val stockParProduit = produits.associateWith { produit ->
        val entrees = filteredOperations.filter { it.type == "Entrée" && it.produit == produit }.sumOf { it.quantite }
        val sorties = filteredOperations.filter { it.type == "Sortie" && it.produit == produit }.sumOf { it.quantite }
        entrees - sorties
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vue Globale du Stock") },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Rechercher un produit") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _: DatePicker, year: Int, month: Int, day: Int ->
                                dateDebut = LocalDate.of(year, month + 1, day)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }, modifier = Modifier.weight(1f)) {
                        Text(dateDebut?.format(formatter) ?: "Date début")
                    }

                    Button(onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _: DatePicker, year: Int, month: Int, day: Int ->
                                dateFin = LocalDate.of(year, month + 1, day)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }, modifier = Modifier.weight(1f)) {
                        Text(dateFin?.format(formatter) ?: "Date fin")
                    }
                }
            }

            item {
                StatCard("📊 Statistiques Globales", listOf(
                    "Total des entrées : $totalEntrees",
                    "Total des sorties : $totalSorties",
                    "Solde net : $balance"
                ))
            }

            item {
                StatCard("📦 Stock Actuel par Produit", stockParProduit.map { "${it.key} : ${it.value} unités" })
            }

            item {
                StockChartsTabs(filteredOperations, stockParProduit)
            }

            item {
                Text("📜 Historique des Mouvements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            if (filteredOperations.isEmpty()) {
                item {
                    Text("🚫 Aucun résultat trouvé.", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            } else {
                items(filteredOperations.sortedByDescending { it.date }) { op ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Type : ${op.type}", fontWeight = FontWeight.Bold)
                            Text("Produit : ${op.produit}")
                            Text("Quantité : ${op.quantite}")
                            Text("Motif : ${op.motif}")
                            Text("Date : ${op.date.format(formatter)}")
                        }
                    }
                }
            }
        }
    }
}
