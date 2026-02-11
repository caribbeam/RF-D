package com.warehouse.rfid.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Ana veritabanı sınıfı
 */
@Database(
    entities = [
        ProductEntity::class,
        StockMovementEntity::class,
        RFIDReadEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun productDao(): ProductDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "warehouse_rfid_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Type converters for Room
 */
class Converters {
    @androidx.room.TypeConverter
    fun fromMovementType(value: MovementType): String {
        return value.name
    }
    
    @androidx.room.TypeConverter
    fun toMovementType(value: String): MovementType {
        return MovementType.valueOf(value)
    }
}
