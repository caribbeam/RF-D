package com.warehouse.rfid.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Ana veritabanı sınıfı
 * Tüm entity'ler ve DAO'lar burada tanımlanır
 */
@Database(
    entities = [
        ProductEntity::class,
        StockMovementEntity::class,
        RFIDReadEntity::class,
        InventoryCountEntity::class,
        CategoryEntity::class
    ],
    version = 2,  // Versiyon güncellendi
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    // DAO'lar
    abstract fun productDao(): ProductDao
    abstract fun stockMovementDao(): StockMovementDao
    abstract fun rfidReadDao(): RFIDReadDao
    abstract fun inventoryCountDao(): InventoryCountDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        private const val DATABASE_NAME = "warehouse_rfid_db"
        
        /**
         * Singleton pattern ile veritabanı instance'ı
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()  // Geliştirme aşamasında
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Veritabanı migration (v1 -> v2)
         * Yeni alanlar eklendi
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Yeni alanları ekle
                database.execSQL("""
                    ALTER TABLE products ADD COLUMN product_code TEXT NOT NULL DEFAULT ''
                """)
                database.execSQL("""
                    ALTER TABLE products ADD COLUMN unit TEXT NOT NULL DEFAULT 'Adet'
                """)
                database.execSQL("""
                    ALTER TABLE products ADD COLUMN min_stock_level INTEGER NOT NULL DEFAULT 5
                """)
                database.execSQL("""
                    ALTER TABLE products ADD COLUMN corridor TEXT
                """)
                database.execSQL("""
                    ALTER TABLE products ADD COLUMN shelf TEXT
                """)
                database.execSQL("""
                    ALTER TABLE products ADD COLUMN level TEXT
                """)
                database.execSQL("""
                    ALTER TABLE products ADD COLUMN last_synced_at INTEGER
                """)
                
                // Index'leri oluştur
                database.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS index_products_product_code 
                    ON products(product_code)
                """)
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_products_rfid_tag 
                    ON products(rfid_tag)
                """)
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_products_barcode 
                    ON products(barcode)
                """)
                
                // StockMovementEntity için yeni alanlar
                database.execSQL("""
                    ALTER TABLE stock_movements ADD COLUMN product_code TEXT NOT NULL DEFAULT ''
                """)
                database.execSQL("""
                    ALTER TABLE stock_movements ADD COLUMN previous_quantity INTEGER NOT NULL DEFAULT 0
                """)
                database.execSQL("""
                    ALTER TABLE stock_movements ADD COLUMN new_quantity INTEGER NOT NULL DEFAULT 0
                """)
                
                // RFIDReadEntity için yeni alanlar
                database.execSQL("""
                    ALTER TABLE rfid_reads ADD COLUMN product_id INTEGER
                """)
                database.execSQL("""
                    ALTER TABLE rfid_reads ADD COLUMN product_code TEXT
                """)
                database.execSQL("""
                    ALTER TABLE rfid_reads ADD COLUMN location TEXT
                """)
                
                // Index'leri oluştur
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_stock_movements_product_id 
                    ON stock_movements(product_id)
                """)
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_rfid_reads_rfid_tag 
                    ON rfid_reads(rfid_tag)
                """)
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_rfid_reads_product_id 
                    ON rfid_reads(product_id)
                """)
                
                // Yeni tablolar oluştur
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS inventory_counts (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        count_date INTEGER NOT NULL,
                        total_products INTEGER NOT NULL,
                        total_quantity INTEGER NOT NULL,
                        low_stock_count INTEGER NOT NULL,
                        out_of_stock_count INTEGER NOT NULL,
                        rfid_tags_read INTEGER NOT NULL,
                        duration_seconds INTEGER NOT NULL,
                        user TEXT NOT NULL,
                        notes TEXT,
                        synced INTEGER NOT NULL DEFAULT 0
                    )
                """)
                
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_inventory_counts_count_date 
                    ON inventory_counts(count_date)
                """)
                
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS categories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT,
                        created_at INTEGER NOT NULL
                    )
                """)
            }
        }
        
        /**
         * Test için veritabanını temizle
         */
        fun clearDatabase(context: Context) {
            context.deleteDatabase(DATABASE_NAME)
            INSTANCE = null
        }
    }
}

/**
 * Type Converters
 * Enum ve diğer özel tipleri veritabanında saklamak için
 */
class Converters {
    
    @androidx.room.TypeConverter
    fun fromMovementType(value: MovementType): String {
        return value.name
    }
    
    @androidx.room.TypeConverter
    fun toMovementType(value: String): MovementType {
        return try {
            MovementType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            MovementType.ADJUST
        }
    }
}
