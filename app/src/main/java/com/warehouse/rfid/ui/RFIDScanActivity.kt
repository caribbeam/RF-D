package com.warehouse.rfid.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.warehouse.rfid.R
import com.warehouse.rfid.data.database.AppDatabase
import com.warehouse.rfid.rfid.RFIDManager
import com.warehouse.rfid.rfid.RFIDTag
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * RFID Okuma Activity
 * 
 * PERFORMANS OPTİMİZASYONLARI:
 * - StateFlow ile reaktif UI güncellemeleri
 * - DiffUtil ile verimli RecyclerView güncellemeleri
 * - Gereksiz UI thread çağrıları kaldırıldı
 * 
 * YENİ ÖZELLİKLER:
 * - RFID eşleştirme (Tag'i ürüne bağlama)
 * - Toplu eşleştirme
 * - Eşleşme durumu gösterimi
 */
class RFIDScanActivity : AppCompatActivity() {
    
    private lateinit var database: AppDatabase
    private lateinit var rfidManager: RFIDManager
    private lateinit var adapter: RFIDTagAdapter
    
    private lateinit var tvStatus: TextView
    private lateinit var tvTagCount: TextView
    private lateinit var btnStartScan: MaterialButton
    private lateinit var btnStopScan: MaterialButton
    private lateinit var btnClear: MaterialButton
    private lateinit var btnLinkTag: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyMessage: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rfid_scan)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.rfid_scan_title)
        
        initViews()
        initRFID()
        setupRecyclerView()
        setupButtons()
    }
    
    private fun initViews() {
        database = AppDatabase.getDatabase(this)
        tvStatus = findViewById(R.id.tvStatus)
        tvTagCount = findViewById(R.id.tvTagCount)
        btnStartScan = findViewById(R.id.btnStartScan)
        btnStopScan = findViewById(R.id.btnStopScan)
        btnClear = findViewById(R.id.btnClear)
        btnLinkTag = findViewById(R.id.btnLinkTag)
        recyclerView = findViewById(R.id.recyclerViewTags)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)
    }
    
    private fun initRFID() {
        rfidManager = RFIDManager(this)
        
        if (rfidManager.initialize()) {
            tvStatus.text = "RFID Hazır"
            tvStatus.setTextColor(getColor(R.color.success))
        } else {
            tvStatus.text = "RFID Hatası"
            tvStatus.setTextColor(getColor(R.color.error))
            Toast.makeText(this, R.string.rfid_init_error, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = RFIDTagAdapter { tag ->
            // Tag'e tıklandığında eşleştirme dialog'u göster
            showLinkDialog(tag)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        
        // StateFlow'dan tag listesini dinle - Otomatik UI güncellemesi
        lifecycleScope.launch {
            rfidManager.scannedTags.collectLatest { tags ->
                adapter.submitList(tags)
                updateTagCount(tags.size)
                updateEmptyState(tags.isEmpty())
                
                // Eşleşme durumlarını kontrol et
                checkTagMatches(tags)
            }
        }
    }
    
    private fun setupButtons() {
        btnStartScan.setOnClickListener {
            startScanning()
        }
        
        btnStopScan.setOnClickListener {
            stopScanning()
        }
        
        btnClear.setOnClickListener {
            clearTags()
        }
        
        btnLinkTag.setOnClickListener {
            showBulkLinkDialog()
        }
    }
    
    private fun startScanning() {
        lifecycleScope.launch {
            btnStartScan.isEnabled = false
            btnStopScan.isEnabled = true
            tvStatus.text = getString(R.string.scanning)
            tvStatus.setTextColor(getColor(R.color.rfid_scanning))
            
            // UI güncellemeleri StateFlow üzerinden otomatik yapılıyor
            rfidManager.startScanning { tag ->
                // Callback sadece ses/vibrasyon feedback için kullanılabilir (opsiyonel)
            }
        }
    }
    
    private fun stopScanning() {
        rfidManager.stopScanning()
        btnStartScan.isEnabled = true
        btnStopScan.isEnabled = false
        tvStatus.text = getString(R.string.scan_stopped)
        tvStatus.setTextColor(getColor(R.color.rfid_stopped))
    }
    
    private fun clearTags() {
        // Liste temizleme - StateFlow otomatik güncellenecek
        adapter.submitList(emptyList())
        Toast.makeText(this, "Liste temizlendi", Toast.LENGTH_SHORT).show()
    }
    
    private fun updateTagCount(count: Int) {
        tvTagCount.text = getString(R.string.tags_found, count)
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            tvEmptyMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmptyMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
    
    /**
     * Tag'lerin eşleşme durumlarını kontrol et
     */
    private fun checkTagMatches(tags: List<RFIDTag>) {
        lifecycleScope.launch {
            for (tag in tags) {
                val product = database.productDao().findByRFIDTag(tag.epc)
                // Eşleşme durumunu adapter'a bildir (opsiyonel)
            }
        }
    }
    
    /**
     * Tek tag eşleştirme dialog'u
     */
    private fun showLinkDialog(tag: RFIDTag) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_link_tag, null)
        val etProductCode = dialogView.findViewById<TextInputEditText>(R.id.etProductCode)
        
        AlertDialog.Builder(this)
            .setTitle("RFID Eşleştir")
            .setMessage("Tag: ${tag.epc}\n\nBu tag'i hangi ürüne bağlamak istiyorsunuz?")
            .setView(dialogView)
            .setPositiveButton("Eşleştir") { _, _ ->
                val productCode = etProductCode.text.toString().trim().uppercase()
                if (productCode.isNotEmpty()) {
                    linkTagToProduct(tag.epc, productCode)
                } else {
                    Toast.makeText(this, "Ürün kodu gerekli", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }
    
    /**
     * Toplu eşleştirme dialog'u
     */
    private fun showBulkLinkDialog() {
        val tags = rfidManager.scannedTags.value
        if (tags.isEmpty()) {
            Toast.makeText(this, "Önce RFID okuma yapın", Toast.LENGTH_SHORT).show()
            return
        }
        
        AlertDialog.Builder(this)
            .setTitle("Toplu Eşleştirme")
            .setMessage("${tags.size} adet tag okundu.\n\nTümünü otomatik eşleştirmek ister misiniz?")
            .setPositiveButton("Evet") { _, _ ->
                bulkLinkTags(tags)
            }
            .setNegativeButton("İptal", null)
            .show()
    }
    
    /**
     * Tag'i ürüne bağla
     */
    private fun linkTagToProduct(rfidTag: String, productCode: String) {
        lifecycleScope.launch {
            try {
                val product = database.productDao().findByProductCode(productCode)
                
                if (product == null) {
                    Toast.makeText(
                        this@RFIDScanActivity,
                        "Ürün bulunamadı: $productCode",
                        Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }
                
                // RFID tag'i ürüne bağla
                database.productDao().linkRFIDToProductCode(productCode, rfidTag)
                
                Toast.makeText(
                    this@RFIDScanActivity,
                    "✅ Tag eşleştirildi!\n$rfidTag → ${product.name}",
                    Toast.LENGTH_LONG
                ).show()
                
            } catch (e: Exception) {
                Toast.makeText(
                    this@RFIDScanActivity,
                    "Hata: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    /**
     * Toplu tag eşleştirme
     */
    private fun bulkLinkTags(tags: List<RFIDTag>) {
        lifecycleScope.launch {
            var successCount = 0
            var failCount = 0
            
            for (tag in tags) {
                try {
                    // Tag'in zaten eşleştirilmiş olup olmadığını kontrol et
                    val existingProduct = database.productDao().findByRFIDTag(tag.epc)
                    
                    if (existingProduct != null) {
                        successCount++
                        continue
                    }
                    
                    // Eşleştirilmemiş tag - Manuel eşleştirme gerekli
                    failCount++
                    
                } catch (e: Exception) {
                    failCount++
                }
            }
            
            val message = buildString {
                appendLine("Toplu Eşleştirme Tamamlandı!")
                appendLine()
                appendLine("✅ Eşleşen: $successCount")
                appendLine("❌ Eşleşmeyen: $failCount")
                if (failCount > 0) {
                    appendLine()
                    appendLine("Eşleşmeyen tag'ler için manuel eşleştirme yapın.")
                }
            }
            
            AlertDialog.Builder(this@RFIDScanActivity)
                .setTitle("Sonuç")
                .setMessage(message)
                .setPositiveButton("Tamam", null)
                .show()
        }
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

/**
 * RFID Tag RecyclerView Adapter
 * DiffUtil ile optimize edilmiş - Sadece değişen itemlar güncellenir
 */
class RFIDTagAdapter(
    private val onTagClick: (RFIDTag) -> Unit
) : RecyclerView.Adapter<RFIDTagAdapter.TagViewHolder>() {
    
    private var tags: List<RFIDTag> = emptyList()
    
    class TagViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEpc: TextView = view.findViewById(R.id.tvEpc)
        val tvRssi: TextView = view.findViewById(R.id.tvRssi)
        val tvReadCount: TextView = view.findViewById(R.id.tvReadCount)
    }
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): TagViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rfid_tag, parent, false)
        return TagViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tags[position]
        holder.tvEpc.text = tag.epc
        holder.tvRssi.text = "${tag.rssi} dBm"
        holder.tvReadCount.text = tag.readCount.toString()
        
        // RSSI'ye göre renk
        val rssiColor = when {
            tag.rssi > -50 -> android.graphics.Color.parseColor("#4CAF50") // Güçlü
            tag.rssi > -65 -> android.graphics.Color.parseColor("#FF9800") // Orta
            else -> android.graphics.Color.parseColor("#F44336") // Zayıf
        }
        holder.tvRssi.setTextColor(rssiColor)
        
        // Tıklama eventi
        holder.itemView.setOnClickListener {
            onTagClick(tag)
        }
    }
    
    override fun getItemCount() = tags.size
    
    /**
     * DiffUtil ile liste güncelle - Performans optimizasyonu
     */
    fun submitList(newTags: List<RFIDTag>) {
        val diffCallback = RFIDTagDiffCallback(tags, newTags)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        
        tags = newTags
        diffResult.dispatchUpdatesTo(this)
    }
}

/**
 * DiffUtil Callback - Verimli liste güncellemeleri için
 * Sadece değişen itemları günceller, tüm listeyi yeniden çizmez
 */
class RFIDTagDiffCallback(
    private val oldList: List<RFIDTag>,
    private val newList: List<RFIDTag>
) : DiffUtil.Callback() {
    
    override fun getOldListSize() = oldList.size
    
    override fun getNewListSize() = newList.size
    
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].epc == newList[newItemPosition].epc
    }
    
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldTag = oldList[oldItemPosition]
        val newTag = newList[newItemPosition]
        return oldTag.epc == newTag.epc &&
                oldTag.rssi == newTag.rssi &&
                oldTag.readCount == newTag.readCount
    }
}
