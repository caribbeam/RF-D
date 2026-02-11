package com.warehouse.rfid.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

/**
 * Ürün veritabanı entity sınıfı
 * Güncellenmiş versiyon - Tüm özellikler eklendi
 */
@Entity(
    tableName = "products",
    indices = [
        Index(value = ["product_code"], unique = true),
        Index(value = ["rfid_tag"]),
        Index(value = ["barcode"])
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // ÜRÜN BİLGİLERİ
    @ColumnInfo(name = "product_code")
    val productCode: String,  // DELL-XPS-001 (Benzersiz)
    
    @ColumnInfo(name = "name")
    val name: String,  // Laptop Dell XPS 15
    
    @ColumnInfo(name = "description")
    val description: String? = null,
    
    @ColumnInfo(name = "barcode")
    val barcode: String? = null,
    
    // RFID BİLGİLERİ
    @ColumnInfo(name = "rfid_tag")
    val rfidTag: String? = null,  // EPC kodu (opsiyonel, henüz etiket yoksa)
    
    // STOK BİLGİLERİ
    @ColumnInfo(name = "quantity")
    val quantity: Int = 0,
    
    @ColumnInfo(name = "unit")
    val unit: String = "Adet",  // Adet, Koli, Palet, Kg, vb.
    
    @ColumnInfo(name = "min_stock_level")
    val minStockLevel: Int = 5,  // Minimum stok seviyesi
    
    // KONUM BİLGİLERİ (Detaylı)
    @ColumnInfo(name = "corridor")
    val corridor: String? = null,  // A, B, C, D
    
    @ColumnInfo(name = "shelf")
    val shelf: String? = null,  // 1, 2, 3, 4, 5
    
    @ColumnInfo(name = "level")
    val level: String? = null,  // Üst, Orta, Alt
    
    // TARİH BİLGİLERİ
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    
    // SENKRONIZASYON
    @ColumnInfo(name = "synced")
    val synced: Boolean = false,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null
) {
    /**
     * Tam konum bilgisi (Koridor-Raf-Seviye)
     */
    fun getFullLocation(): String {
        return if (corridor != null && shelf != null && level != null) {
            "$corridor-$shelf-$level"
        } else {
            "Konum Belirtilmemiş"
        }
    }
    
    /**
     * Düşük stok kontrolü
     */
    fun isLowStock(): Boolean {
        return quantity > 0 && quantity <= minStockLevel
    }
    
    /**
     * Stok tükendi mi?
     */
    fun isOutOfStock(): Boolean {
        return quantity == 0
    }
    
    /**
     * RFID etiketi var mı?
     */
    fun hasRFIDTag(): Boolean {
        return !rfidTag.isNullOrEmpty()
    }
}

/**
 * Stok hareketi entity sınıfı
 */
@Entity(
    tableName = "stock_movements",
    indices = [Index(value = ["product_id"])]
)
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "product_id")
    val productId: Long,
    
    @ColumnInfo(name = "product_code")
    val productCode: String,  // Hızlı erişim için
    
    @ColumnInfo(name = "movement_type")
    val movementType: MovementType,
    
    @ColumnInfo(name = "quantity")
    val quantity: Int,
    
    @ColumnInfo(name = "previous_quantity")
    val previousQuantity: Int,  // Önceki miktar
    
    @ColumnInfo(name = "new_quantity")
    val newQuantity: Int,  // Yeni miktar
    
    @ColumnInfo(name = "user")
    val user: String = "System",
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "synced")
    val synced: Boolean = false
)

/**
 * Hareket tipleri
 */
enum class MovementType {
    IN,         // Giriş (Yeni ürün geldi)
    OUT,        // Çıkış (Ürün sevk edildi)
    COUNT,      // Sayım (Envanter sayımı)
    ADJUST,     // Düzeltme (Manuel düzeltme)
    RETURN,     // İade
    DAMAGE,     // Hasar/Fire
    TRANSFER    // Transfer (Konum değişikliği)
}

/**
 * RFID okuma kaydı
 */
@Entity(
    tableName = "rfid_reads",
    indices = [
        Index(value = ["rfid_tag"]),
        Index(value = ["product_id"])
    ]
)
data class RFIDReadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "rfid_tag")
    val rfidTag: String,  // EPC kodu
    
    @ColumnInfo(name = "product_id")
    val productId: Long? = null,  // Eşleştirilmiş ürün ID
    
    @ColumnInfo(name = "product_code")
    val productCode: String? = null,  // Hızlı erişim
    
    @ColumnInfo(name = "rssi")
    val rssi: Int,  // Sinyal gücü
    
    @ColumnInfo(name = "read_count")
    val readCount: Int = 1,
    
    @ColumnInfo(name = "location")
    val location: String? = null,  // Okunduğu konum
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Envanter sayım kaydı
 */
@Entity(
    tableName = "inventory_counts",
    indices = [Index(value = ["count_date"])]
)
data class InventoryCountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "count_date")
    val countDate: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "total_products")
    val totalProducts: Int,  // Toplam ürün çeşidi
    
    @ColumnInfo(name = "total_quantity")
    val totalQuantity: Int,  // Toplam miktar
    
    @ColumnInfo(name = "low_stock_count")
    val lowStockCount: Int,  // Düşük stoklu ürün sayısı
    
    @ColumnInfo(name = "out_of_stock_count")
    val outOfStockCount: Int,  // Tükenen ürün sayısı
    
    @ColumnInfo(name = "rfid_tags_read")
    val rfidTagsRead: Int,  // Okunan RFID etiket sayısı
    
    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Int,  // Sayım süresi (saniye)
    
    @ColumnInfo(name = "user")
    val user: String = "System",
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "synced")
    val synced: Boolean = false
)

/**
 * Ürün kategorisi (opsiyonel)
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "description")
    val description: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
