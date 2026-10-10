package com.example.mediverify.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mediverify.data.model.MedicineBatch

@Dao
interface MedicineBatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(batch: MedicineBatch): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(batches: List<MedicineBatch>)

    @Query("SELECT * FROM medicine_batches WHERE batch_number = :batchNumber LIMIT 1")
    suspend fun findByBatchNumber(batchNumber: String): MedicineBatch?

    @Query("SELECT * FROM medicine_batches WHERE product_identifier = :productIdentifier AND batch_number = :batchNumber LIMIT 1")
    suspend fun findByProductAndBatch(productIdentifier: String, batchNumber: String): MedicineBatch?

    @Query("SELECT * FROM medicine_batches WHERE batch_number = :batchNumber OR product_identifier = :productIdentifier ORDER BY id DESC LIMIT 20")
    suspend fun findByBatchOrProduct(batchNumber: String, productIdentifier: String): List<MedicineBatch>

    @Query("SELECT COUNT(*) FROM medicine_batches")
    suspend fun count(): Int

    @Query("SELECT * FROM medicine_batches ORDER BY id DESC LIMIT 20")
    suspend fun recentBatches(): List<MedicineBatch>
}
