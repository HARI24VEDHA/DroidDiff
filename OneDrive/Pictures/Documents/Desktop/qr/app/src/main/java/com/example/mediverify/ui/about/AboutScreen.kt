package com.example.mediverify.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AboutScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAF9))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("About MediVerify", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("MediVerify is designed to help you compare medicine batch information against a local demonstration dataset.", color = Color(0xFF475569))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("How to use", fontWeight = FontWeight.Bold)
                Text("• Scan a barcode or QR code from a medicine pack.")
                Text("• Enter a batch number manually when scanning is not possible.")
                Text("• Check the result dashboard for status details and limitations.")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Status meaning", fontWeight = FontWeight.Bold)
                Text("VERIFIED — NO KNOWN ALERT: the record matches locally and no alert is present.")
                Text("EXPIRED: the expiry date passed before today's date.")
                Text("RECALLED: the batch is marked as recalled in the demonstration dataset.")
                Text("UNKNOWN / UNVERIFIED: the data could not be confirmed reliably.")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Important limitation", fontWeight = FontWeight.Bold)
                Text("This application uses a bundled demonstration dataset and does not connect to live government databases or regulator feeds. A missing record does not prove a medicine is counterfeit.")
            }
        }
    }
}
