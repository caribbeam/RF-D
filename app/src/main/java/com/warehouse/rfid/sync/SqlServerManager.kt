package com.warehouse.rfid.sync

import android.content.Context
import android.util.Log
import com.warehouse.rfid.data.database.AppDatabase
import com.warehouse.rfid.data.database.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

/**
 * SQL Server (Mikro) Entegrasyon Yöneticisi
 * Mevcut SQL Mikro veritabanından ürün kodlarını çeker
 * AFP002460 gibi kodları RFID sistemine aktarır
 */
class SqlServerManager(
    private val context: Context,
    private val localDatabase: AppDatabase
) {
    
    private val TAG = "SqlServerManager"
    
    // SQL Server bağlantı bilgileri
    private var serverIp: String = ""
    private var serverPort: String = "1433"
    private var databaseName: String = ""
    private var username: String = ""
    private var password: String = ""
    
    // Bağlantı
    private var connection: Connection? = null
    
    /**
     * Bağlantı ayarlarını yapılandır
     */
    fun configure(
        serverIp: String,
        serverPort: String = "1433",
        databaseName: String,
        username: String,
        password: String
    ) {
        this.serverIp = serverIp
        this.serverPort = serverPort
        this.databaseName = databaseName
        this.username = username
        this.password = password
    }
    
    /**
     * SQL Server'a bağlan
     */
    suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            // JTDS driver kullan (Android için)
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            
            val connectionUrl = "jdbc:jtds:sqlserver://$serverIp:$serverPort/$databaseName"
            
            connection = DriverManager.getConnection(
                connectionUrl,
                username,
                password
            )
            
            Log.d(TAG, "SQL Server'a bağlandı: $serverIp")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "SQL Server bağlantı hatası: ${e.message}")
            false
        }
    }
    
    /**
     * Bağlantıyı kapat
     */
    fun disconnect() {
        try {
            connection?.close()
            connection = null
            Log.d(TAG, "SQL Server bağlantısı kapatıldı")
        } catch (e: Exception) {
            Log.e(TAG, "Bağlantı kapatma hatası: ${e.message}")
        }
    }
    
    /**
     * Mevcut ürünleri SQL Mikro'dan çek (KRİTİK)
     * AFP002460 gibi kodları alır
     */
    suspend fun syncProductsFromSqlMikro(): SyncResult = withContext(Dispatchers.IO) {
        return@withContext try {
            if (connection == null || connection?.isClosed == true) {
                if (!connect()) {
                    return@withContext SyncResult.Error("SQL Server bağlantısı kurulamadı")
                }
            }
            
            val statement = connection?.createStatement()
            
            // SQL Mikro'dan ürünleri çek
            // Tablo ve kolon isimleri SQL Mikro yapınıza göre ayarlanmalı
            val query = """
                SELECT 
                    sto_kod AS product_code,
                    sto_isim AS product_name,
                    sto_birim1_ad AS unit,
                    sto_perakende_vergi AS barcode,
                    sto_marka AS brand,
                    sto_model AS model,
                    sto_aciklama AS description
                FROM STOKLAR
                WHERE sto_kod IS NOT NULL
                ORDER BY sto_kod
            """.trimIndent()
            
            val resultSet: ResultSet? = statement?.executeQuery(query)
            
            val products = mutableListOf<SqlMikroProduct>()
            var count = 0
            
            while (resultSet?.next() == true) {
                val product = SqlMikroProduct(
                    productCode = resultSet.getString("product_code")?.trim() ?: "",
                    productName = resultSet.getString("product_name")?.trim() ?: "",
                    unit = resultSet.getString("unit")?.trim() ?: "Adet",
                    barcode = resultSet.getString("barcode")?.trim(),
                    brand = resultSet.getString("brand")?.trim(),
                    model = resultSet.getString("model")?.trim(),
                    description = resultSet.getString("description")?.trim()
                )
                
                if (product.productCode.isNotEmpty()) {
                    products.add(product)
                    count++
                }
            }
            
            resultSet?.close()
            statement?.close()
            
            // Yerel veritabanına aktar
            val imported = importToLocalDatabase(products)
            
            Log.d(TAG, "SQL Mikro'dan $count ürün çekildi, $imported ürün aktarıldı")
            
            SyncResult.Success(
                totalProducts = count,
                importedProducts = imported,
                message = "$count ürün çekildi, $imported yeni ürün aktarıldı"
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Senkronizasyon hatası: ${e.message}")
            SyncResult.Error("Senkronizasyon hatası: ${e.message}")
        }
    }
    
    /**
     * SQL Mikro ürünlerini yerel veritabanına aktar
     */
    private suspend fun importToLocalDatabase(products: List<SqlMikroProduct>): Int {
        var importedCount = 0
        
        for (sqlProduct in products) {
            try {
                // Ürün zaten var mı kontrol et
                val existing = localDatabase.productDao().findByProductCode(sqlProduct.productCode)
                
                if (existing == null) {
                    // Yeni ürün - ekle
                    val product = ProductEntity(
                        productCode = sqlProduct.productCode,
                        name = sqlProduct.productName,
                        quantity = 0, // Başlangıçta 0
                        unit = sqlProduct.unit,
                        minStockLevel = 5,
                        barcode = sqlProduct.barcode,
                        description = buildDescription(sqlProduct),
                        rfidTag = null, // RFID henüz atanmadı
                        corridor = null,
                        shelf = null,
                        level = null
                    )
                    
                    localDatabase.productDao().insert(product)
                    importedCount++
                    
                    Log.d(TAG, "Yeni ürün eklendi: ${sqlProduct.productCode} - ${sqlProduct.productName}")
                } else {
                    // Ürün var - sadece isim ve açıklamayı güncelle (miktar değişmez)
                    val updated = existing.copy(
                        name = sqlProduct.productName,
                        unit = sqlProduct.unit,
                        barcode = sqlProduct.barcode ?: existing.barcode,
                        description = buildDescription(sqlProduct)
                    )
                    
                    localDatabase.productDao().update(updated)
                    Log.d(TAG, "Ürün güncellendi: ${sqlProduct.productCode}")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Ürün aktarma hatası (${sqlProduct.productCode}): ${e.message}")
            }
        }
        
        return importedCount
    }
    
    /**
     * Açıklama oluştur
     */
    private fun buildDescription(product: SqlMikroProduct): String {
        val parts = mutableListOf<String>()
        
        product.brand?.let { if (it.isNotEmpty()) parts.add("Marka: $it") }
        product.model?.let { if (it.isNotEmpty()) parts.add("Model: $it") }
        product.description?.let { if (it.isNotEmpty()) parts.add(it) }
        
        return parts.joinToString(" | ")
    }
    
    /**
     * Tek ürün sorgula (AFP002460 gibi)
     */
    suspend fun getProductByCode(productCode: String): SqlMikroProduct? = withContext(Dispatchers.IO) {
        return@withContext try {
            if (connection == null || connection?.isClosed == true) {
                if (!connect()) return@withContext null
            }
            
            val statement = connection?.prepareStatement("""
                SELECT 
                    sto_kod AS product_code,
                    sto_isim AS product_name,
                    sto_birim1_ad AS unit,
                    sto_perakende_vergi AS barcode,
                    sto_marka AS brand,
                    sto_model AS model,
                    sto_aciklama AS description
                FROM STOKLAR
                WHERE sto_kod = ?
            """.trimIndent())
            
            statement?.setString(1, productCode)
            val resultSet = statement?.executeQuery()
            
            var product: SqlMikroProduct? = null
            
            if (resultSet?.next() == true) {
                product = SqlMikroProduct(
                    productCode = resultSet.getString("product_code")?.trim() ?: "",
                    productName = resultSet.getString("product_name")?.trim() ?: "",
                    unit = resultSet.getString("unit")?.trim() ?: "Adet",
                    barcode = resultSet.getString("barcode")?.trim(),
                    brand = resultSet.getString("brand")?.trim(),
                    model = resultSet.getString("model")?.trim(),
                    description = resultSet.getString("description")?.trim()
                )
            }
            
            resultSet?.close()
            statement?.close()
            
            product
            
        } catch (e: Exception) {
            Log.e(TAG, "Ürün sorgulama hatası: ${e.message}")
            null
        }
    }
    
    /**
     * Ürün kodu ile arama (AFP ile başlayanlar gibi)
     */
    suspend fun searchProductsByCode(searchTerm: String): List<SqlMikroProduct> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (connection == null || connection?.isClosed == true) {
                if (!connect()) return@withContext emptyList()
            }
            
            val statement = connection?.prepareStatement("""
                SELECT TOP 100
                    sto_kod AS product_code,
                    sto_isim AS product_name,
                    sto_birim1_ad AS unit,
                    sto_perakende_vergi AS barcode,
                    sto_marka AS brand,
                    sto_model AS model,
                    sto_aciklama AS description
                FROM STOKLAR
                WHERE sto_kod LIKE ?
                ORDER BY sto_kod
            """.trimIndent())
            
            statement?.setString(1, "$searchTerm%")
            val resultSet = statement?.executeQuery()
            
            val products = mutableListOf<SqlMikroProduct>()
            
            while (resultSet?.next() == true) {
                val product = SqlMikroProduct(
                    productCode = resultSet.getString("product_code")?.trim() ?: "",
                    productName = resultSet.getString("product_name")?.trim() ?: "",
                    unit = resultSet.getString("unit")?.trim() ?: "Adet",
                    barcode = resultSet.getString("barcode")?.trim(),
                    brand = resultSet.getString("brand")?.trim(),
                    model = resultSet.getString("model")?.trim(),
                    description = resultSet.getString("description")?.trim()
                )
                
                if (product.productCode.isNotEmpty()) {
                    products.add(product)
                }
            }
            
            resultSet?.close()
            statement?.close()
            
            products
            
        } catch (e: Exception) {
            Log.e(TAG, "Arama hatası: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Bağlantı durumunu kontrol et
     */
    fun isConnected(): Boolean {
        return try {
            connection != null && connection?.isClosed() == false
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * SQL Mikro ürün modeli
 */
data class SqlMikroProduct(
    val productCode: String,      // AFP002460
    val productName: String,       // Ürün adı
    val unit: String,              // Birim
    val barcode: String?,          // Barkod
    val brand: String?,            // Marka
    val model: String?,            // Model
    val description: String?       // Açıklama
)

/**
 * Senkronizasyon sonucu
 */
sealed class SyncResult {
    data class Success(
        val totalProducts: Int,
        val importedProducts: Int,
        val message: String
    ) : SyncResult()
    
    data class Error(val message: String) : SyncResult()
}
