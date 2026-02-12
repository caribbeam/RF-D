package com.warehouse.rfid.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.warehouse.rfid.R
import com.warehouse.rfid.data.database.AppDatabase
import com.warehouse.rfid.data.database.InventoryCountEntity
import com.warehouse.rfid.data.database.MovementType
import com.warehouse.rfid.data.database.StockMovementEntity
import com.warehouse.rfid.rfid.RFIDManager
import com.warehouse.rfid.rfid.RFIDTag
import kotlinx.coroutines.launch

/**
 * Envanter Sayımı Activity
 * 
 * ÖZELLİKLER:
 * - RFID ile hızlı sayım
 * - Konum bazlı sayım
 * - Eksik/Fazla tespit
 * - Rapor oluşturma
 * - Otomatik stok güncelleme
 */
class InventoryActivity : AppCompatActivity() {
    
    private lateinit var database: AppDatabase
    private lateinit var rfidManager: RFIDManager
    
    // Konum Seçimi
    private lateinit var actvCorridor: AutoCompleteTextView
    private lateinit var actvShelf: AutoCompleteTextView
    private lateinit var actvLevel: AutoCompleteTextView
    
    // İstatistikler
    private lateinit var tvTotalProducts: TextView
    private lateinit var tvTotalQuantity: TextView
    private lateinit var tvScannedTags: TextView
    private lateinit var tvDuration: TextView
    
    // Butonlar
    private lateinit var btnStartCount: MaterialButton
    private lateinit var btnStopCount: MaterialButton
    private lateinit var btnGenerateReport: MaterialButton
    
    // Sayım verileri
    private val scannedTags = mutableSetOf<String>()
    private var startTime: Long = 0
    private var isCounting = false
    
    // Konum seçenekleri
    private val corridors = arrayOf("Tümü", "A", "B", "C", "D", "E", "F")
    private val shelves = arrayOf("Tümü", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    private val levels = arrayOf("Tümü", "Üst", "Orta", "Alt")
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Envanter Sayımı"
        
        database = AppDatabase.getDatabase(this)
        rfidManager = RFIDManager(this)
        rfidManager.initialize()
        
        initViews()
        setupDropdowns()
        setupButtons()
        loadStatistics()
    }
    
    private fun initViews() {
        // Konum
        actvCorridor = findViewById(R.id.actvCorridor)
        actvShelf = findViewById(R.id.actvShelf)
        actvLevel = findViewById(R.id.actvLevel)
        
        // İstatistikler
        tvTotalProducts = findViewById(R.id.tvTotalProducts)
        tvTotalQuantity = findViewById(R.id.tvTotalQuantity)
        tvScannedTags = findViewById(R.id.tvScannedTags)
        tvDuration = findViewById(R.id.tvDuration)
        
        // Butonlar
        btnStartCount = findViewById(R.id.btnStartCount)
        btnStopCount = findViewById(R.id.btnStopCount)
        btnGenerateReport = findViewById(R.id.btnGenerateReport)
    }
    
    private fun setupDropdowns() {
        // Koridor
        val corridorAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, corridors)
        actvCorridor.setAdapter(corridorAdapter)
        actvCorridor.setText("Tümü", false)
        
        // Raf
        val shelfAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, shelves)
        actvShelf.setAdapter(shelfAdapter)
        actvShelf.setText("Tümü", false)
        
        // Seviye
        val levelAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, levels)
        actvLevel.setAdapter(levelAdapter)
        actvLevel.setText("Tümü", false)
    }
    
    private fun setupButtons() {
        btnStartCount.setOnClickListener {
            startInventoryCount()
        }
        
        btnStopCount.setOnClickListener {
            stopInventoryCount()
        }
        
        btnGenerateReport.setOnClickListener {
            generateReport()
        }
    }
    
    private fun loadStatistics() {
        lifecycleScope.launch {
            try {
                val totalProducts = database.productDao().getTotalProductCount()
                val totalQuantity = database.productDao().getTotalStockQuantity() ?: 0
                
                tvTotalProducts.text = totalProducts.toString()
                tvTotalQuantity.text = totalQuantity.toString()
                
            } catch (e: Exception) {
                Toast.makeText(
                    this@InventoryActivity,
                    "İstatistik yüklenemedi: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    /**
     * Envanter sayımını başlat
     */
    private fun startInventoryCount() {
        lifecycleScope.launch {
            isCounting = true
            startTime = System.currentTimeMillis()
            scannedTags.clear()
            
            btnStartCount.isEnabled = false
            btnStopCount.isEnabled = true
            btnGenerateReport.isEnabled = false
            
            // Konum filtresi
            val corridor = actvCorridor.text.toString()
            val shelf = actvShelf.text.toString()
            val level = actvLevel.text.toString()
            
            val locationFilter = if (corridor != "Tümü" && shelf != "Tümü" && level != "Tümü") {
                "$corridor-$shelf-$level"
            } else {
                null
            }
            
            Toast.makeText(
                this@InventoryActivity,
                if (locationFilter != null) "Sayım başladı: $locationFilter" else "Tüm depo sayımı başladı",
                Toast.LENGTH_SHORT
            ).show()
            
            // RFID okumaya başla
            rfidManager.startScanning { tag ->
                handleScannedTag(tag)
            }
            
            // Süre sayacını başlat
            startDurationCounter()
        }
    }
    
    /**
     * Envanter sayımını durdur
     */
    private fun stopInventoryCount() {
        isCounting = false
        rfidManager.stopScanning()
        
        btnStartCount.isEnabled = true
        btnStopCount.isEnabled = false
        btnGenerateReport.isEnabled = true
        
        val duration = (System.currentTimeMillis() - startTime) / 1000
        
        Toast.makeText(
            this,
            "Sayım tamamlandı! ${scannedTags.size} etiket okundu (${duration}s)",
            Toast.LENGTH_LONG
        ).show()
        
        // Sayım kaydını veritabanına kaydet
        saveInventoryCount(duration.toInt())
    }
    
    /**
     * Okunan RFID tag'i işle
     */
    private fun handleScannedTag(tag: RFIDTag) {
        if (!isCounting) return
        
        scannedTags.add(tag.epc)
        
        runOnUiThread {
            tvScannedTags.text = scannedTags.size.toString()
        }
    }
    
    /**
     * Süre sayacını başlat
     */
    private fun startDurationCounter() {
        lifecycleScope.launch {
            while (isCounting) {
                val duration = (System.currentTimeMillis() - startTime) / 1000
                runOnUiThread {
                    tvDuration.text = formatDuration(duration.toInt())
                }
                kotlinx.coroutines.delay(1000)
            }
        }
    }
    
    /**
     * Süreyi formatla (HH:MM:SS)
     */
    private fun formatDuration(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }
    
    /**
     * Sayım kaydını kaydet
     */
    private fun saveInventoryCount(durationSeconds: Int) {
        lifecycleScope.launch {
            try {
                val totalProducts = database.productDao().getTotalProductCount()
                val totalQuantity = database.productDao().getTotalStockQuantity() ?: 0
                val lowStockCount = database.productDao().getLowStockCount()
                val outOfStockCount = database.productDao().getOutOfStockCount()
                
                val inventoryCount = InventoryCountEntity(
                    totalProducts = totalProducts,
                    totalQuantity = totalQuantity,
                    lowStockCount = lowStockCount,
                    outOfStockCount = outOfStockCount,
                    rfidTagsRead = scannedTags.size,
                    durationSeconds = durationSeconds,
                    user = "System",
                    notes = "RFID ile envanter sayımı"
                )
                
                database.inventoryCountDao().insert(inventoryCount)
                
            } catch (e: Exception) {
                Toast.makeText(
                    this@InventoryActivity,
                    "Kayıt hatası: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    /**
     * Rapor oluştur ve göster
     */
    private fun generateReport() {
        lifecycleScope.launch {
            try {
                // Okunan tag'leri veritabanındaki ürünlerle eşleştir
                val matchedProducts = mutableListOf<String>()
                val unmatchedTags = mutableListOf<String>()
                
                for (tag in scannedTags) {
                    val product = database.productDao().findByRFIDTag(tag)
                    if (product != null) {
                        matchedProducts.add("${product.productCode} - ${product.name}")
                    } else {
                        unmatchedTags.add(tag)
                    }
                }
                
                // Eksik ürünleri bul (RFID'si var ama okunmamış)
                val allProductsWithRFID = database.productDao().getProductsWithRFID()
                val missingProducts = allProductsWithRFID.filter { product ->
                    product.rfidTag?.let { !scannedTags.contains(it) } ?: false
                }
                
                // Rapor oluştur
                val report = buildString {
                    appendLine("📊 ENVANTER SAYIM RAPORU")
                    appendLine("=" .repeat(40))
                    appendLine()
                    appendLine("📅 Tarih: ${java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}")
                    appendLine("⏱️ Süre: ${tvDuration.text}")
                    appendLine()
                    appendLine("📦 Toplam Ürün: ${tvTotalProducts.text}")
                    appendLine("📊 Toplam Miktar: ${tvTotalQuantity.text}")
                    appendLine("🏷️ Okunan Etiket: ${scannedTags.size}")
                    appendLine()
                    appendLine("✅ Eşleşen Ürün: ${matchedProducts.size}")
                    appendLine("❌ Eşleşmeyen Etiket: ${unmatchedTags.size}")
                    appendLine("⚠️ Eksik Ürün: ${missingProducts.size}")
                    appendLine()
                    
                    if (missingProducts.isNotEmpty()) {
                        appendLine("⚠️ EKSİK ÜRÜNLER:")
                        missingProducts.forEach {
                            appendLine("  • ${it.productCode} - ${it.name}")
                        }
                        appendLine()
                    }
                    
                    if (unmatchedTags.isNotEmpty()) {
                        appendLine("❌ EŞLEŞMEYEN ETİKETLER:")
                        unmatchedTags.forEach { tag ->
                            appendLine("  • $tag")
                        }
                    }
                }
                
                // Raporu göster
                showReportDialog(report)
                
            } catch (e: Exception) {
                Toast.makeText(
                    this@InventoryActivity,
                    "Rapor oluşturulamadı: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    /**
     * Rapor dialog'unu göster
     */
    private fun showReportDialog(report: String) {
        AlertDialog.Builder(this)
            .setTitle("Envanter Raporu")
            .setMessage(report)
            .setPositiveButton("Tamam", null)
            .setNeutralButton("Paylaş") { _, _ ->
                // Raporu paylaş (Email, WhatsApp, vb.)
                shareReport(report)
            }
            .show()
    }
    
    /**
     * Raporu paylaş
     */
    private fun shareReport(report: String) {
        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, report)
            type = "text/plain"
        }
        startActivity(android.content.Intent.createChooser(shareIntent, "Raporu Paylaş"))
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (isCounting) {
            stopInventoryCount()
        }
        rfidManager.release()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
