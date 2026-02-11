package com.warehouse.rfid.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Ürün veritabanı erişim nesnesi (DAO)
 * Tüm veritabanı işlemleri için fonksiyonlar
 */
@Dao
interface ProductDao {
    
    // ==================== TEMEL İŞLEMLER ====================
    
    /**
     * Yeni ürün ekle
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity): Long
    
    /**
     * Ürünü güncelle
     */
    @Update
    suspend fun update(product: ProductEntity)
    
    /**
     * Ürünü sil
     */
    @Delete
    suspend fun delete(product: ProductEntity)
    
    /**
     * Tüm ürünleri sil
     */
    @Query("DELETE FROM products")
    suspend fun deleteAll()
    
    // ==================== SORGULAMA İŞLEMLERİ ====================
    
    /**
     * Tüm ürünleri getir (Flow ile reaktif)
     */
    @Query("SELECT * FROM products ORDER BY updated_at DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>
    
    /**
     * Tüm ürünleri getir (suspend)
     */
    @Query("SELECT * FROM products ORDER BY updated_at DESC")
    suspend fun getAllProductsList(): List<ProductEntity>
    
    /**
     * ID ile ürün bul
     */
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?
    
    /**
     * Ürün kodu ile bul (KRİTİK)
     */
    @Query("SELECT * FROM products WHERE product_code = :code")
    suspend fun findByProductCode(code: String): ProductEntity?
    
    /**
     * Ürün kodu ile bul (Flow)
     */
    @Query("SELECT * FROM products WHERE product_code = :code")
    fun findByProductCodeFlow(code: String): Flow<ProductEntity?>
    
    /**
     * RFID tag ile ürün bul (KRİTİK)
     */
    @Query("SELECT * FROM products WHERE rfid_tag = :tag")
    suspend fun findByRFIDTag(tag: String): ProductEntity?
    
    /**
     * Barkod ile ürün bul
     */
    @Query("SELECT * FROM products WHERE barcode = :barcode")
    suspend fun findByBarcode(barcode: String): ProductEntity?
    
    // ==================== ARAMA İŞLEMLERİ ====================
    
    /**
     * İsme göre arama
     */
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' ORDER BY name")
    suspend fun searchByName(query: String): List<ProductEntity>
    
    /**
     * Ürün koduna göre arama
     */
    @Query("SELECT * FROM products WHERE product_code LIKE '%' || :query || '%' ORDER BY product_code")
    suspend fun searchByProductCode(query: String): List<ProductEntity>
    
    /**
     * Genel arama (isim veya kod)
     */
    @Query("""
        SELECT * FROM products 
        WHERE name LIKE '%' || :query || '%' 
        OR product_code LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        ORDER BY name
    """)
    suspend fun searchProducts(query: String): List<ProductEntity>
    
    // ==================== KONUM İŞLEMLERİ ====================
    
    /**
     * Koridora göre filtrele
     */
    @Query("SELECT * FROM products WHERE corridor = :corridor ORDER BY shelf, level")
    suspend fun findByCorridor(corridor: String): List<ProductEntity>
    
    /**
     * Koridor ve rafa göre filtrele
     */
    @Query("SELECT * FROM products WHERE corridor = :corridor AND shelf = :shelf ORDER BY level")
    suspend fun findByLocation(corridor: String, shelf: String): List<ProductEntity>
    
    /**
     * Tam konuma göre filtrele
     */
    @Query("SELECT * FROM products WHERE corridor = :corridor AND shelf = :shelf AND level = :level")
    suspend fun findByFullLocation(corridor: String, shelf: String, level: String): List<ProductEntity>
    
    /**
     * Konumu olmayan ürünler
     */
    @Query("SELECT * FROM products WHERE corridor IS NULL OR shelf IS NULL OR level IS NULL")
    suspend fun getProductsWithoutLocation(): List<ProductEntity>
    
    // ==================== STOK İŞLEMLERİ ====================
    
    /**
     * Düşük stoklu ürünler
     */
    @Query("SELECT * FROM products WHERE quantity > 0 AND quantity <= min_stock_level ORDER BY quantity")
    suspend fun getLowStockProducts(): List<ProductEntity>
    
    /**
     * Düşük stoklu ürünler (Flow)
     */
    @Query("SELECT * FROM products WHERE quantity > 0 AND quantity <= min_stock_level ORDER BY quantity")
    fun getLowStockProductsFlow(): Flow<List<ProductEntity>>
    
    /**
     * Tükenen ürünler
     */
    @Query("SELECT * FROM products WHERE quantity = 0")
    suspend fun getOutOfStockProducts(): List<ProductEntity>
    
    /**
     * Tükenen ürünler (Flow)
     */
    @Query("SELECT * FROM products WHERE quantity = 0")
    fun getOutOfStockProductsFlow(): Flow<List<ProductEntity>>
    
    /**
     * Stokta olan ürünler
     */
    @Query("SELECT * FROM products WHERE quantity > 0 ORDER BY quantity DESC")
    suspend fun getInStockProducts(): List<ProductEntity>
    
    /**
     * Miktar güncelle
     */
    @Query("UPDATE products SET quantity = :newQuantity, updated_at = :timestamp WHERE id = :id")
    suspend fun updateQuantity(id: Long, newQuantity: Int, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Miktarı artır (KRİTİK - Aynı ürün geldiğinde)
     */
    @Query("UPDATE products SET quantity = quantity + :additionalQty, updated_at = :timestamp WHERE product_code = :code")
    suspend fun increaseQuantity(code: String, additionalQty: Int, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Miktarı azalt
     */
    @Query("UPDATE products SET quantity = quantity - :decreaseQty, updated_at = :timestamp WHERE product_code = :code")
    suspend fun decreaseQuantity(code: String, decreaseQty: Int, timestamp: Long = System.currentTimeMillis())
    
    // ==================== RFID İŞLEMLERİ ====================
    
    /**
     * RFID tag'i ürüne bağla (KRİTİK)
     */
    @Query("UPDATE products SET rfid_tag = :rfidTag, updated_at = :timestamp WHERE id = :productId")
    suspend fun linkRFIDToProduct(productId: Long, rfidTag: String, timestamp: Long = System.currentTimeMillis())
    
    /**
     * RFID tag'i ürün koduna bağla
     */
    @Query("UPDATE products SET rfid_tag = :rfidTag, updated_at = :timestamp WHERE product_code = :productCode")
    suspend fun linkRFIDToProductCode(productCode: String, rfidTag: String, timestamp: Long = System.currentTimeMillis())
    
    /**
     * RFID etiketi olan ürünler
     */
    @Query("SELECT * FROM products WHERE rfid_tag IS NOT NULL AND rfid_tag != ''")
    suspend fun getProductsWithRFID(): List<ProductEntity>
    
    /**
     * RFID etiketi olmayan ürünler
     */
    @Query("SELECT * FROM products WHERE rfid_tag IS NULL OR rfid_tag = ''")
    suspend fun getProductsWithoutRFID(): List<ProductEntity>
    
    // ==================== İSTATİSTİK İŞLEMLERİ ====================
    
    /**
     * Toplam ürün sayısı
     */
    @Query("SELECT COUNT(*) FROM products")
    suspend fun getTotalProductCount(): Int
    
    /**
     * Toplam stok miktarı
     */
    @Query("SELECT SUM(quantity) FROM products")
    suspend fun getTotalStockQuantity(): Int?
    
    /**
     * Düşük stok sayısı
     */
    @Query("SELECT COUNT(*) FROM products WHERE quantity > 0 AND quantity <= min_stock_level")
    suspend fun getLowStockCount(): Int
    
    /**
     * Tükenen ürün sayısı
     */
    @Query("SELECT COUNT(*) FROM products WHERE quantity = 0")
    suspend fun getOutOfStockCount(): Int
    
    /**
     * Koridora göre ürün sayısı
     */
    @Query("SELECT COUNT(*) FROM products WHERE corridor = :corridor")
    suspend fun getProductCountByCorridor(corridor: String): Int
    
    // ==================== BİRİM İŞLEMLERİ ====================
    
    /**
     * Birime göre filtrele
     */
    @Query("SELECT * FROM products WHERE unit = :unit ORDER BY name")
    suspend fun findByUnit(unit: String): List<ProductEntity>
    
    /**
     * Kullanılan tüm birimleri getir
     */
    @Query("SELECT DISTINCT unit FROM products ORDER BY unit")
    suspend fun getAllUnits(): List<String>
    
    // ==================== TOPLU İŞLEMLER ====================
    
    /**
     * Çoklu ürün ekle
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>): List<Long>
    
    /**
     * Çoklu ürün güncelle
     */
    @Update
    suspend fun updateAll(products: List<ProductEntity>)
    
    /**
     * Çoklu ürün sil
     */
    @Delete
    suspend fun deleteAll(products: List<ProductEntity>)
}

/**
 * Stok hareketi DAO
 */
@Dao
interface StockMovementDao {
    
    @Insert
    suspend fun insert(movement: StockMovementEntity): Long
    
    @Query("SELECT * FROM stock_movements ORDER BY timestamp DESC")
    fun getAllMovements(): Flow<List<StockMovementEntity>>
    
    @Query("SELECT * FROM stock_movements WHERE product_id = :productId ORDER BY timestamp DESC")
    suspend fun getMovementsByProduct(productId: Long): List<StockMovementEntity>
    
    @Query("SELECT * FROM stock_movements WHERE product_code = :productCode ORDER BY timestamp DESC")
    suspend fun getMovementsByProductCode(productCode: String): List<StockMovementEntity>
    
    @Query("SELECT * FROM stock_movements WHERE movement_type = :type ORDER BY timestamp DESC")
    suspend fun getMovementsByType(type: MovementType): List<StockMovementEntity>
    
    @Query("SELECT * FROM stock_movements WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    suspend fun getMovementsByDateRange(startTime: Long, endTime: Long): List<StockMovementEntity>
    
    @Query("DELETE FROM stock_movements")
    suspend fun deleteAll()
}

/**
 * RFID okuma DAO
 */
@Dao
interface RFIDReadDao {
    
    @Insert
    suspend fun insert(read: RFIDReadEntity): Long
    
    @Query("SELECT * FROM rfid_reads ORDER BY timestamp DESC LIMIT 100")
    fun getRecentReads(): Flow<List<RFIDReadEntity>>
    
    @Query("SELECT * FROM rfid_reads WHERE rfid_tag = :tag ORDER BY timestamp DESC")
    suspend fun getReadsByTag(tag: String): List<RFIDReadEntity>
    
    @Query("SELECT * FROM rfid_reads WHERE product_id = :productId ORDER BY timestamp DESC")
    suspend fun getReadsByProduct(productId: Long): List<RFIDReadEntity>
    
    @Query("SELECT COUNT(*) FROM rfid_reads WHERE timestamp >= :since")
    suspend fun getReadCountSince(since: Long): Int
    
    @Query("DELETE FROM rfid_reads WHERE timestamp < :before")
    suspend fun deleteOldReads(before: Long)
    
    @Query("DELETE FROM rfid_reads")
    suspend fun deleteAll()
}

/**
 * Envanter sayım DAO
 */
@Dao
interface InventoryCountDao {
    
    @Insert
    suspend fun insert(count: InventoryCountEntity): Long
    
    @Query("SELECT * FROM inventory_counts ORDER BY count_date DESC")
    fun getAllCounts(): Flow<List<InventoryCountEntity>>
    
    @Query("SELECT * FROM inventory_counts ORDER BY count_date DESC LIMIT 1")
    suspend fun getLastCount(): InventoryCountEntity?
    
    @Query("SELECT * FROM inventory_counts WHERE count_date BETWEEN :startDate AND :endDate ORDER BY count_date DESC")
    suspend fun getCountsByDateRange(startDate: Long, endDate: Long): List<InventoryCountEntity>
    
    @Query("DELETE FROM inventory_counts")
    suspend fun deleteAll()
}
