package com.example.mediverify

import android.app.Application
import com.example.mediverify.data.local.MedicineDatabase
import com.example.mediverify.data.repository.MedicineRepository
import com.example.mediverify.data.repository.ScanHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MediVerifyApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: MedicineDatabase by lazy { MedicineDatabase.getDatabase(this) }
    val medicineRepository: MedicineRepository by lazy {
        MedicineRepository(
            medicineDao = database.medicineBatchDao(),
            historyDao = database.scanHistoryDao(),
            context = this
        )
    }
    val scanHistoryRepository: ScanHistoryRepository by lazy {
        ScanHistoryRepository(database.scanHistoryDao())
    }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            medicineRepository.ensureSeeded()
        }
    }
}
