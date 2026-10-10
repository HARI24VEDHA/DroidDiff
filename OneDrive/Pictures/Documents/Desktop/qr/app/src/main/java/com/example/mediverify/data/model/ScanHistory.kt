package com.example.mediverify.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "medicine_name") val medicineName: String,
    @ColumnInfo(name = "manufacturer") val manufacturer: String? = null,
    @ColumnInfo(name = "batch_number") val batchNumber: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "verification_details") val verificationDetails: String? = null,
    @ColumnInfo(name = "product_identifier") val productIdentifier: String? = null,
    @ColumnInfo(name = "expiry_date") val expiryDate: String? = null,
)
