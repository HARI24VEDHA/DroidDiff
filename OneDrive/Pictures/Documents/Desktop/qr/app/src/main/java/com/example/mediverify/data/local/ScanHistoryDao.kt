package com.example.mediverify.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mediverify.data.model.ScanHistory

@Dao
interface ScanHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: ScanHistory): Long

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC LIMIT 10")
    suspend fun getRecentHistory(): List<ScanHistory>

    @Query("DELETE FROM scan_history")
    suspend fun clearAll()
}
