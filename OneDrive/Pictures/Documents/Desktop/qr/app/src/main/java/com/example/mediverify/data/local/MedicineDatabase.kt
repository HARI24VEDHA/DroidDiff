package com.example.mediverify.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mediverify.data.model.MedicineBatch
import com.example.mediverify.data.model.ScanHistory

@Database(
    entities = [MedicineBatch::class, ScanHistory::class],
    version = 1,
    exportSchema = false
)
abstract class MedicineDatabase : RoomDatabase() {
    abstract fun medicineBatchDao(): MedicineBatchDao
    abstract fun scanHistoryDao(): ScanHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: MedicineDatabase? = null

        fun getDatabase(context: Context): MedicineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedicineDatabase::class.java,
                    "medicine_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
