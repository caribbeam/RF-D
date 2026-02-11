package com.warehouse.rfid.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Ürün veritabanı erişim nesnesi (DAO)
 */
@Dao
interface ProductDao {
    
    // Ürün işlemleri
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long
    
    @Update
    suspend fun updateProduct(product: ProductEntity)
    
    @Delete
    suspend fun deleteProduct(product: ProductEntity)
    
    @Query("SELECT * FROM products ORDER BY created_at DESC")
    fun getAllProducts(): LiveData<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Long): ProductEntity?
    
    @Query("SELECT * FROM products WHERE rfid_tag = :rfidTag")
    suspend fun getProductByRfidTag(rfidTag: String): ProductEntity?
    
    @Query("SELECT * FROM products WHERE barcode = :barcode")
    suspend fun getProductByBarcode(barcode: String): ProductEntity?
    
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%'")
    fun searchProducts(query: String): LiveData<List<ProductEntity>>
    
    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
    
    @Query("SELECT * FROM products WHERE synced = 0")
    suspend fun getUnsyncedProducts(): List<ProductEntity>
    
    @Query("UPDATE products SET synced = 1 WHERE id = :productId")
    suspend fun markProductAsSynced(productId: Long)
    
    // Stok hareketi işlemleri
    @Insert
    suspend fun insertStockMovement(movement: StockMovementEntity): Long
    
    @Query("SELECT * FROM stock_movements WHERE product_id = :productId ORDER BY timestamp DESC")
    fun getProductMovements(productId: Long): LiveData<List<StockMovementEntity>>
    
    @Query("SELECT * FROM stock_movements ORDER BY timestamp DESC LIMIT 100")
    fun getRecentMovements(): LiveData<List<StockMovementEntity>>
    
    @Query("SELECT * FROM stock_movements WHERE synced = 0")
    suspend fun getUnsyncedMovements(): List<StockMovementEntity>
    
    @Query("UPDATE stock_movements SET synced = 1 WHERE id = :movementId")
    suspend fun markMovementAsSynced(movementId: Long)
    
    // RFID okuma kayıtları
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRFIDRead(read: RFIDReadEntity)
    
    @Query("SELECT * FROM rfid_reads ORDER BY timestamp DESC LIMIT 100")
    fun getRecentRFIDReads(): LiveData<List<RFIDReadEntity>>
    
    @Query("DELETE FROM rfid_reads WHERE timestamp < :timestamp")
    suspend fun deleteOldRFIDReads(timestamp: Long)
    
    // İstatistikler
    @Query("SELECT SUM(quantity) FROM products")
    suspend fun getTotalQuantity(): Int?
    
    @Query("SELECT COUNT(DISTINCT location) FROM products WHERE location IS NOT NULL")
    suspend fun getLocationCount(): Int
}
