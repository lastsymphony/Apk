package com.lastsymp.smsrelay

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

class MainActivity : ComponentActivity() {

    private lateinit var statusText: TextView
    private lateinit var btnPerm: Button
    private lateinit var btnSmsDefaultInfo: Button

    private val requestPermLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        // Setelah user jawab izin, update status
        updateStatusText()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        btnPerm = findViewById(R.id.btnPerm)
        btnSmsDefaultInfo = findViewById(R.id.btnSmsDefaultInfo)

        btnPerm.setOnClickListener {
            requestPermLauncher.launch(
                arrayOf(
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.INTERNET
                )
            )
        }

        btnSmsDefaultInfo.setOnClickListener {
            // bukain settings default SMS biar user bisa set app ini jadi SMS default kalau dibutuhin
            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(intent)
        }

        updateStatusText()
    }

    private fun updateStatusText() {
        val r1 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
        val r2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
        val net = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED

        val okAll = r1 && r2 && net

        statusText.text = if (okAll) {
            "STATUS: ✅ Listener aktif.\nSMS baru akan otomatis dikirim ke Telegram."
        } else {
            "STATUS: ❌ Belum punya izin.\nTap \"Grant Permission\"."
        }
    }
}