package com.warehouse.rfid.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Kullanıcı DAO
 */
@Dao
interface UserDao {
    
    // ==================== KULLANICI İŞLEMLERİ ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long
    
    @Update
    suspend fun update(user: UserEntity)
    
    @Delete
    suspend fun delete(user: UserEntity)
    
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?
    
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE employee_id = :employeeId")
    suspend fun getUserByEmployeeId(employeeId: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE is_active = 1 ORDER BY full_name")
    suspend fun getAllActiveUsers(): List<UserEntity>
    
    @Query("SELECT * FROM users ORDER BY full_name")
    suspend fun getAllUsers(): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE role = :role AND is_active = 1")
    suspend fun getUsersByRole(role: UserRole): List<UserEntity>
    
    /**
     * Kullanıcı girişi (KRİTİK)
     */
    @Query("SELECT * FROM users WHERE username = :username AND password = :password AND is_active = 1")
    suspend fun login(username: String, password: String): UserEntity?
    
    /**
     * Son giriş zamanını güncelle
     */
    @Query("UPDATE users SET last_login = :loginTime, terminal_id = :terminalId WHERE id = :userId")
    suspend fun updateLastLogin(userId: Long, loginTime: Long, terminalId: String)
    
    /**
     * Terminal ID'yi güncelle
     */
    @Query("UPDATE users SET terminal_id = :terminalId WHERE id = :userId")
    suspend fun updateTerminalId(userId: Long, terminalId: String?)
    
    /**
     * Kullanıcıyı aktif/pasif yap
     */
    @Query("UPDATE users SET is_active = :isActive WHERE id = :userId")
    suspend fun setUserActive(userId: Long, isActive: Boolean)
    
    // ==================== OTURUM İŞLEMLERİ ====================
    
    @Insert
    suspend fun insertSession(session: UserSessionEntity): Long
    
    @Update
    suspend fun updateSession(session: UserSessionEntity)
    
    @Query("SELECT * FROM user_sessions WHERE user_id = :userId ORDER BY login_time DESC")
    suspend fun getUserSessions(userId: Long): List<UserSessionEntity>
    
    @Query("SELECT * FROM user_sessions WHERE terminal_id = :terminalId ORDER BY login_time DESC LIMIT 1")
    suspend fun getLastSessionByTerminal(terminalId: String): UserSessionEntity?
    
    @Query("SELECT * FROM user_sessions WHERE logout_time IS NULL")
    suspend fun getActiveSessions(): List<UserSessionEntity>
    
    /**
     * Oturumu kapat
     */
    @Query("UPDATE user_sessions SET logout_time = :logoutTime, session_duration = :duration WHERE id = :sessionId")
    suspend fun closeSession(sessionId: Long, logoutTime: Long, duration: Long)
    
    // ==================== AKTİVİTE LOGU ====================
    
    @Insert
    suspend fun insertActivity(activity: UserActivityEntity): Long
    
    @Query("SELECT * FROM user_activities WHERE user_id = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getUserActivities(userId: Long, limit: Int = 100): List<UserActivityEntity>
    
    @Query("SELECT * FROM user_activities WHERE activity_type = :type ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getActivitiesByType(type: ActivityType, limit: Int = 100): List<UserActivityEntity>
    
    @Query("SELECT * FROM user_activities WHERE product_code = :productCode ORDER BY timestamp DESC")
    suspend fun getActivitiesByProduct(productCode: String): List<UserActivityEntity>
    
    @Query("SELECT * FROM user_activities WHERE terminal_id = :terminalId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getActivitiesByTerminal(terminalId: String, limit: Int = 100): List<UserActivityEntity>
    
    @Query("SELECT * FROM user_activities WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    suspend fun getActivitiesByDateRange(startTime: Long, endTime: Long): List<UserActivityEntity>
    
    /**
     * Bugünkü aktiviteler
     */
    @Query("""
        SELECT * FROM user_activities 
        WHERE timestamp >= :todayStart 
        ORDER BY timestamp DESC
    """)
    suspend fun getTodayActivities(todayStart: Long): List<UserActivityEntity>
    
    /**
     * Kullanıcının bugünkü aktiviteleri
     */
    @Query("""
        SELECT * FROM user_activities 
        WHERE user_id = :userId AND timestamp >= :todayStart 
        ORDER BY timestamp DESC
    """)
    suspend fun getUserTodayActivities(userId: Long, todayStart: Long): List<UserActivityEntity>
    
    // ==================== İSTATİSTİKLER ====================
    
    @Query("SELECT COUNT(*) FROM users WHERE is_active = 1")
    suspend fun getActiveUserCount(): Int
    
    @Query("SELECT COUNT(*) FROM user_sessions WHERE logout_time IS NULL")
    suspend fun getActiveSessionCount(): Int
    
    @Query("SELECT COUNT(*) FROM user_activities WHERE user_id = :userId AND timestamp >= :since")
    suspend fun getUserActivityCount(userId: Long, since: Long): Int
    
    @Query("""
        SELECT COUNT(*) FROM user_activities 
        WHERE activity_type = :type AND timestamp >= :since
    """)
    suspend fun getActivityCountByType(type: ActivityType, since: Long): Int
    
    /**
     * En aktif kullanıcılar
     */
    @Query("""
        SELECT user_id, COUNT(*) as activity_count 
        FROM user_activities 
        WHERE timestamp >= :since 
        GROUP BY user_id 
        ORDER BY activity_count DESC 
        LIMIT :limit
    """)
    suspend fun getMostActiveUsers(since: Long, limit: Int = 10): List<UserActivitySummary>
    
    // ==================== TEMİZLİK ====================
    
    @Query("DELETE FROM user_activities WHERE timestamp < :before")
    suspend fun deleteOldActivities(before: Long)
    
    @Query("DELETE FROM user_sessions WHERE logout_time < :before")
    suspend fun deleteOldSessions(before: Long)
}

/**
 * Kullanıcı aktivite özeti
 */
data class UserActivitySummary(
    @ColumnInfo(name = "user_id")
    val userId: Long,
    
    @ColumnInfo(name = "activity_count")
    val activityCount: Int
)
