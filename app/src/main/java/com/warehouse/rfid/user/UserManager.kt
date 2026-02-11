package com.warehouse.rfid.user

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import com.warehouse.rfid.data.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.security.MessageDigest

/**
 * Kullanıcı Yönetim Sınıfı
 * Oturum yönetimi, giriş/çıkış, aktivite takibi
 */
class UserManager(private val context: Context, private val database: AppDatabase) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    
    // Aktif kullanıcı
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser
    
    // Aktif oturum
    private var currentSession: UserSessionEntity? = null
    
    // Terminal ID (cihaz benzersiz ID)
    val terminalId: String by lazy {
        getOrCreateTerminalId()
    }
    
    init {
        // Uygulama açıldığında son oturumu kontrol et
        loadLastSession()
    }
    
    /**
     * Terminal ID'yi al veya oluştur
     */
    private fun getOrCreateTerminalId(): String {
        var id = prefs.getString("terminal_id", null)
        if (id == null) {
            // Android cihaz ID'sini kullan
            id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            prefs.edit().putString("terminal_id", id).apply()
        }
        return id ?: "UNKNOWN"
    }
    
    /**
     * Son oturumu yükle
     */
    private fun loadLastSession() {
        val userId = prefs.getLong("last_user_id", -1)
        if (userId != -1L) {
            // Arka planda kullanıcıyı yükle
            kotlinx.coroutines.GlobalScope.launch {
                val user = database.userDao().getUserById(userId)
                if (user != null && user.isActive) {
                    _currentUser.value = user
                }
            }
        }
    }
    
    /**
     * Kullanıcı girişi (KRİTİK)
     */
    suspend fun login(username: String, password: String): LoginResult {
        return try {
            // Şifreyi hash'le
            val hashedPassword = hashPassword(password)
            
            // Kullanıcıyı kontrol et
            val user = database.userDao().login(username, hashedPassword)
            
            if (user == null) {
                return LoginResult.Error("Kullanıcı adı veya şifre hatalı")
            }
            
            if (!user.isActive) {
                return LoginResult.Error("Kullanıcı hesabı pasif")
            }
            
            // Oturum başlat
            val loginTime = System.currentTimeMillis()
            
            // Son giriş zamanını güncelle
            database.userDao().updateLastLogin(user.id, loginTime, terminalId)
            
            // Oturum kaydı oluştur
            val session = UserSessionEntity(
                userId = user.id,
                username = user.username,
                fullName = user.fullName,
                terminalId = terminalId,
                loginTime = loginTime
            )
            val sessionId = database.userDao().insertSession(session)
            currentSession = session.copy(id = sessionId)
            
            // Aktivite kaydı
            logActivity(
                user = user,
                activityType = ActivityType.LOGIN,
                description = "${user.fullName} giriş yaptı"
            )
            
            // Aktif kullanıcıyı ayarla
            _currentUser.value = user
            prefs.edit().putLong("last_user_id", user.id).apply()
            
            LoginResult.Success(user)
            
        } catch (e: Exception) {
            LoginResult.Error("Giriş hatası: ${e.message}")
        }
    }
    
    /**
     * Kullanıcı çıkışı
     */
    suspend fun logout() {
        val user = _currentUser.value ?: return
        
        try {
            val logoutTime = System.currentTimeMillis()
            
            // Oturumu kapat
            currentSession?.let { session ->
                val duration = logoutTime - session.loginTime
                database.userDao().closeSession(session.id, logoutTime, duration)
            }
            
            // Aktivite kaydı
            logActivity(
                user = user,
                activityType = ActivityType.LOGOUT,
                description = "${user.fullName} çıkış yaptı"
            )
            
            // Terminal ID'yi temizle
            database.userDao().updateTerminalId(user.id, null)
            
            // Aktif kullanıcıyı temizle
            _currentUser.value = null
            currentSession = null
            
        } catch (e: Exception) {
            // Hata olsa bile çıkış yap
            _currentUser.value = null
            currentSession = null
        }
    }
    
    /**
     * Aktivite kaydet (KRİTİK)
     */
    suspend fun logActivity(
        user: UserEntity = _currentUser.value ?: return,
        activityType: ActivityType,
        description: String,
        productCode: String? = null,
        quantity: Int? = null
    ) {
        try {
            val activity = UserActivityEntity(
                userId = user.id,
                username = user.username,
                fullName = user.fullName,
                activityType = activityType,
                description = description,
                productCode = productCode,
                quantity = quantity,
                terminalId = terminalId
            )
            
            database.userDao().insertActivity(activity)
            
            // Oturum işlem sayısını artır
            currentSession?.let { session ->
                val updatedSession = session.copy(actionsCount = session.actionsCount + 1)
                database.userDao().updateSession(updatedSession)
                currentSession = updatedSession
            }
            
        } catch (e: Exception) {
            // Log hatası - sessizce devam et
        }
    }
    
    /**
     * Yeni kullanıcı oluştur
     */
    suspend fun createUser(
        username: String,
        password: String,
        fullName: String,
        employeeId: String,
        role: UserRole,
        department: String? = null,
        phone: String? = null,
        email: String? = null
    ): CreateUserResult {
        return try {
            // Kullanıcı adı kontrolü
            val existingUser = database.userDao().getUserByUsername(username)
            if (existingUser != null) {
                return CreateUserResult.Error("Bu kullanıcı adı zaten kullanılıyor")
            }
            
            // Personel numarası kontrolü
            val existingEmployee = database.userDao().getUserByEmployeeId(employeeId)
            if (existingEmployee != null) {
                return CreateUserResult.Error("Bu personel numarası zaten kayıtlı")
            }
            
            // Şifreyi hash'le
            val hashedPassword = hashPassword(password)
            
            // Kullanıcı oluştur
            val user = UserEntity(
                username = username,
                password = hashedPassword,
                fullName = fullName,
                employeeId = employeeId,
                role = role,
                department = department,
                phone = phone,
                email = email
            )
            
            val userId = database.userDao().insert(user)
            
            // Aktivite kaydı (admin tarafından)
            _currentUser.value?.let { admin ->
                logActivity(
                    user = admin,
                    activityType = ActivityType.SETTINGS_CHANGE,
                    description = "${admin.fullName} yeni kullanıcı oluşturdu: $fullName"
                )
            }
            
            CreateUserResult.Success(user.copy(id = userId))
            
        } catch (e: Exception) {
            CreateUserResult.Error("Kullanıcı oluşturma hatası: ${e.message}")
        }
    }
    
    /**
     * Şifre hash'leme
     */
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Kullanıcı yetkisi kontrolü
     */
    fun hasPermission(permission: Permission): Boolean {
        val user = _currentUser.value ?: return false
        
        return when (permission) {
            Permission.VIEW -> true  // Herkes görüntüleyebilir
            Permission.ENTRY -> user.role in listOf(UserRole.ADMIN, UserRole.OPERATOR)
            Permission.EXIT -> user.role in listOf(UserRole.ADMIN, UserRole.OPERATOR)
            Permission.COUNT -> user.role in listOf(UserRole.ADMIN, UserRole.OPERATOR)
            Permission.ADMIN -> user.role == UserRole.ADMIN
        }
    }
    
    /**
     * Aktif kullanıcı var mı?
     */
    fun isLoggedIn(): Boolean = _currentUser.value != null
    
    /**
     * Kullanıcı bilgilerini al
     */
    fun getCurrentUserInfo(): String {
        val user = _currentUser.value ?: return "Giriş yapılmadı"
        return "${user.fullName} (${user.employeeId}) - Terminal: $terminalId"
    }
}

/**
 * Giriş sonucu
 */
sealed class LoginResult {
    data class Success(val user: UserEntity) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

/**
 * Kullanıcı oluşturma sonucu
 */
sealed class CreateUserResult {
    data class Success(val user: UserEntity) : CreateUserResult()
    data class Error(val message: String) : CreateUserResult()
}

/**
 * Yetki tipleri
 */
enum class Permission {
    VIEW,       // Görüntüleme
    ENTRY,      // Giriş yapma
    EXIT,       // Çıkış yapma
    COUNT,      // Sayım yapma
    ADMIN       // Yönetim
}
