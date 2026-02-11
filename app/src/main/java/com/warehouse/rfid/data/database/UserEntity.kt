package com.warehouse.rfid.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

/**
 * Kullanıcı Entity
 * Çoklu terminal için kullanıcı yönetimi
 */
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["employee_id"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "username")
    val username: String,  // Kullanıcı adı (benzersiz)
    
    @ColumnInfo(name = "password")
    val password: String,  // Şifre (hash'lenmiş)
    
    @ColumnInfo(name = "full_name")
    val fullName: String,  // Ad Soyad
    
    @ColumnInfo(name = "employee_id")
    val employeeId: String,  // Personel numarası (benzersiz)
    
    @ColumnInfo(name = "role")
    val role: UserRole,  // Rol (Admin, Operator, Viewer)
    
    @ColumnInfo(name = "department")
    val department: String? = null,  // Departman
    
    @ColumnInfo(name = "phone")
    val phone: String? = null,  // Telefon
    
    @ColumnInfo(name = "email")
    val email: String? = null,  // Email
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,  // Aktif mi?
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "last_login")
    val lastLogin: Long? = null,  // Son giriş zamanı
    
    @ColumnInfo(name = "terminal_id")
    val terminalId: String? = null  // Hangi terminalde aktif
)

/**
 * Kullanıcı rolleri
 */
enum class UserRole {
    ADMIN,      // Yönetici - Tüm yetkiler
    OPERATOR,   // Operatör - Giriş/Çıkış/Sayım
    VIEWER      // Görüntüleyici - Sadece okuma
}

/**
 * Kullanıcı oturumu
 */
@Entity(
    tableName = "user_sessions",
    indices = [Index(value = ["user_id"])]
)
data class UserSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "user_id")
    val userId: Long,
    
    @ColumnInfo(name = "username")
    val username: String,
    
    @ColumnInfo(name = "full_name")
    val fullName: String,
    
    @ColumnInfo(name = "terminal_id")
    val terminalId: String,  // Cihaz ID
    
    @ColumnInfo(name = "login_time")
    val loginTime: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "logout_time")
    val logoutTime: Long? = null,
    
    @ColumnInfo(name = "session_duration")
    val sessionDuration: Long? = null,  // Süre (ms)
    
    @ColumnInfo(name = "actions_count")
    val actionsCount: Int = 0  // Yapılan işlem sayısı
)

/**
 * Kullanıcı aktivite logu
 */
@Entity(
    tableName = "user_activities",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["activity_type"]),
        Index(value = ["timestamp"])
    ]
)
data class UserActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "user_id")
    val userId: Long,
    
    @ColumnInfo(name = "username")
    val username: String,
    
    @ColumnInfo(name = "full_name")
    val fullName: String,
    
    @ColumnInfo(name = "activity_type")
    val activityType: ActivityType,
    
    @ColumnInfo(name = "description")
    val description: String,  // İşlem açıklaması
    
    @ColumnInfo(name = "product_code")
    val productCode: String? = null,  // İlgili ürün kodu
    
    @ColumnInfo(name = "quantity")
    val quantity: Int? = null,  // Miktar
    
    @ColumnInfo(name = "terminal_id")
    val terminalId: String,  // Hangi cihazdan
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "synced")
    val synced: Boolean = false
)

/**
 * Aktivite tipleri
 */
enum class ActivityType {
    LOGIN,              // Giriş yaptı
    LOGOUT,             // Çıkış yaptı
    PRODUCT_ENTRY,      // Ürün girişi yaptı
    PRODUCT_EXIT,       // Ürün çıkışı yaptı
    INVENTORY_COUNT,    // Sayım yaptı
    RFID_SCAN,          // RFID okuma yaptı
    LABEL_PRINT,        // Etiket yazdırdı
    PRODUCT_UPDATE,     // Ürün güncelledi
    PRODUCT_DELETE,     // Ürün sildi
    SETTINGS_CHANGE     // Ayar değiştirdi
}
