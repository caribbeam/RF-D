package com.warehouse.rfid

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.warehouse.rfid.ui.*

/**
 * Ana Activity - Menü ekranı
 */
class MainActivity : AppCompatActivity() {
    
    private val PERMISSION_REQUEST_CODE = 100
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // İzinleri kontrol et
        checkPermissions()
        
        // Menü kartlarını ayarla
        setupMenuCards()
    }
    
    private fun setupMenuCards() {
        // Ürün Girişi
        findViewById<MaterialCardView>(R.id.cardProductEntry).setOnClickListener {
            startActivity(Intent(this, ProductEntryActivity::class.java))
        }
        
        // RFID Okuma
        findViewById<MaterialCardView>(R.id.cardRFIDScan).setOnClickListener {
            startActivity(Intent(this, RFIDScanActivity::class.java))
        }
        
        // Envanter
        findViewById<MaterialCardView>(R.id.cardInventory).setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }
        
        // Barkod Yazdır
        findViewById<MaterialCardView>(R.id.cardPrinter).setOnClickListener {
            startActivity(Intent(this, PrinterActivity::class.java))
        }
        
        // Ürün Listesi
        findViewById<MaterialCardView>(R.id.cardProductList).setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }
        
        // Ayarlar
        findViewById<MaterialCardView>(R.id.cardSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
    
    private fun checkPermissions() {
        val permissions = mutableListOf<String>()
        
        // Bluetooth izinleri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
        
        // Konum izni (Bluetooth için gerekli)
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        
        // Kamera izni (Barkod okuma için)
        permissions.add(Manifest.permission.CAMERA)
        
        // İnternet izni (API için)
        permissions.add(Manifest.permission.INTERNET)
        
        // Depolama izni
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        // İzin kontrolü
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = permissions.filterIndexed { index, _ ->
                grantResults[index] != PackageManager.PERMISSION_GRANTED
            }
            
            if (deniedPermissions.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "Bazı izinler reddedildi. Uygulama tam olarak çalışmayabilir.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
