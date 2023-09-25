package com.cesoft.cesardoom2

import android.content.Context
import android.media.SoundPool

//Sounds: https://mixkit.co/free-sound-effects/monster/
enum class Sound { Attack, Awake, Hurt, Gun }
object SoundFx {
    private var lastTime: Long = 0
    private lateinit var soundPool: SoundPool
    private var attack: Int = 0
    private var attackPlay: Int = 0
    private var awake: Int = 0
    private var awakePlay: Int = 0
    private var hurt: Int = 0
    private var hurtPlay: Int = 0
    private var gun: Int = 0
    private var gunPlay: Int = 0

    val lastTimePlayed: Float
        get() = (System.currentTimeMillis() - lastTime)/1000f

    fun init(context: Context) {
        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .build()
        soundPool.setOnLoadCompleteListener { _, _, status ->
            //if(status == 0) { } else { }
        }
        attack = soundPool.load(context, R.raw.roar, 1)
        awake = soundPool.load(context, R.raw.growl, 1)
        hurt = soundPool.load(context, R.raw.scream, 1)
        gun = soundPool.load(context, R.raw.gun, 1)
    }

    fun release() {
        stop()
        soundPool.release()
    }

    fun stop(sound: Sound? = null) {
        when(sound) {
            null -> {
                soundPool.stop(attackPlay)
                soundPool.stop(awakePlay)
                soundPool.stop(hurtPlay)
                soundPool.stop(gunPlay)
            }
            Sound.Attack -> {
                soundPool.stop(attackPlay)
            }
            Sound.Awake -> {
                soundPool.stop(awakePlay)
            }
            Sound.Hurt -> {
                soundPool.stop(hurtPlay)
            }
            Sound.Gun -> {
                soundPool.stop(gunPlay)
            }
        }
    }

    fun play(sound: Sound, loop: Boolean = false, distance2: Float = 0f) {
        lastTime = System.currentTimeMillis()
        val inLoop = if(loop) -1 else 0
        val priority = 0
        val rate = 1f
        var vol = 0.1f + 1f/distance2
        if(vol > 1f) vol = 1f
//        android.util.Log.e("Sound", "--------------play: $sound  loop: $loop  dis2: $distance2 ::: VOL=$vol")
        when(sound) {
            Sound.Attack -> {
                attackPlay = soundPool.play(attack, vol, vol, priority, inLoop, rate)
            }
            Sound.Awake -> {
                awakePlay = soundPool.play(awake, vol, vol, priority, inLoop, rate)
            }
            Sound.Hurt -> {
                hurtPlay = soundPool.play(hurt, vol, vol, priority, inLoop, rate)
            }
            Sound.Gun -> {
                gunPlay = soundPool.play(gun, vol, vol, priority, inLoop, rate)
            }
        }
    }
}