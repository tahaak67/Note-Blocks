package com.example.taha.noteblocks

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_settings.*

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    override fun onResume() {
        super.onResume()
        val saveSettings =SaveSettings(this)
        isShakeActive.isChecked= saveSettings.loadSettings()
    }

    override fun onPause() {
        super.onPause()
        val saveSettings = SaveSettings(this)
        saveSettings.saveSettings(isShakeActive.isChecked)
        serviceControl()
        finish()

    }
    fun serviceControl (){
        val saveSettings = SaveSettings(this)
        val isSerChecked = saveSettings.loadSettings()
        if (isSerChecked) {
            startService(Intent(applicationContext,ShakeSensorService::class.java))
        }else {
            stopService(Intent(applicationContext,ShakeSensorService::class.java))
        }

    }
}
