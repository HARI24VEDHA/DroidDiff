package com.example.mediverify.domain

import com.example.mediverify.data.model.MedicineBatch

enum class VerificationStatus {
    VERIFIED_NO_KNOWN_ALERT,
    EXPIRED,
    RECALLED,
    COUNTERFEIT_FLAGGED,
    UNKNOWN_UNVERIFIED,
    UNVERIFIED_DATA_CONFLICT
}

data class VerificationResult(
    val status: VerificationStatus,
    val title: String,
    val message: String,
    val medicineName: String,
    val batchNumber: String,
    val manufacturer: String? = null,
    val expiryDate: String? = null,
    val productIdentifier: String? = null,
    val registrationRef: String? = null,
    val trustedDatasetSource: String? = null,
    val lastUpdated: String? = null,
    val details: String = "",
    val record: MedicineBatch? = null,
    val sourceStatus: String? = null,
)
