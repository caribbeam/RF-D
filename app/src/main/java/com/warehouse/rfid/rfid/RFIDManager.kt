package com.warehouse.rfid.rfid

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * RFID Yönetici Sınıfı
 * Chainway C5 UHF RFID okuyucu için yönetici
 * 
 * NOT: Gerçek cihaz için Chainway SDK entegre edilecek
 * Şu an demo/simülasyon modu ile çalışıyor
 * 
 * PERFORMANS OPTİMİZASYONLARI:
 * - CoroutineScope ile memory leak önleme
 * - Tag cache ile hızlı erişim
 * - Batch processing ile UI güncellemelerini azaltma
 * - Configurable scan speed
 */
class RFIDManager(private val context: Context) {
    
    private val TAG = "RFIDManager"
    
    // Coroutine scope - memory leak önleme
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // RFID durumu
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning
    
    private val _scannedTags = MutableStateFlow<List<RFIDTag>>(emptyList())
    val scannedTags: StateFlow<List<RFIDTag>> = _scannedTags
    
    // Tag cache - performans için
    private val tagCache = ConcurrentHashMap<String, RFIDTag>()
    
    // Demo modu (gerçek cihaz yoksa)
    private var isDemoMode = true
    private var scanningJob: Job? = null
    
    // Performans ayarları
    private var scanSpeed: Long = 100 // ms - Demo modda tag okuma hızı (varsayılan: 100ms)
    private var batchSize: Int = 10 // Kaç tag'de bir UI güncellensin
    private var batchCounter = 0
    
    // Chainway SDK nesneleri (gerçek cihaz için)
    // private var uhfReader: UHFReader? = null
    
    /**
     * RFID okuyucuyu başlat
     */
    fun initialize(): Boolean {
        return try {
            // Gerçek cihaz kontrolü
            isDemoMode = !isChainwayDevice()
            
            if (isDemoMode) {
                Log.d(TAG, "Demo modu aktif - Simülasyon ile çalışıyor")
                true
            } else {
                // Gerçek Chainway SDK başlatma
                // uhfReader = UHFReader.getInstance()
                // uhfReader?.open() ?: false
                Log.d(TAG, "Chainway cihazı tespit edildi")
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "RFID başlatma hatası: ${e.message}")
            false
        }
    }
    
    /**
     * Okumaya başla
     */
    suspend fun startScanning(callback: (RFIDTag) -> Unit) {
        if (_isScanning.value) return
        
        _isScanning.value = true
        _scannedTags.value = emptyList()
        tagCache.clear()
        batchCounter = 0
        
        if (isDemoMode) {
            startDemoScanning(callback)
        } else {
            startRealScanning(callback)
        }
    }
    
    /**
     * Okumayı durdur
     */
    fun stopScanning() {
        _isScanning.value = false
        scanningJob?.cancel()
        scanningJob = null
        
        // Son batch'i güncelle
        updateStateFromCache()
        
        if (!isDemoMode) {
            // uhfReader?.stopInventory()
        }
    }
    
    /**
     * Tekli etiket oku
     */
    suspend fun readSingleTag(): RFIDTag? {
        return if (isDemoMode) {
            generateDemoTag()
        } else {
            // Gerçek okuma
            // val epc = uhfReader?.inventorySingleTag()
            // epc?.let { RFIDTag(it, -50, 1) }
            null
        }
    }
    
    /**
     * Güç seviyesini ayarla (0-30 dBm)
     */
    fun setPower(power: Int): Boolean {
        return try {
            if (!isDemoMode) {
                // uhfReader?.setPower(power) ?: false
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Güç ayarlama hatası: ${e.message}")
            false
        }
    }
    
    /**
     * Frekans ayarla
     */
    fun setFrequency(frequency: Int): Boolean {
        return try {
            if (!isDemoMode) {
                // uhfReader?.setFrequency(frequency) ?: false
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Frekans ayarlama hatası: ${e.message}")
            false
        }
    }
    
    /**
     * Kaynakları temizle
     */
    fun release() {
        stopScanning()
        scope.cancel()
        tagCache.clear()
        if (!isDemoMode) {
            // uhfReader?.close()
        }
    }
    
    /**
     * Tarama hızını ayarla (ms)
     */
    fun setScanSpeed(speedMs: Long) {
        scanSpeed = speedMs.coerceIn(50, 1000) // 50ms - 1000ms arası
    }
    
    /**
     * Batch boyutunu ayarla
     */
    fun setBatchSize(size: Int) {
        batchSize = size.coerceIn(1, 50) // 1-50 arası
    }
    
    /**
     * Cache'den state'i güncelle
     */
    private fun updateStateFromCache() {
        _scannedTags.value = tagCache.values.sortedByDescending { it.timestamp }
    }
    
    // Demo/Simülasyon fonksiyonları
    private suspend fun startDemoScanning(callback: (RFIDTag) -> Unit) {
        scanningJob = scope.launch {
            while (_isScanning.value) {
                delay(scanSpeed) // Ayarlanabilir hız
                val tag = generateDemoTag()
                
                // Cache'e ekle veya güncelle
                val existingTag = tagCache[tag.epc]
                val updatedTag = if (existingTag != null) {
                    existingTag.copy(
                        readCount = existingTag.readCount + 1,
                        rssi = tag.rssi,
                        timestamp = System.currentTimeMillis()
                    )
                } else {
                    tag
                }
                
                tagCache[tag.epc] = updatedTag
                
                // Callback'i hemen çağır
                withContext(Dispatchers.Main) {
                    callback(updatedTag)
                }
                
                // Batch processing - belirli sayıda tag'de bir UI güncelle
                batchCounter++
                if (batchCounter >= batchSize) {
                    updateStateFromCache()
                    batchCounter = 0
                }
            }
        }
    }
    
    private fun generateDemoTag(): RFIDTag {
        val randomEpc = "E200${(1000..9999).random()}${(10000000..99999999).random()}"
        val randomRssi = (-70..-40).random()
        return RFIDTag(randomEpc, randomRssi, 1)
    }
    
    private fun startRealScanning(callback: (RFIDTag) -> Unit) {
        // Gerçek Chainway SDK ile okuma
        /*
        uhfReader?.setInventoryListener { epc, rssi ->
            scope.launch {
                // Cache'e ekle veya güncelle
                val existingTag = tagCache[epc]
                val tag = if (existingTag != null) {
                    existingTag.copy(
                        readCount = existingTag.readCount + 1,
                        rssi = rssi,
                        timestamp = System.currentTimeMillis()
                    )
                } else {
                    RFIDTag(epc, rssi, 1)
                }
                
                tagCache[epc] = tag
                
                // Callback'i hemen çağır
                withContext(Dispatchers.Main) {
                    callback(tag)
                }
                
                // Batch processing
                batchCounter++
                if (batchCounter >= batchSize) {
                    updateStateFromCache()
                    batchCounter = 0
                }
            }
        }
        
        uhfReader?.startInventory()
        */
    }
    
    private fun isChainwayDevice(): Boolean {
        // Chainway cihaz kontrolü
        val manufacturer = android.os.Build.MANUFACTURER.lowercase()
        val model = android.os.Build.MODEL.lowercase()
        return manufacturer.contains("chainway") || model.contains("c5")
    }
}

/**
 * RFID Etiket veri sınıfı
 */
data class RFIDTag(
    val epc: String,           // EPC kodu
    val rssi: Int,             // Sinyal gücü
    val readCount: Int = 1,    // Okuma sayısı
    val timestamp: Long = System.currentTimeMillis()
)
