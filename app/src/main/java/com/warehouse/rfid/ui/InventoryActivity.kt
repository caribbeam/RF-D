package com.warehouse.rfid.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.warehouse.rfid.R

/**
 * Envanter Sayımı Activity
 */
class InventoryActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.inventory_title)
        
        // Basit bir mesaj göster
        Toast.makeText(
            this,
            "Envanter modülü geliştirme aşamasında",
            Toast.LENGTH_SHORT
        ).show()
        
        finish()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
