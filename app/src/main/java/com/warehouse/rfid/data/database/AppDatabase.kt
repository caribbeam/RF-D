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
 * 
 * VERSİYON 3: Kullanıcı yönetimi eklendi
 */
@Database(
    entities = [
        ProductEntity::class,
        StockMovementEntity::class,
        RFIDReadEntity::class,
        InventoryCountEntity::class,
        CategoryEntity::class,
        UserEntity::class,              // YENİ
        UserSessionEntity::class,       // YENİ
        UserActivityEntity::class       // YENİ
    ],
    version = 3,  // Versiyon 2 -> 3
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    // DAO'lar
    abstract fun productDao(): ProductDao
    abstract fun stockMovementDao(): StockMovementDao
    abstract fun rfidReadDao(): RFIDReadDao
    abstract fun inventoryCountDao(): InventoryCountDao
    abstract fun userDao(): UserDao  // YENİ
    
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // İlk kurulumda varsayılan admin kullanıcısı oluştur
                            createDefaultAdmin(db)
                        }
                    })
                    .fallbackToDestructiveMigration()  // Geliştirme aşamasında
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Varsayılan admin kullanıcısı oluştur
         */
        private fun createDefaultAdmin(db: SupportSQLiteDatabase) {
            // Şifre: admin123 (SHA-256 hash)
            val hashedPassword = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9"
            
            db.execSQL("""
                INSERT INTO users (username, password, full_name, employee_id, role, is_active, created_at)
                VALUES ('admin', '$hashedPassword', 'Sistem Yöneticisi', 'ADMIN001', 'ADMIN', 1, ${System.currentTimeMillis()})
            """)
        }
        
        /**
         * Migration v1 -> v2
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Yeni alanları ekle
                database.execSQL("ALTER TABLE products ADD COLUMN product_code TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE products ADD COLUMN unit TEXT NOT NULL DEFAULT 'Adet'")
                database.execSQL("ALTER TABLE products ADD COLUMN min_stock_level INTEGER NOT NULL DEFAULT 5")
                database.execSQL("ALTER TABLE products ADD COLUMN corridor TEXT")
                database.execSQL("ALTER TABLE products ADD COLUMN shelf TEXT")
                database.execSQL("ALTER TABLE products ADD COLUMN level TEXT")
                database.execSQL("ALTER TABLE products ADD COLUMN last_synced_at INTEGER")
                
                // Index'leri oluştur
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_products_product_code ON products(product_code)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_products_rfid_tag ON products(rfid_tag)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_products_barcode ON products(barcode)")
                
                // StockMovementEntity için yeni alanlar
                database.execSQL("ALTER TABLE stock_movements ADD COLUMN product_code TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE stock_movements ADD COLUMN previous_quantity INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE stock_movements ADD COLUMN new_quantity INTEGER NOT NULL DEFAULT 0")
                
                // RFIDReadEntity için yeni alanlar
                database.execSQL("ALTER TABLE rfid_reads ADD COLUMN product_id INTEGER")
                database.execSQL("ALTER TABLE rfid_reads ADD COLUMN product_code TEXT")
                database.execSQL("ALTER TABLE rfid_reads ADD COLUMN location TEXT")
                
                // Index'leri oluştur
                database.execSQL("CREATE INDEX IF NOT EXISTS index_stock_movements_product_id ON stock_movements(product_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_rfid_reads_rfid_tag ON rfid_reads(rfid_tag)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_rfid_reads_product_id ON rfid_reads(product_id)")
                
                // Yeni tablolar
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
                
                database.execSQL("CREATE INDEX IF NOT EXISTS index_inventory_counts_count_date ON inventory_counts(count_date)")
                
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
         * Migration v2 -> v3 (KULLANICI YÖNETİMİ)
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // USERS tablosu
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        username TEXT NOT NULL,
                        password TEXT NOT NULL,
                        full_name TEXT NOT NULL,
                        employee_id TEXT NOT NULL,
                        role TEXT NOT NULL,
                        department TEXT,
                        phone TEXT,
                        email TEXT,
                        is_active INTEGER NOT NULL DEFAULT 1,
                        created_at INTEGER NOT NULL,
                        last_login INTEGER,
                        terminal_id TEXT
                    )
                """)
                
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_users_username ON users(username)")
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_users_employee_id ON users(employee_id)")
                
                // USER_SESSIONS tablosu
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_sessions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        user_id INTEGER NOT NULL,
                        username TEXT NOT NULL,
                        full_name TEXT NOT NULL,
                        terminal_id TEXT NOT NULL,
                        login_time INTEGER NOT NULL,
                        logout_time INTEGER,
                        session_duration INTEGER,
                        actions_count INTEGER NOT NULL DEFAULT 0
                    )
                """)
                
                database.execSQL("CREATE INDEX IF NOT EXISTS index_user_sessions_user_id ON user_sessions(user_id)")
                
                // USER_ACTIVITIES tablosu
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_activities (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        user_id INTEGER NOT NULL,
                        username TEXT NOT NULL,
                        full_name TEXT NOT NULL,
                        activity_type TEXT NOT NULL,
                        description TEXT NOT NULL,
                        product_code TEXT,
                        quantity INTEGER,
                        terminal_id TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        synced INTEGER NOT NULL DEFAULT 0
                    )
                """)
                
                database.execSQL("CREATE INDEX IF NOT EXISTS index_user_activities_user_id ON user_activities(user_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_user_activities_activity_type ON user_activities(activity_type)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_user_activities_timestamp ON user_activities(timestamp)")
                
                // Mevcut tablolara kullanıcı bilgisi ekle
                database.execSQL("ALTER TABLE stock_movements ADD COLUMN user_id INTEGER")
                database.execSQL("ALTER TABLE stock_movements ADD COLUMN username TEXT")
                database.execSQL("ALTER TABLE stock_movements ADD COLUMN terminal_id TEXT")
                
                database.execSQL("ALTER TABLE inventory_counts ADD COLUMN user_id INTEGER")
                database.execSQL("ALTER TABLE inventory_counts ADD COLUMN terminal_id TEXT")
                
                database.execSQL("ALTER TABLE rfid_reads ADD COLUMN user_id INTEGER")
                database.execSQL("ALTER TABLE rfid_reads ADD COLUMN terminal_id TEXT")
                
                // Varsayılan admin kullanıcısı oluştur
                val hashedPassword = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9"
                database.execSQL("""
                    INSERT INTO users (username, password, full_name, employee_id, role, is_active, created_at)
                    VALUES ('admin', '$hashedPassword', 'Sistem Yöneticisi', 'ADMIN001', 'ADMIN', 1, ${System.currentTimeMillis()})
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
    
    @androidx.room.TypeConverter
    fun fromUserRole(value: UserRole): String {
        return value.name
    }
    
    @androidx.room.TypeConverter
    fun toUserRole(value: String): UserRole {
        return try {
            UserRole.valueOf(value)
        } catch (e: IllegalArgumentException) {
            UserRole.VIEWER
        }
    }
    
    @androidx.room.TypeConverter
    fun fromActivityType(value: ActivityType): String {
        return value.name
    }
    
    @androidx.room.TypeConverter
    fun toActivityType(value: String): ActivityType {
        return try {
            ActivityType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ActivityType.LOGIN
        }
    }
}
