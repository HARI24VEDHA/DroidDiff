package com.example.mediverify.ui.manual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mediverify.ui.viewmodel.MediVerifyViewModel

@Composable
fun ManualEntryScreen(navController: NavController, viewModel: MediVerifyViewModel) {
    var productIdentifier by remember { mutableStateOf("") }
    var batchNumber by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAF9))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Manual batch lookup", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Enter a medicine identifier or batch number to compare it against the stored dataset.", color = Color(0xFF475569))

        Button(
            onClick = {
                productIdentifier = "AUST R56789X"
                batchNumber = "AUST R56789X"
                validationError = ""
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Use attached Valofol IV sample label")
        }

        OutlinedTextField(
            value = productIdentifier,
            onValueChange = { productIdentifier = it },
            label = { Text("Product identifier (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = batchNumber,
            onValueChange = { batchNumber = it },
            label = { Text("Batch number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (validationError.isNotEmpty()) {
            Text(validationError, color = Color(0xFFDC2626))
        }

        Button(
            onClick = {
                val normalizedProduct = productIdentifier.trim()
                val normalizedBatch = batchNumber.trim()
                when {
                    normalizedBatch.isBlank() -> validationError = "Enter a batch number to continue."
                    normalizedBatch.length < 3 -> validationError = "Batch numbers are too short to be reliable."
                    else -> {
                        validationError = ""
                        viewModel.lookupManual(normalizedProduct, normalizedBatch)
                        navController.navigate("result")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Check batch")
        }
    }
}
