package com.example.mediverify.ui.result

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.mediverify.domain.VerificationStatus
import com.example.mediverify.ui.theme.AmberWarning
import com.example.mediverify.ui.theme.GreenSuccess
import com.example.mediverify.ui.theme.RedWarning
import com.example.mediverify.ui.theme.TealPrimary
import com.example.mediverify.ui.theme.White
import com.example.mediverify.ui.viewmodel.MediVerifyViewModel

@Composable
fun VerificationResultScreen(navController: NavController, viewModel: MediVerifyViewModel) {
    val result by viewModel.currentResult.collectAsState()
    val lookupState by viewModel.lookupState.collectAsState()

    if (result == null) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF7FAF9)).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                lookupState.isLoading -> {
                    CircularProgressIndicator(color = TealPrimary)
                    Text("Checking fictional demo records…", modifier = Modifier.padding(top = 16.dp))
                }
                lookupState.errorMessage != null -> {
                    Text("LIVE LOOKUP FAILED", color = RedWarning, fontWeight = FontWeight.Bold)
                    Text(lookupState.errorMessage.orEmpty(), modifier = Modifier.padding(vertical = 12.dp))
                    Button(onClick = viewModel::retryLookup) { Text("Retry") }
                }
                else -> Text("No result has been generated yet.")
            }
        }
        return
    }

    val statusColor = when (result!!.status) {
        VerificationStatus.VERIFIED_NO_KNOWN_ALERT -> GreenSuccess
        VerificationStatus.EXPIRED, VerificationStatus.RECALLED, VerificationStatus.COUNTERFEIT_FLAGGED -> RedWarning
        VerificationStatus.UNKNOWN_UNVERIFIED, VerificationStatus.UNVERIFIED_DATA_CONFLICT -> AmberWarning
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAF9))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.12f))
        ) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(statusColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text("!", color = White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(result!!.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(result!!.message, color = Color(0xFF334155), style = MaterialTheme.typography.bodyLarge)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(result!!.medicineName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Batch: ${result!!.batchNumber}")
                Text("Manufacturer: ${result!!.manufacturer ?: "Unavailable"}")
                Text("Expiry: ${result!!.expiryDate ?: "Unknown"}")
                Text("Status: ${result!!.sourceStatus ?: result!!.title}")
                Text("Record source: ${result!!.trustedDatasetSource ?: "Supabase fictional demo records"}")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Verification details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(result!!.details.ifBlank { "No extra details available." }, color = Color(0xFF475569))
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { navController.navigate("scan") { popUpTo("home") { inclusive = false } } }, modifier = Modifier.weight(1f)) {
                Text("Scan Another")
            }
            Button(onClick = { navController.navigate("home") }, modifier = Modifier.weight(1f)) {
                Text("Home")
            }
        }
    }
}
