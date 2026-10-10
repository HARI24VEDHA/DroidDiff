package com.example.mediverify.domain

import com.example.mediverify.data.model.MedicineBatch
import org.junit.Assert.assertEquals
import org.junit.Test

class VerificationEngineTest {
    private val engine = VerificationEngine()

    @Test
    fun expiredBatchIsClassifiedAsExpired() {
        val batch = MedicineBatch(
            medicineName = "Expired Sample",
            manufacturer = "Demo Pharma",
            batchNumber = "EXP-001",
            expiryDate = "2020-01-01",
            verificationStatus = "trusted_demo_data",
            trustedDatasetSource = "Local demonstration dataset"
        )

        val result = engine.verify(batch)
        assertEquals(VerificationStatus.EXPIRED, result.status)
    }

    @Test
    fun recalledBatchTakesPriorityOverVerifiedStatus() {
        val batch = MedicineBatch(
            medicineName = "Recall Sample",
            manufacturer = "Demo Pharma",
            batchNumber = "RECALL-001",
            expiryDate = "2028-01-01",
            recallStatus = true,
            verificationStatus = "trusted_demo_data",
            trustedDatasetSource = "Local demonstration dataset"
        )

        val result = engine.verify(batch)
        assertEquals(VerificationStatus.RECALLED, result.status)
    }

    @Test
    fun unknownBatchRemainsUnverified() {
        val result = engine.verify(null)
        assertEquals(VerificationStatus.UNKNOWN_UNVERIFIED, result.status)
    }
}
