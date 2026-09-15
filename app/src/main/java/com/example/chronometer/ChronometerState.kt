package com.example.chronometer

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChronometerState(context: Context) {

    private val preferences = context.getSharedPreferences(
        "chronometer_preferences",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_ACCUMULATED = "accumulated"
        private const val KEY_START_TIME = "start_time"
        private const val KEY_RUNNING = "running"
    }

    private val _elapsedMillis = MutableStateFlow(loadElapsedTime())
    val elapsedMillis: StateFlow<Long> = _elapsedMillis.asStateFlow()

    private var accumulatedMillis: Long =
        preferences.getLong(KEY_ACCUMULATED, 0L)

    private var startTimeMillis: Long =
        preferences.getLong(KEY_START_TIME, 0L)

    private var running: Boolean =
        preferences.getBoolean(KEY_RUNNING, false)


    fun getCurrentElapsedTime(): Long {
        return if (running) {
            accumulatedMillis +
                    (System.currentTimeMillis() - startTimeMillis)
        } else {
            accumulatedMillis
        }
    }


    fun start() {
        if (running) return

        startTimeMillis = System.currentTimeMillis()
        running = true

        saveState()

        _elapsedMillis.value = getCurrentElapsedTime()
    }


    fun pause() {
        if (!running) return

        accumulatedMillis =
            accumulatedMillis +
                    (System.currentTimeMillis() - startTimeMillis)

        running = false
        startTimeMillis = 0L

        saveState()

        _elapsedMillis.value = accumulatedMillis
    }


    fun reset() {
        accumulatedMillis = 0L
        startTimeMillis = 0L
        running = false

        saveState()

        _elapsedMillis.value = 0L
    }


    fun update() {
        _elapsedMillis.value = getCurrentElapsedTime()
    }


    fun isRunning(): Boolean {
        return running
    }


    private fun saveState() {
        preferences.edit()
            .putLong(KEY_ACCUMULATED, accumulatedMillis)
            .putLong(KEY_START_TIME, startTimeMillis)
            .putBoolean(KEY_RUNNING, running)
            .apply()
    }


    private fun loadElapsedTime(): Long {
        val savedAccumulated =
            preferences.getLong(KEY_ACCUMULATED, 0L)

        val savedStartTime =
            preferences.getLong(KEY_START_TIME, 0L)

        val savedRunning =
            preferences.getBoolean(KEY_RUNNING, false)

        return if (savedRunning) {
            savedAccumulated +
                    (System.currentTimeMillis() - savedStartTime)
        } else {
            savedAccumulated
        }
    }
}