package com.warehouse.rfid.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.warehouse.rfid.R
import com.warehouse.rfid.data.database.AppDatabase
import com.warehouse.rfid.user.UserManager
import com.warehouse.rfid.user.LoginResult
import kotlinx.coroutines.launch

/**
 * Giriş Activity
 * Kullanıcı kimlik doğrulama
 */
class LoginActivity : AppCompatActivity() {
    
    private lateinit var userManager: UserManager
    private lateinit var database: AppDatabase
    
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        database = AppDatabase.getDatabase(this)
        userManager = UserManager(this, database)
        
        // Zaten giriş yapılmışsa ana ekrana git
        if (userManager.isLoggedIn()) {
            navigateToMain()
            return
        }
        
        initViews()
        setupButtons()
    }
    
    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
    }
    
    private fun setupButtons() {
        btnLogin.setOnClickListener {
            performLogin()
        }
    }
    
    private fun performLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        
        // Validasyon
        if (username.isEmpty()) {
            etUsername.error = "Kullanıcı adı gerekli"
            etUsername.requestFocus()
            return
        }
        
        if (password.isEmpty()) {
            etPassword.error = "Şifre gerekli"
            etPassword.requestFocus()
            return
        }
        
        // Giriş yap
        btnLogin.isEnabled = false
        btnLogin.text = "Giriş yapılıyor..."
        
        lifecycleScope.launch {
            try {
                when (val result = userManager.login(username, password)) {
                    is LoginResult.Success -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "Hoş geldiniz, ${result.user.fullName}",
                            Toast.LENGTH_SHORT
                        ).show()
                        
                        navigateToMain()
                    }
                    is LoginResult.Error -> {
                        Toast.makeText(
                            this@LoginActivity,
                            result.message,
                            Toast.LENGTH_LONG
                        ).show()
                        
                        btnLogin.isEnabled = true
                        btnLogin.text = "Giriş Yap"
                        etPassword.text?.clear()
                        etPassword.requestFocus()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Giriş hatası: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                
                btnLogin.isEnabled = true
                btnLogin.text = "Giriş Yap"
            }
        }
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    override fun onBackPressed() {
        // Geri tuşunu devre dışı bırak (giriş zorunlu)
        // super.onBackPressed()
    }
}
