package com.warehouse.rfid.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.warehouse.rfid.R
import com.warehouse.rfid.data.database.AppDatabase
import com.warehouse.rfid.data.database.ProductEntity
import com.warehouse.rfid.rfid.RFIDManager
import kotlinx.coroutines.launch

/**
 * Ürün Girişi Activity
 */
class ProductEntryActivity : AppCompatActivity() {
    
    private lateinit var database: AppDatabase
    private lateinit var rfidManager: RFIDManager
    
    private lateinit var etRfidTag: TextInputEditText
    private lateinit var etBarcode: TextInputEditText
    private lateinit var etProductName: TextInputEditText
    private lateinit var etQuantity: TextInputEditText
    private lateinit var etLocation: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    
    private lateinit var btnScanRfid: MaterialButton
    private lateinit var btnScanBarcode: MaterialButton
    private lateinit var btnSave: MaterialButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_entry)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.product_entry_title)
        
        database = AppDatabase.getDatabase(this)
        rfidManager = RFIDManager(this)
        rfidManager.initialize()
        
        initViews()
        setupButtons()
    }
    
    private fun initViews() {
        etRfidTag = findViewById(R.id.etRfidTag)
        etBarcode = findViewById(R.id.etBarcode)
        etProductName = findViewById(R.id.etProductName)
        etQuantity = findViewById(R.id.etQuantity)
        etLocation = findViewById(R.id.etLocation)
        etDescription = findViewById(R.id.etDescription)
        
        btnScanRfid = findViewById(R.id.btnScanRfid)
        btnScanBarcode = findViewById(R.id.btnScanBarcode)
        btnSave = findViewById(R.id.btnSave)
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
    }
    
    private fun scanRFID() {
        lifecycleScope.launch {
            btnScanRfid.isEnabled = false
            btnScanRfid.text = "Okuma yapılıyor..."
            
            try {
                val tag = rfidManager.readSingleTag()
                if (tag != null) {
                    etRfidTag.setText(tag.epc)
                    Toast.makeText(
                        this@ProductEntryActivity,
                        "RFID okundu: ${tag.epc}",
                        Toast.LENGTH_SHORT
                    ).show()
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
    
    private fun saveProduct() {
        val rfidTag = etRfidTag.text.toString().trim()
        val barcode = etBarcode.text.toString().trim()
        val name = etProductName.text.toString().trim()
        val quantityStr = etQuantity.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val description = etDescription.text.toString().trim()
        
        // Validasyon
        if (rfidTag.isEmpty()) {
            Toast.makeText(this, "RFID etiketi gerekli", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (name.isEmpty()) {
            Toast.makeText(this, "Ürün adı gerekli", Toast.LENGTH_SHORT).show()
            return
        }
        
        val quantity = quantityStr.toIntOrNull() ?: 1
        
        // Ürünü kaydet
        lifecycleScope.launch {
            try {
                // Aynı RFID etiketli ürün var mı kontrol et
                val existingProduct = database.productDao().getProductByRfidTag(rfidTag)
                
                if (existingProduct != null) {
                    Toast.makeText(
                        this@ProductEntryActivity,
                        "Bu RFID etiketi zaten kayıtlı",
                        Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }
                
                val product = ProductEntity(
                    rfidTag = rfidTag,
                    barcode = barcode.ifEmpty { null },
                    name = name,
                    description = description.ifEmpty { null },
                    quantity = quantity,
                    location = location.ifEmpty { null }
                )
                
                database.productDao().insertProduct(product)
                
                Toast.makeText(
                    this@ProductEntryActivity,
                    getString(R.string.product_saved),
                    Toast.LENGTH_SHORT
                ).show()
                
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
    
    private fun clearForm() {
        etRfidTag.text?.clear()
        etBarcode.text?.clear()
        etProductName.text?.clear()
        etQuantity.setText("1")
        etLocation.text?.clear()
        etDescription.text?.clear()
        etRfidTag.requestFocus()
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
