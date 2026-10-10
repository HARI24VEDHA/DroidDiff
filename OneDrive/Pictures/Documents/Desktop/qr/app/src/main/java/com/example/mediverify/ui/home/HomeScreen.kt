package com.example.mediverify.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mediverify.data.model.ScanHistory
import com.example.mediverify.ui.theme.AmberWarning
import com.example.mediverify.ui.theme.GreenSuccess
import com.example.mediverify.ui.theme.MintLight
import com.example.mediverify.ui.theme.RedWarning
import com.example.mediverify.ui.theme.TealPrimary
import com.example.mediverify.ui.theme.White
import com.example.mediverify.ui.viewmodel.MediVerifyViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: MediVerifyViewModel) {
    val recentScans by viewModel.recentScans.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAF9))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(TealPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.size(12.dp))
                Column {
                    Text("MediVerify", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("Verify before you trust.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF475569))
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = TealPrimary)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Verify Before You Trust.", color = White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Check batch numbers, expiry dates, and recall warnings using a local demonstration dataset.", color = White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.navigate("scan") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Scan Medicine")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { navController.navigate("manual") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            containerColor = White.copy(alpha = 0.12f),
                            contentColor = White
                        )
                    ) {
                        Text("Enter Batch Number")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            viewModel.lookupManual("AUST R56789X", "AUST R56789X")
                            navController.navigate("result")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            containerColor = White.copy(alpha = 0.12f),
                            contentColor = White
                        )
                    ) {
                        Text("Load Valofol IV sample")
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                InfoCard(title = "Batch verification", text = "Match against a trusted local dataset.", tint = MintLight, modifier = Modifier.weight(1f))
                InfoCard(title = "Expiry checks", text = "Flag expired medicines automatically.", tint = AmberWarning.copy(alpha = 0.2f), modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                InfoCard(title = "Recall alerts", text = "Surface recall and counterfeit warnings.", tint = RedWarning.copy(alpha = 0.12f), modifier = Modifier.weight(1f))
                InfoCard(title = "Transparency", text = "Every result explains what was matched.", tint = GreenSuccess.copy(alpha = 0.12f), modifier = Modifier.weight(1f))
            }
        }

        item {
            Text("Recent scans", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        if (recentScans.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("No scans yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("A scan result will appear here after you verify a batch.", color = Color(0xFF475569))
                    }
                }
            }
        } else {
            items(recentScans) { item ->
                RecentScanRow(item)
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, text: String, tint: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = tint)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF334155))
        }
    }
}

@Composable
private fun RecentScanRow(item: ScanHistory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(item.medicineName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                StatusChip(item.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.batchNumber, color = Color(0xFF475569))
        }
    }
}

@Composable
private fun StatusChip(label: String) {
    val color = when {
        label.contains("RECALLED", ignoreCase = true) || label.contains("COUNTERFEIT", ignoreCase = true) -> RedWarning
        label.contains("EXPIRED", ignoreCase = true) -> AmberWarning
        label.contains("VERIFIED", ignoreCase = true) -> GreenSuccess
        else -> Color(0xFF64748B)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(label, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
    }
}
