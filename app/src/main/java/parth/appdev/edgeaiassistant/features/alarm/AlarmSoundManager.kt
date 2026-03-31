package parth.appdev.edgeaiassistant.features.alarm

import android.media.Ringtone

object AlarmSoundManager {

    var ringtone: Ringtone? = null  // ✅ MUST be var

    fun stop() {
        ringtone?.stop()
        ringtone = null
    }
}