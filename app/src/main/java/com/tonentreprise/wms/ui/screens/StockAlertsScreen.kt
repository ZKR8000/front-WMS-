package com.tonentreprise.wms.ui.screens

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tonentreprise.wms.MainActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAlertsScreen() {
    val context = LocalContext.current

    val stockAlerts = listOf(
        "Stock Critique - Produit A" to "Il ne reste que 3 unités de Produit A !",
        "Surstock - Produit B" to "Produit B dépasse la capacité de stockage !"
    )

    // ✅ Crée une notification statique (à titre de test)
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) return@LaunchedEffect
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "vue_globale_stock")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, "stock_alerts_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Alerte de Stock")
            .setContentText("Produit A est en rupture imminente !")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        NotificationManagerCompat.from(context).notify(1001, builder.build())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alertes de Stock", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE3D7FF))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            stockAlerts.forEach { (title, description) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF8B0000)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("\uD83D\uDD14 $title", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(description, color = Color.White)
                    }
                }
            }
        }
    }
}
