package com.jfdedit3.pix.auth

data class UserSession(
    val displayName: String,
    val sessionValue: String,
    val refreshValue: String,
    val isConnected: Boolean
)
