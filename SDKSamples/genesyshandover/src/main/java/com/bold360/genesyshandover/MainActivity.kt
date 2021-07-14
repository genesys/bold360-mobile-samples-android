package com.bold360.genesyshandover

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : History() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}