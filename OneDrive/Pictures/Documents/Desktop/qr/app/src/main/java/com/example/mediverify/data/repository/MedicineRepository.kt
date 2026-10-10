package com.example.mediverify.data.repository

import android.content.Context
import android.net.Uri
import com.example.mediverify.BuildConfig
import com.example.mediverify.data.local.MedicineBatchDao
import com.example.mediverify.data.local.ScanHistoryDao
import com.example.mediverify.data.model.MedicineBatch
import com.example.mediverify.data.model.ScanHistory
import com.example.mediverify.data.seed.MedicineJsonImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class LiveMedicineRecord(
    val medicineName: String,
    val manufacturer: String,
    val productIdentifier: String?,
    val batchNumber: String,
    val manufacturingDate: String?,
    val expiryDate: String?,
    val status: String,
    val isDemo: Boolean,
)

class MedicineRepository(
    private val medicineDao: MedicineBatchDao,
    private val historyDao: ScanHistoryDao,
    private val context: Context,
) {
    private val importer = MedicineJsonImporter(context)

    suspend fun lookupLiveBatch(batchNumber: String): List<LiveMedicineRecord> = withContext(Dispatchers.IO) {
        val cleanBatch = batchNumber.trim()
        if (cleanBatch.isBlank()) return@withContext emptyList()
        check(BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_PUBLISHABLE_KEY.isNotBlank()) {
            "Supabase is not configured. Add supabase.properties in the project root."
        }

        val endpoint = Uri.parse("${BuildConfig.SUPABASE_URL.trimEnd('/')}/rest/v1/medicine_batches")
            .buildUpon()
            .appendQueryParameter("batch_number", "eq.$cleanBatch")
            .appendQueryParameter("is_demo", "eq.true")
            .appendQueryParameter(
                "select",
                "medicine_name,manufacturer,product_identifier,batch_number,manufacturing_date,expiry_date,status,is_demo"
            )
            .appendQueryParameter("limit", "2")
            .build()
        val connection = (URL(endpoint.toString()).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
            setRequestProperty("apikey", BuildConfig.SUPABASE_PUBLISHABLE_KEY)
            setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_PUBLISHABLE_KEY}")
            setRequestProperty("Accept", "application/json")
        }

        try {
            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                val message = connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                throw IOException("Supabase request failed ($responseCode): $message")
            }
            val rows = JSONArray(connection.inputStream.bufferedReader().use { it.readText() })
            (0 until rows.length()).map { index ->
                val row = rows.getJSONObject(index)
                LiveMedicineRecord(
                    medicineName = row.optString("medicine_name"),
                    manufacturer = row.optString("manufacturer"),
                    productIdentifier = row.optString("product_identifier").takeIf(String::isNotBlank),
                    batchNumber = row.optString("batch_number"),
                    manufacturingDate = row.optString("manufacturing_date").takeIf(String::isNotBlank),
                    expiryDate = row.optString("expiry_date").takeIf(String::isNotBlank),
                    status = row.optString("status"),
                    isDemo = row.optBoolean("is_demo")
                )
            }
        } finally {
            connection.disconnect()
        }
    }

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        importer.seedIfEmpty(medicineDao)
    }

    suspend fun lookupByScanValue(rawValue: String): MedicineBatch? = withContext(Dispatchers.IO) {
        val normalized = rawValue.trim()
        if (normalized.isBlank()) return@withContext null

        medicineDao.findByBatchNumber(normalized)
            ?: medicineDao.findByProductAndBatch(normalized, normalized)
            ?: medicineDao.findByBatchOrProduct(normalized, normalized).firstOrNull()
            ?: medicineDao.recentBatches().firstOrNull { batch ->
                val candidateText = listOfNotNull(
                    batch.medicineName,
                    batch.batchNumber,
                    batch.productIdentifier,
                    batch.registrationRef,
                    batch.manufacturer
                )

                candidateText.any { candidate ->
                    candidate.equals(normalized, ignoreCase = true) ||
                        normalized.equals(candidate, ignoreCase = true) ||
                        candidate.contains(normalized, ignoreCase = true) ||
                        normalized.contains(candidate, ignoreCase = true)
                }
            }
    }

    suspend fun lookupByBatchAndProduct(productIdentifier: String?, batchNumber: String): MedicineBatch? = withContext(Dispatchers.IO) {
        val cleanProduct = productIdentifier?.trim().orEmpty()
        val cleanBatch = batchNumber.trim()
        if (cleanBatch.isBlank()) return@withContext null
        if (cleanProduct.isNotBlank()) {
            medicineDao.findByProductAndBatch(cleanProduct, cleanBatch)
        } else {
            medicineDao.findByBatchNumber(cleanBatch)
        }
    }

    suspend fun saveHistory(scanHistory: ScanHistory) = withContext(Dispatchers.IO) {
        historyDao.insert(scanHistory)
    }

    suspend fun getRecentHistory(): List<ScanHistory> = withContext(Dispatchers.IO) {
        historyDao.getRecentHistory()
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        historyDao.clearAll()
    }
}
