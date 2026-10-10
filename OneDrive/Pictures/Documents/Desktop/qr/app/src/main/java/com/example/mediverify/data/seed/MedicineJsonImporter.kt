package com.example.mediverify.data.seed

import android.content.Context
import com.example.mediverify.data.local.MedicineBatchDao
import com.example.mediverify.data.model.MedicineBatch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader

class MedicineJsonImporter(private val context: Context) {
    suspend fun seedIfEmpty(dao: MedicineBatchDao) {
        if (dao.count() > 0) return

        val json = context.assets.open("medicine_batches.json").bufferedReader().use(BufferedReader::readText)
        val root = JSONArray(json)
        val records = mutableListOf<MedicineBatch>()

        for (i in 0 until root.length()) {
            val item = root.getJSONObject(i)
            records.add(
                MedicineBatch(
                    medicineName = item.optString("medicineName", "Unknown medicine"),
                    manufacturer = item.optString("manufacturer", "Unknown manufacturer"),
                    productIdentifier = item.optString("productIdentifier", "").ifBlank { null },
                    batchNumber = item.optString("batchNumber", "UNKNOWN"),
                    manufacturingDate = item.optString("manufacturingDate", "").ifBlank { null },
                    expiryDate = item.optString("expiryDate", "").ifBlank { null },
                    registrationRef = item.optString("registrationRef", "").ifBlank { null },
                    recallStatus = item.optBoolean("recallStatus", false),
                    verificationStatus = item.optString("verificationStatus", "demo_dataset"),
                    trustedDatasetSource = item.optString("trustedDatasetSource", "Demonstration dataset"),
                    lastUpdated = item.optString("lastUpdated", "2026-01-01"),
                    counterfeitFlag = item.optBoolean("counterfeitFlag", false),
                    dataConflict = item.optBoolean("dataConflict", false),
                    incompleteData = item.optBoolean("incompleteData", false),
                )
            )
        }

        if (records.isNotEmpty()) {
            dao.insertAll(records)
        }
    }
}
