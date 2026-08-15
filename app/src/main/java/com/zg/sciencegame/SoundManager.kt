package com.zg.sciencegame

import android.content.Context
import android.media.SoundPool

class SoundManager private constructor(context: Context) {
    
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .build()
    
    private var successSoundId: Int = 0
    private var errorSoundId: Int = 0
    private var levelUpSoundId: Int = 0
    private var chemicalIntoFlaskSoundId: Int = 0
    
    init {
        successSoundId = soundPool.load(context, R.raw.success_sound, 1)
        errorSoundId = soundPool.load(context, R.raw.error_sound, 1)
        levelUpSoundId = soundPool.load(context, R.raw.level_up_sound, 1)
        chemicalIntoFlaskSoundId = soundPool.load(context, R.raw.chemical_into_flask, 1)
    }
    
    fun playSuccess() {
        soundPool.play(successSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }
    
    fun playError() {
        soundPool.play(errorSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }
    
    fun playLevelUp() {
        soundPool.play(levelUpSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }
    
    fun playChemicalIntoFlask() {
        soundPool.play(chemicalIntoFlaskSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }
    
    fun release() {
        soundPool.release()
    }
    
    companion object {
        @Volatile
        private var INSTANCE: SoundManager? = null
        
        fun getInstance(context: Context): SoundManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SoundManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

