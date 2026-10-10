package com.example.mediverify.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mediverify.data.model.ScanHistory
import com.example.mediverify.ui.viewmodel.MediVerifyViewModel

@Composable
fun HistoryScreen(navController: NavController, viewModel: MediVerifyViewModel) {
    val history by viewModel.recentScans.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAF9))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Scan history", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Button(onClick = { viewModel.clearHistory() }) {
                Text("Clear")
            }
        }

        if (history.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("No scan history", fontWeight = FontWeight.Bold)
                    Text("The app stores a local history of completed checks here.", color = Color(0xFF475569))
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(history) { item ->
                    HistoryCard(item)
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(item: ScanHistory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(item.medicineName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(item.batchNumber, color = Color(0xFF475569))
            Text(item.status, color = when {
                item.status.contains("RECALLED", ignoreCase = true) || item.status.contains("COUNTERFEIT", ignoreCase = true) -> Color(0xFFDC2626)
                item.status.contains("EXPIRED", ignoreCase = true) -> Color(0xFFF59E0B)
                item.status.contains("VERIFIED", ignoreCase = true) -> Color(0xFF16A34A)
                else -> Color(0xFF64748B)
            })
        }
    }
}
