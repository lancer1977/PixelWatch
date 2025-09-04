package com.polyhydragames.pixelwatch.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SimpleLiveHrSession {
    private val _bpm = MutableStateFlow<Int?>(null)
    val bpm: StateFlow<Int?> = _bpm

    fun updateBpm(value: Int) {
        _bpm.value = value
    }
}