package com.woker.model

data class Alarm(
    val id: Long = System.currentTimeMillis(),
    val time: String,
    val enabled: Boolean = true,
    val soundUri: String? = null
)
