package com.example.kindle.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.kindle.R

class cart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        supportActionBar?.hide()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val intent = Intent(this@cart, MainActivity::class.java)
                startActivity(intent)
                finish()
            },
            3000,
        )
    }
}
