package com.example.mediverify.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medicine_batches",
    indices = [
        Index(value = ["batch_number"], unique = false),
        Index(value = ["product_identifier"], unique = false)
    ]
)
data class MedicineBatch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "medicine_name") val medicineName: String,
    @ColumnInfo(name = "manufacturer") val manufacturer: String,
    @ColumnInfo(name = "product_identifier") val productIdentifier: String? = null,
    @ColumnInfo(name = "batch_number") val batchNumber: String,
    @ColumnInfo(name = "manufacturing_date") val manufacturingDate: String? = null,
    @ColumnInfo(name = "expiry_date") val expiryDate: String? = null,
    @ColumnInfo(name = "registration_ref") val registrationRef: String? = null,
    @ColumnInfo(name = "recall_status") val recallStatus: Boolean = false,
    @ColumnInfo(name = "verification_status") val verificationStatus: String = "demo_dataset",
    @ColumnInfo(name = "trusted_dataset_source") val trustedDatasetSource: String = "Demonstration dataset",
    @ColumnInfo(name = "last_updated") val lastUpdated: String = "2026-01-01",
    @ColumnInfo(name = "counterfeit_flag") val counterfeitFlag: Boolean = false,
    @ColumnInfo(name = "data_conflict") val dataConflict: Boolean = false,
    @ColumnInfo(name = "incomplete_data") val incompleteData: Boolean = false,
)
