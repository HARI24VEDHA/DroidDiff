package com.example.mediverify.domain

import com.example.mediverify.data.model.MedicineBatch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class VerificationEngine {
    fun verify(batch: MedicineBatch?): VerificationResult {
        if (batch == null) {
            return VerificationResult(
                status = VerificationStatus.UNKNOWN_UNVERIFIED,
                title = "UNKNOWN / UNVERIFIED",
                message = "The local dataset could not establish a reliable match for this batch.",
                medicineName = "Unknown medicine",
                batchNumber = "Unknown batch",
                details = "No matching record was found in the local demonstration dataset. This does not prove the medicine is counterfeit; it simply means the product could not be confirmed."
            )
        }

        if (batch.dataConflict || batch.incompleteData) {
            return VerificationResult(
                status = VerificationStatus.UNVERIFIED_DATA_CONFLICT,
                title = "UNVERIFIED — DATA CONFLICT",
                message = "The available data for this batch is incomplete or conflicting.",
                medicineName = batch.medicineName,
                batchNumber = batch.batchNumber,
                manufacturer = batch.manufacturer,
                expiryDate = batch.expiryDate,
                productIdentifier = batch.productIdentifier,
                registrationRef = batch.registrationRef,
                trustedDatasetSource = batch.trustedDatasetSource,
                lastUpdated = batch.lastUpdated,
                details = "This batch contains fields that do not agree or are incomplete, so the local dataset cannot reliably verify it.",
                record = batch
            )
        }

        val expiryDate = parseDate(batch.expiryDate)
        val isExpired = (expiryDate != null && expiryDate.isBefore(LocalDate.now())) ||
            batch.verificationStatus.contains("expired", ignoreCase = true)
        val warnings = mutableListOf<String>()

        if (batch.recallStatus) {
            warnings.add("The batch is listed as recalled in the local demonstration dataset.")
        }
        if (batch.counterfeitFlag) {
            warnings.add("The batch is flagged as counterfeit in the trusted demonstration dataset.")
        }
        if (isExpired) {
            warnings.add("The batch has expired and should not be used.")
        }

        when {
            batch.recallStatus -> return VerificationResult(
                status = VerificationStatus.RECALLED,
                title = "RECALLED",
                message = "This batch is recalled. Do not use the medicine and consult a pharmacist or healthcare professional.",
                medicineName = batch.medicineName,
                batchNumber = batch.batchNumber,
                manufacturer = batch.manufacturer,
                expiryDate = batch.expiryDate,
                productIdentifier = batch.productIdentifier,
                registrationRef = batch.registrationRef,
                trustedDatasetSource = batch.trustedDatasetSource,
                lastUpdated = batch.lastUpdated,
                details = warnings.joinToString(" "),
                record = batch
            )
            batch.counterfeitFlag -> return VerificationResult(
                status = VerificationStatus.COUNTERFEIT_FLAGGED,
                title = "COUNTERFEIT FLAGGED",
                message = "This batch is flagged as counterfeit or unverifiable in the local demonstration dataset.",
                medicineName = batch.medicineName,
                batchNumber = batch.batchNumber,
                manufacturer = batch.manufacturer,
                expiryDate = batch.expiryDate,
                productIdentifier = batch.productIdentifier,
                registrationRef = batch.registrationRef,
                trustedDatasetSource = batch.trustedDatasetSource,
                lastUpdated = batch.lastUpdated,
                details = warnings.joinToString(" "),
                record = batch
            )
            isExpired -> return VerificationResult(
                status = VerificationStatus.EXPIRED,
                title = "EXPIRED",
                message = "This batch has expired and should not be used.",
                medicineName = batch.medicineName,
                batchNumber = batch.batchNumber,
                manufacturer = batch.manufacturer,
                expiryDate = batch.expiryDate,
                productIdentifier = batch.productIdentifier,
                registrationRef = batch.registrationRef,
                trustedDatasetSource = batch.trustedDatasetSource,
                lastUpdated = batch.lastUpdated,
                details = warnings.joinToString(" "),
                record = batch
            )
            batch.verificationStatus.contains("trusted", ignoreCase = true) && !batch.recallStatus && !batch.counterfeitFlag && !isExpired -> return VerificationResult(
                status = VerificationStatus.VERIFIED_NO_KNOWN_ALERT,
                title = "VERIFIED — NO KNOWN ALERT",
                message = "This matched record has no known recall, counterfeit flag, or expiry alert in the available local dataset.",
                medicineName = batch.medicineName,
                batchNumber = batch.batchNumber,
                manufacturer = batch.manufacturer,
                expiryDate = batch.expiryDate,
                productIdentifier = batch.productIdentifier,
                registrationRef = batch.registrationRef,
                trustedDatasetSource = batch.trustedDatasetSource,
                lastUpdated = batch.lastUpdated,
                details = "This is a local demonstration-data match only and does not guarantee authenticity. It means no known alert was found in the available dataset.",
                record = batch
            )
            else -> return VerificationResult(
                status = VerificationStatus.UNKNOWN_UNVERIFIED,
                title = "UNKNOWN / UNVERIFIED",
                message = "The local dataset could not establish a reliable match for this batch.",
                medicineName = batch.medicineName,
                batchNumber = batch.batchNumber,
                manufacturer = batch.manufacturer,
                expiryDate = batch.expiryDate,
                productIdentifier = batch.productIdentifier,
                registrationRef = batch.registrationRef,
                trustedDatasetSource = batch.trustedDatasetSource,
                lastUpdated = batch.lastUpdated,
                details = "The record is incomplete or the batch could not be confirmed from the local demonstration dataset.",
                record = batch
            )
        }
    }

    private fun parseDate(rawValue: String?): LocalDate? {
        if (rawValue.isNullOrBlank()) return null
        return try {
            LocalDate.parse(rawValue.trim(), DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (_: DateTimeParseException) {
            null
        }
    }
}
