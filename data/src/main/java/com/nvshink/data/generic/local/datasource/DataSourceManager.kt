package com.nvshink.data.generic.local.datasource

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DataSourceManager {
    private val _isLocal = MutableStateFlow(false)
    val isLocal: StateFlow<Boolean> = _isLocal.asStateFlow()

    fun setLocal(isLocal: Boolean) {
        _isLocal.value = isLocal
    }
}