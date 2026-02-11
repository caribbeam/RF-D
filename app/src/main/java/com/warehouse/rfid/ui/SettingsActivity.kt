package com.warehouse.rfid.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.warehouse.rfid.R

/**
 * Ayarlar Activity
 */
class SettingsActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings_title)
        
        // Basit bir mesaj göster
        Toast.makeText(
            this,
            "Ayarlar modülü geliştirme aşamasında",
            Toast.LENGTH_SHORT
        ).show()
        
        finish()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
