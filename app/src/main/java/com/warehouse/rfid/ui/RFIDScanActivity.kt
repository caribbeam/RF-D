package com.warehouse.rfid.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.warehouse.rfid.R
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
 */
class RFIDScanActivity : AppCompatActivity() {
    
    private lateinit var rfidManager: RFIDManager
    private lateinit var adapter: RFIDTagAdapter
    
    private lateinit var tvStatus: TextView
    private lateinit var tvTagCount: TextView
    private lateinit var btnStartScan: MaterialButton
    private lateinit var btnStopScan: MaterialButton
    private lateinit var btnClear: MaterialButton
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
        tvStatus = findViewById(R.id.tvStatus)
        tvTagCount = findViewById(R.id.tvTagCount)
        btnStartScan = findViewById(R.id.btnStartScan)
        btnStopScan = findViewById(R.id.btnStopScan)
        btnClear = findViewById(R.id.btnClear)
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
        adapter = RFIDTagAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        
        // StateFlow'dan tag listesini dinle - Otomatik UI güncellemesi
        lifecycleScope.launch {
            rfidManager.scannedTags.collectLatest { tags ->
                adapter.submitList(tags)
                updateTagCount(tags.size)
                updateEmptyState(tags.isEmpty())
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
class RFIDTagAdapter : RecyclerView.Adapter<RFIDTagAdapter.TagViewHolder>() {
    
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
