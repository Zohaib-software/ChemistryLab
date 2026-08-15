package com.zg.sciencegame

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.zg.sciencegame.databinding.ActivityMainBinding
import com.zg.sciencegame.databinding.SettingsDialogBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    
    var onGyroscopeSettingChanged: (() -> Unit)? = null

    companion object {
        private const val PREFS_NAME = "ScienceGameSettings"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_GYROSCOPE_ENABLED = "gyroscope_enabled"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        binding.settingsButton.setOnClickListener {
            showSettingsDialog()
        }
    }

    private fun showSettingsDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)

        val dialogBinding = SettingsDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)

        val soundEnabled = sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true)
        val vibrationEnabled = sharedPreferences.getBoolean(KEY_VIBRATION_ENABLED, true)
        val gyroscopeEnabled = sharedPreferences.getBoolean(KEY_GYROSCOPE_ENABLED, true)

        dialogBinding.soundSwitch.isChecked = soundEnabled
        dialogBinding.vibrationSwitch.isChecked = vibrationEnabled
        dialogBinding.gyroscopeSwitch.isChecked = gyroscopeEnabled

        dialogBinding.soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(KEY_SOUND_ENABLED, isChecked).apply()
        }

        dialogBinding.vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(KEY_VIBRATION_ENABLED, isChecked).apply()
        }

        dialogBinding.gyroscopeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(KEY_GYROSCOPE_ENABLED, isChecked).apply()
            onGyroscopeSettingChanged?.invoke()
        }

        dialogBinding.closeSettingsButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    fun isSoundEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true)
    }

    fun isVibrationEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_VIBRATION_ENABLED, true)
    }

    fun isGyroscopeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_GYROSCOPE_ENABLED, true)
    }
}
