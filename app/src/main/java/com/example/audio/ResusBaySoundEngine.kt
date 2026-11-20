package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class ResusBaySoundEngine {

    private var audioJob: Job? = null
    @Volatile
    var isAudioEnabled: Boolean = true
    @Volatile
    var isAlarmSilenced: Boolean = false
    @Volatile
    var currentHr: Int = 96
    @Volatile
    var currentRr: Int = 20
    @Volatile
    var currentSpo2: Int = 98
    @Volatile
    var isVentilatorActive: Boolean = false
    @Volatile
    var isCriticalAlarmActive: Boolean = false

    fun updateTelemetry(
        hr: Int,
        rr: Int,
        spo2: Int?,
        ventilatorOn: Boolean,
        criticalAlarm: Boolean
    ) {
        currentHr = hr.coerceIn(30, 240)
        currentRr = rr.coerceIn(6, 70)
        currentSpo2 = (spo2 ?: 92).coerceIn(70, 100)
        isVentilatorActive = ventilatorOn
        isCriticalAlarmActive = criticalAlarm
    }

    fun start(scope: CoroutineScope) {
        if (audioJob?.isActive == true) return
        audioJob = scope.launch(Dispatchers.Default) {
            val sampleRate = 22050
            val minBuffer = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(2048)

            val track = try {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBuffer)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            } catch (_: Throwable) {
                null
            } ?: return@launch

            try {
                track.play()
                val chunkSamples = 512
                val buffer = ShortArray(chunkSamples)
                var globalSampleIndex = 0L
                var noiseSeed = 0x13579BDF

                while (isActive) {
                    val enabled = isAudioEnabled
                    val hr = currentHr
                    val rr = currentRr
                    val spo2 = currentSpo2
                    val ventOn = isVentilatorActive
                    val alarmOn = isCriticalAlarmActive && !isAlarmSilenced

                    val samplesPerBeat = (sampleRate * 60.0 / hr).toLong().coerceAtLeast(2500L)
                    val beepDurationSamples = (sampleRate * 0.065).toLong() // 65ms crisp QRS beep
                    // Pitch modulated by SpO2 like a real ICU pulse oximeter (620 Hz at 80% -> 940 Hz at 100%)
                    val beepFreq = 620.0 + (spo2 - 80).coerceIn(0, 20) * 16.0

                    val samplesPerBreath = (sampleRate * 60.0 / rr).toLong().coerceAtLeast(8000L)
                    val inspDurationSamples = (samplesPerBreath * 0.35).toLong()

                    val alarmCycleSamples = (sampleRate * 1.4).toLong()

                    for (i in 0 until chunkSamples) {
                        if (!enabled) {
                            buffer[i] = 0
                            globalSampleIndex++
                            continue
                        }

                        var mixed = 0.0

                        // 1. ECG / Pulse Oximeter Beep
                        val beatPhaseSample = globalSampleIndex % samplesPerBeat
                        if (beatPhaseSample < beepDurationSamples) {
                            val env = sin(PI * beatPhaseSample.toDouble() / beepDurationSamples)
                            val t = globalSampleIndex.toDouble() / sampleRate
                            mixed += 0.24 * env * sin(2.0 * PI * beepFreq * t)
                        }

                        // 2. ICU Ambient Hum + Mechanical Ventilator Pneumatic Cycle
                        noiseSeed = noiseSeed xor (noiseSeed shl 13)
                        noiseSeed = noiseSeed xor (noiseSeed ushr 17)
                        noiseSeed = noiseSeed xor (noiseSeed shl 5)
                        val whiteNoise = ((noiseSeed and 0x7FFF) / 16384.0) - 1.0

                        val breathPhase = globalSampleIndex % samplesPerBreath
                        if (ventOn && breathPhase < inspDurationSamples) {
                            val ventEnv = sin(PI * breathPhase.toDouble() / inspDurationSamples)
                            mixed += 0.045 * ventEnv * whiteNoise
                        } else {
                            // Subtle room HVAC / resuscitation bay ambient floor
                            mixed += 0.008 * whiteNoise
                        }

                        // 3. Physiological High-Priority Dual-Tone Alarm (if vitals critical & not silenced)
                        if (alarmOn) {
                            val alarmPhase = globalSampleIndex % alarmCycleSamples
                            val toneAEnd = (sampleRate * 0.11).toLong()
                            val toneBStart = (sampleRate * 0.16).toLong()
                            val toneBEnd = (sampleRate * 0.27).toLong()
                            val t = globalSampleIndex.toDouble() / sampleRate
                            if (alarmPhase < toneAEnd) {
                                val env = sin(PI * alarmPhase.toDouble() / toneAEnd)
                                mixed += 0.16 * env * sin(2.0 * PI * 987.77 * t) // B5 medical alarm tone
                            } else if (alarmPhase in toneBStart until toneBEnd) {
                                val env = sin(PI * (alarmPhase - toneBStart).toDouble() / (toneBEnd - toneBStart))
                                mixed += 0.16 * env * sin(2.0 * PI * 783.99 * t) // G5 medical alarm tone
                            }
                        }

                        val clamped = (mixed * 32767.0).toInt().coerceIn(-32760, 32760)
                        buffer[i] = clamped.toShort()
                        globalSampleIndex++
                    }

                    track.write(buffer, 0, chunkSamples)
                }
            } catch (_: Throwable) {
                // Ignore audio stop exceptions
            } finally {
                try {
                    track.stop()
                    track.release()
                } catch (_: Throwable) {
                }
            }
        }
    }

    fun stop() {
        audioJob?.cancel()
        audioJob = null
    }
}
