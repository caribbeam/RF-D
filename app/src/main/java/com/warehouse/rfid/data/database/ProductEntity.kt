package com.warehouse.rfid.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

/**
 * Ürün veritabanı entity sınıfı
 */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "rfid_tag")
    val rfidTag: String,
    
    @ColumnInfo(name = "barcode")
    val barcode: String?,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "quantity")
    val quantity: Int,
    
    @ColumnInfo(name = "location")
    val location: String?,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "synced")
    val synced: Boolean = false
)

/**
 * Stok hareketi entity sınıfı
 */
@Entity(tableName = "stock_movements")
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "product_id")
    val productId: Long,
    
    @ColumnInfo(name = "movement_type")
    val movementType: MovementType,
    
    @ColumnInfo(name = "quantity")
    val quantity: Int,
    
    @ColumnInfo(name = "user")
    val user: String,
    
    @ColumnInfo(name = "notes")
    val notes: String?,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "synced")
    val synced: Boolean = false
)

/**
 * Hareket tipleri
 */
enum class MovementType {
    IN,      // Giriş
    OUT,     // Çıkış
    COUNT,   // Sayım
    ADJUST   // Düzeltme
}

/**
 * RFID okuma kaydı
 */
@Entity(tableName = "rfid_reads")
data class RFIDReadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "rfid_tag")
    val rfidTag: String,
    
    @ColumnInfo(name = "rssi")
    val rssi: Int,
    
    @ColumnInfo(name = "read_count")
    val readCount: Int,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
