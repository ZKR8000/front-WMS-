package com.tonentreprise.wms.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Input
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControleEntreeSortieScreen(navController: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var dateDebut by remember { mutableStateOf<LocalDate?>(null) }
    var dateFin by remember { mutableStateOf<LocalDate?>(null) }

    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    val entrees = listOf(
        Operation("Entrée", "Produit A", "Fournisseur X", 50, "Stock initial", LocalDate.of(2025, 3, 10)),
        Operation("Entrée", "Produit B", "Fournisseur Y", 100, "Retour fournisseur", LocalDate.of(2025, 3, 15)),
        Operation("Entrée", "Produit C", "Fournisseur Z", 60, "Réapprovisionnement", LocalDate.of(2025, 3, 20))
    )

    val sorties = listOf(
        Operation("Sortie", "Produit A", "Client Alpha", 30, "Commande client", LocalDate.of(2025, 3, 12)),
        Operation("Sortie", "Produit C", "Client Beta", 20, "Transfert entre sites", LocalDate.of(2025, 3, 17)),
        Operation("Sortie", "Produit D", "Client Gamma", 25, "Vente", LocalDate.of(2025, 3, 22))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contrôle des Entrées / Sorties") },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Boutons Entrée / Sortie
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Entrées") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Input, contentDescription = null) }
                )
                FilterChip(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Sorties") },
                    leadingIcon = { Icon(Icons.Filled.ExitToApp, contentDescription = null) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sélection de la plage de dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DateButton(
                    label = "Date début",
                    date = dateDebut,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        showDatePicker(context) { selected -> dateDebut = selected }
                    }
                )

                DateButton(
                    label = "Date fin",
                    date = dateFin,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        showDatePicker(context) { selected -> dateFin = selected }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filtrage des données
            val allData = if (selectedTab == 0) entrees else sorties

            val filteredData = allData.filter { op ->
                val date = op.date
                (dateDebut == null || !date.isBefore(dateDebut)) &&
                        (dateFin == null || !date.isAfter(dateFin))
            }

            val totalQuantite = filteredData.sumOf { it.quantite }
            val nombreOperations = filteredData.size

            // Carte de statistiques
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (selectedTab == 0) "📥 Statistiques des Entrées" else "📤 Statistiques des Sorties",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Nombre d’opérations : $nombreOperations")
                    Text("Quantité totale : $totalQuantite")
                }
            }

            // Liste des opérations
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredData) { op ->
                    OperationCard(op, formatter)
                }
            }
        }
    }
}

@Composable
fun DateButton(label: String, date: LocalDate?, modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(12.dp)
    ) {
        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
        Text(
            text = date?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) ?: label,
            textAlign = TextAlign.Center
        )
    }
}

fun showDatePicker(context: android.content.Context, onDateSelected: (LocalDate) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            onDateSelected(LocalDate.of(year, month + 1, day))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@Composable
fun OperationCard(operation: Operation, formatter: DateTimeFormatter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Type : ${operation.type}", fontWeight = FontWeight.Bold)
            Text("Produit : ${operation.produit}")
            Text("Fournisseur / Client : ${operation.fournisseur}")
            Text("Quantité : ${operation.quantite}")
            Text("Motif : ${operation.motif}")
            Text("Date : ${operation.date.format(formatter)}")
        }
    }
}

data class Operation(
    val type: String,
    val produit: String,
    val fournisseur: String,
    val quantite: Int,
    val motif: String,
    val date: LocalDate
)
