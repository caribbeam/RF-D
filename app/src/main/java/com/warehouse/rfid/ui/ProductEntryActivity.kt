package com.warehouse.rfid.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.warehouse.rfid.R
import com.warehouse.rfid.data.database.AppDatabase
import com.warehouse.rfid.data.database.ProductEntity
import com.warehouse.rfid.data.database.StockMovementEntity
import com.warehouse.rfid.data.database.MovementType
import com.warehouse.rfid.rfid.RFIDManager
import com.warehouse.rfid.printer.PrinterManager
import kotlinx.coroutines.launch

/**
 * Ürün Girişi Activity
 * Güncellenmiş versiyon - Tüm yeni özelliklerle
 */
class ProductEntryActivity : AppCompatActivity() {
    
    private lateinit var database: AppDatabase
    private lateinit var rfidManager: RFIDManager
    private lateinit var printerManager: PrinterManager
    
    // Ürün Bilgileri
    private lateinit var etProductCode: TextInputEditText
    private lateinit var etProductName: TextInputEditText
    private lateinit var etQuantity: TextInputEditText
    private lateinit var actvUnit: AutoCompleteTextView
    private lateinit var etMinStock: TextInputEditText
    
    // Konum Bilgileri
    private lateinit var actvCorridor: AutoCompleteTextView
    private lateinit var actvShelf: AutoCompleteTextView
    private lateinit var actvLevel: AutoCompleteTextView
    
    // RFID ve Barkod
    private lateinit var etRfidTag: TextInputEditText
    private lateinit var etBarcode: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    
    // Butonlar
    private lateinit var btnScanRfid: MaterialButton
    private lateinit var btnScanBarcode: MaterialButton
    private lateinit var btnSave: MaterialButton
    private lateinit var btnPrintLabel: MaterialButton
    
    // Dropdown seçenekleri
    private val units = arrayOf("Adet", "Koli", "Palet", "Kg", "Ton", "Litre", "Metre")
    private val corridors = arrayOf("A", "B", "C", "D", "E", "F")
    private val shelves = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    private val levels = arrayOf("Üst", "Orta", "Alt")
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_entry)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.product_entry_title)
        
        database = AppDatabase.getDatabase(this)
        rfidManager = RFIDManager(this)
        rfidManager.initialize()
        printerManager = PrinterManager(this)
        
        initViews()
        setupDropdowns()
        setupButtons()
    }
    
    private fun initViews() {
        // Ürün Bilgileri
        etProductCode = findViewById(R.id.etProductCode)
        etProductName = findViewById(R.id.etProductName)
        etQuantity = findViewById(R.id.etQuantity)
        actvUnit = findViewById(R.id.actvUnit)
        etMinStock = findViewById(R.id.etMinStock)
        
        // Konum Bilgileri
        actvCorridor = findViewById(R.id.actvCorridor)
        actvShelf = findViewById(R.id.actvShelf)
        actvLevel = findViewById(R.id.actvLevel)
        
        // RFID ve Barkod
        etRfidTag = findViewById(R.id.etRfidTag)
        etBarcode = findViewById(R.id.etBarcode)
        etDescription = findViewById(R.id.etDescription)
        
        // Butonlar
        btnScanRfid = findViewById(R.id.btnScanRfid)
        btnScanBarcode = findViewById(R.id.btnScanBarcode)
        btnSave = findViewById(R.id.btnSave)
        btnPrintLabel = findViewById(R.id.btnPrintLabel)
    }
    
    private fun setupDropdowns() {
        // Birim dropdown
        val unitAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, units)
        actvUnit.setAdapter(unitAdapter)
        
        // Koridor dropdown
        val corridorAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, corridors)
        actvCorridor.setAdapter(corridorAdapter)
        
        // Raf dropdown
        val shelfAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, shelves)
        actvShelf.setAdapter(shelfAdapter)
        
        // Seviye dropdown
        val levelAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, levels)
        actvLevel.setAdapter(levelAdapter)
    }
    
    private fun setupButtons() {
        btnScanRfid.setOnClickListener {
            scanRFID()
        }
        
        btnScanBarcode.setOnClickListener {
            scanBarcode()
        }
        
        btnSave.setOnClickListener {
            saveProduct()
        }
        
        btnPrintLabel.setOnClickListener {
            printLabel()
        }
        
        // Ürün kodu değiştiğinde kontrol et
        etProductCode.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                checkExistingProduct()
            }
        }
    }
    
    /**
     * Aynı ürün kodu var mı kontrol et (KRİTİK)
     */
    private fun checkExistingProduct() {
        val productCode = etProductCode.text.toString().trim().uppercase()
        if (productCode.isEmpty()) return
        
        lifecycleScope.launch {
            try {
                val existingProduct = database.productDao().findByProductCode(productCode)
                
                if (existingProduct != null) {
                    // Ürün var - Bilgileri doldur
                    Toast.makeText(
                        this@ProductEntryActivity,
                        "Bu ürün zaten kayıtlı! Miktar eklenecek.",
                        Toast.LENGTH_LONG
                    ).show()
                    
                    fillProductInfo(existingProduct)
                }
            } catch (e: Exception) {
                // Hata - devam et
            }
        }
    }
    
    /**
     * Mevcut ürün bilgilerini doldur
     */
    private fun fillProductInfo(product: ProductEntity) {
        etProductName.setText(product.name)
        actvUnit.setText(product.unit, false)
        etMinStock.setText(product.minStockLevel.toString())
        
        product.corridor?.let { actvCorridor.setText(it, false) }
        product.shelf?.let { actvShelf.setText(it, false) }
        product.level?.let { actvLevel.setText(it, false) }
        
        product.rfidTag?.let { etRfidTag.setText(it) }
        product.barcode?.let { etBarcode.setText(it) }
        product.description?.let { etDescription.setText(it) }
    }
    
    private fun scanRFID() {
        lifecycleScope.launch {
            btnScanRfid.isEnabled = false
            btnScanRfid.text = "Okuma yapılıyor..."
            
            try {
                val tag = rfidManager.readSingleTag()
                if (tag != null) {
                    etRfidTag.setText(tag.epc)
                    
                    // RFID ile ürün var mı kontrol et
                    val existingProduct = database.productDao().findByRFIDTag(tag.epc)
                    if (existingProduct != null) {
                        Toast.makeText(
                            this@ProductEntryActivity,
                            "Bu RFID etiketi ${existingProduct.name} ürününe kayıtlı",
                            Toast.LENGTH_LONG
                        ).show()
                        fillProductInfo(existingProduct)
                    } else {
                        Toast.makeText(
                            this@ProductEntryActivity,
                            "RFID okundu: ${tag.epc}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ProductEntryActivity,
                        "RFID okunamadı",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ProductEntryActivity,
                    "Hata: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                btnScanRfid.isEnabled = true
                btnScanRfid.text = getString(R.string.scan_rfid)
            }
        }
    }
    
    private fun scanBarcode() {
        // Barkod okuma için ZXing kütüphanesi kullanılabilir
        // Şimdilik basit bir demo
        Toast.makeText(this, "Barkod okuma özelliği yakında eklenecek", Toast.LENGTH_SHORT).show()
        
        // Demo için rastgele barkod
        val demoBarcode = "123456789${(1000..9999).random()}"
        etBarcode.setText(demoBarcode)
    }
    
    /**
     * Ürünü kaydet veya güncelle (KRİTİK)
     */
    private fun saveProduct() {
        val productCode = etProductCode.text.toString().trim().uppercase()
        val name = etProductName.text.toString().trim()
        val quantityStr = etQuantity.text.toString().trim()
        val unit = actvUnit.text.toString().trim()
        val minStockStr = etMinStock.text.toString().trim()
        
        val corridor = actvCorridor.text.toString().trim()
        val shelf = actvShelf.text.toString().trim()
        val level = actvLevel.text.toString().trim()
        
        val rfidTag = etRfidTag.text.toString().trim()
        val barcode = etBarcode.text.toString().trim()
        val description = etDescription.text.toString().trim()
        
        // Validasyon
        if (productCode.isEmpty()) {
            Toast.makeText(this, "Ürün kodu gerekli", Toast.LENGTH_SHORT).show()
            etProductCode.requestFocus()
            return
        }
        
        if (name.isEmpty()) {
            Toast.makeText(this, "Ürün adı gerekli", Toast.LENGTH_SHORT).show()
            etProductName.requestFocus()
            return
        }
        
        val quantity = quantityStr.toIntOrNull() ?: 1
        val minStock = minStockStr.toIntOrNull() ?: 5
        
        // Ürünü kaydet veya güncelle
        lifecycleScope.launch {
            try {
                val existingProduct = database.productDao().findByProductCode(productCode)
                
                if (existingProduct != null) {
                    // ÜRÜN VAR - Miktarı artır (KRİTİK)
                    val previousQty = existingProduct.quantity
                    val newQty = previousQty + quantity
                    
                    database.productDao().increaseQuantity(productCode, quantity)
                    
                    // RFID tag'i güncelle (varsa)
                    if (rfidTag.isNotEmpty() && existingProduct.rfidTag != rfidTag) {
                        database.productDao().linkRFIDToProductCode(productCode, rfidTag)
                    }
                    
                    // Stok hareketi kaydet
                    val movement = StockMovementEntity(
                        productId = existingProduct.id,
                        productCode = productCode,
                        movementType = MovementType.IN,
                        quantity = quantity,
                        previousQuantity = previousQty,
                        newQuantity = newQty,
                        user = "System",
                        notes = "Ürün girişi - Miktar artırıldı"
                    )
                    database.stockMovementDao().insert(movement)
                    
                    Toast.makeText(
                        this@ProductEntryActivity,
                        "Ürün güncellendi! Yeni miktar: $newQty $unit",
                        Toast.LENGTH_LONG
                    ).show()
                    
                } else {
                    // YENİ ÜRÜN - Kaydet
                    val product = ProductEntity(
                        productCode = productCode,
                        name = name,
                        quantity = quantity,
                        unit = unit,
                        minStockLevel = minStock,
                        corridor = corridor.ifEmpty { null },
                        shelf = shelf.ifEmpty { null },
                        level = level.ifEmpty { null },
                        rfidTag = rfidTag.ifEmpty { null },
                        barcode = barcode.ifEmpty { null },
                        description = description.ifEmpty { null }
                    )
                    
                    val productId = database.productDao().insert(product)
                    
                    // Stok hareketi kaydet
                    val movement = StockMovementEntity(
                        productId = productId,
                        productCode = productCode,
                        movementType = MovementType.IN,
                        quantity = quantity,
                        previousQuantity = 0,
                        newQuantity = quantity,
                        user = "System",
                        notes = "İlk ürün girişi"
                    )
                    database.stockMovementDao().insert(movement)
                    
                    Toast.makeText(
                        this@ProductEntryActivity,
                        "Yeni ürün kaydedildi!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
                // Formu temizle
                clearForm()
                
            } catch (e: Exception) {
                Toast.makeText(
                    this@ProductEntryActivity,
                    "Hata: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    /**
     * Barkod/RFID etiketi yazdır
     */
    private fun printLabel() {
        val productCode = etProductCode.text.toString().trim().uppercase()
        val name = etProductName.text.toString().trim()
        val quantityStr = etQuantity.text.toString().trim()
        
        if (productCode.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Ürün kodu ve adı gerekli", Toast.LENGTH_SHORT).show()
            return
        }
        
        val quantity = quantityStr.toIntOrNull() ?: 1
        
        lifecycleScope.launch {
            try {
                val corridor = actvCorridor.text.toString().trim()
                val shelf = actvShelf.text.toString().trim()
                val level = actvLevel.text.toString().trim()
                val location = if (corridor.isNotEmpty() && shelf.isNotEmpty() && level.isNotEmpty()) {
                    "$corridor-$shelf-$level"
                } else {
                    "Konum Yok"
                }
                
                // Zebra yazıcıdan etiket yazdır
                val success = printerManager.printRFIDLabel(
                    productCode = productCode,
                    productName = name,
                    location = location,
                    quantity = quantity
                )
                
                if (success) {
                    Toast.makeText(
                        this@ProductEntryActivity,
                        "$quantity adet etiket yazdırılıyor...",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@ProductEntryActivity,
                        "Yazıcı bağlantısı kurulamadı",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ProductEntryActivity,
                    "Yazdırma hatası: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun clearForm() {
        etProductCode.text?.clear()
        etProductName.text?.clear()
        etQuantity.setText("1")
        actvUnit.setText("Adet", false)
        etMinStock.setText("5")
        
        actvCorridor.text?.clear()
        actvShelf.text?.clear()
        actvLevel.text?.clear()
        
        etRfidTag.text?.clear()
        etBarcode.text?.clear()
        etDescription.text?.clear()
        
        etProductCode.requestFocus()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        rfidManager.release()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
