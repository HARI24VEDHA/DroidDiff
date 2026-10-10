package com.example.mediverify.data.repository

import com.example.mediverify.data.local.ScanHistoryDao
import com.example.mediverify.data.model.ScanHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScanHistoryRepository(private val scanHistoryDao: ScanHistoryDao) {
    suspend fun getRecentHistory(): List<ScanHistory> = withContext(Dispatchers.IO) {
        scanHistoryDao.getRecentHistory()
    }

    suspend fun addHistoryItem(history: ScanHistory) = withContext(Dispatchers.IO) {
        scanHistoryDao.insert(history)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        scanHistoryDao.clearAll()
    }
}
