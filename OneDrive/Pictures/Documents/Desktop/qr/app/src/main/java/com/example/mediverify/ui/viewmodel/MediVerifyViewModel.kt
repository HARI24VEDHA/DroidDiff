package com.example.mediverify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mediverify.data.model.ScanHistory
import com.example.mediverify.data.repository.MedicineRepository
import com.example.mediverify.domain.VerificationEngine
import com.example.mediverify.domain.VerificationResult
import com.example.mediverify.domain.VerificationStatus
import com.example.mediverify.data.model.MedicineBatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BatchLookupState(
    val isLoading: Boolean = false,
    val batchNumber: String = "",
    val errorMessage: String? = null,
)

class MediVerifyViewModel(
    private val repository: MedicineRepository,
) : ViewModel() {

    private val _recentScans = MutableStateFlow<List<ScanHistory>>(emptyList())
    val recentScans: StateFlow<List<ScanHistory>> = _recentScans

    private val _currentResult = MutableStateFlow<VerificationResult?>(null)
    val currentResult: StateFlow<VerificationResult?> = _currentResult

    private val _lookupState = MutableStateFlow(BatchLookupState())
    val lookupState: StateFlow<BatchLookupState> = _lookupState

    private var lastLookupBatch = ""
    private val verificationEngine = VerificationEngine()

    init {
        refreshHistory()
    }

    fun refreshHistory() {
        viewModelScope.launch {
            _recentScans.value = repository.getRecentHistory()
        }
    }

    fun lookupFromBarcode(rawValue: String) {
        lookupLive(rawValue)
    }

    fun lookupManual(productIdentifier: String, batchNumber: String) {
        lookupLive(batchNumber)
    }

    fun retryLookup() {
        if (lastLookupBatch.isNotBlank()) lookupLive(lastLookupBatch)
    }

    private fun lookupLive(batchNumber: String) {
        val cleanBatch = batchNumber.trim()
        lastLookupBatch = cleanBatch
        _currentResult.value = null
        _lookupState.value = BatchLookupState(isLoading = true, batchNumber = cleanBatch)
        viewModelScope.launch {
            try {
                val rows = repository.lookupLiveBatch(cleanBatch)
                val result = when {
                    rows.isEmpty() -> verificationEngine.verify(null).copy(
                        batchNumber = cleanBatch,
                        details = "No matching fictional demonstration record was found in Supabase."
                    )
                    rows.size > 1 -> verificationEngine.verify(null).copy(
                        batchNumber = cleanBatch,
                        title = "UNKNOWN / CONFLICTING RECORDS",
                        details = "Multiple demo records share this batch number; the result is unverified."
                    )
                    else -> {
                        val row = rows.single()
                        val normalizedStatus = row.status.trim().lowercase()
                        val recalled = "recall" in normalizedStatus
                        val counterfeit = "counterfeit" in normalizedStatus
                        val trustedStatus = normalizedStatus in setOf(
                            "active", "approved", "clear", "registered", "valid", "verified", "no known alert"
                        )
                        val expiredStatus = "expired" in normalizedStatus
                        val record = MedicineBatch(
                            medicineName = row.medicineName,
                            manufacturer = row.manufacturer,
                            productIdentifier = row.productIdentifier,
                            batchNumber = row.batchNumber,
                            manufacturingDate = row.manufacturingDate,
                            expiryDate = row.expiryDate,
                            recallStatus = recalled,
                            counterfeitFlag = counterfeit,
                            verificationStatus = if (trustedStatus || recalled || counterfeit || expiredStatus) {
                                "trusted_live_demo_record"
                            } else {
                                "unverified_live_demo_record"
                            },
                            trustedDatasetSource = "Supabase fictional demo records",
                            incompleteData = row.medicineName.isBlank() ||
                                row.batchNumber.isBlank() ||
                                row.expiryDate.isNullOrBlank() ||
                                row.status.isBlank()
                        )
                        verificationEngine.verify(record).copy(
                            batchNumber = row.batchNumber.ifBlank { cleanBatch },
                            sourceStatus = row.status.ifBlank { "Unknown status" },
                            details = "Supabase fictional demo record. A database match is not proof of real-world authenticity."
                        )
                    }
                }
                _currentResult.value = result
                _lookupState.value = BatchLookupState(batchNumber = cleanBatch)
                repository.saveHistory(
                    ScanHistory(
                        medicineName = result.medicineName,
                        manufacturer = result.manufacturer,
                        batchNumber = result.batchNumber,
                        status = result.title,
                        verificationDetails = result.details,
                        productIdentifier = result.productIdentifier,
                        expiryDate = result.expiryDate,
                    )
                )
                refreshHistory()
            } catch (error: Exception) {
                _lookupState.value = BatchLookupState(
                    batchNumber = cleanBatch,
                    errorMessage = error.message ?: "Could not reach Supabase. Check the network and retry."
                )
            }
        }
    }

    fun setResult(result: VerificationResult) {
        _currentResult.value = result
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            refreshHistory()
        }
    }

    companion object {
        fun Factory(repository: MedicineRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MediVerifyViewModel(repository) as T
            }
        }
    }
}
