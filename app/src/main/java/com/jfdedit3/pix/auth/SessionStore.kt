package com.jfdedit3.pix.auth

import android.content.Context

class SessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("pix_session", Context.MODE_PRIVATE)

    fun read(): UserSession? {
        val displayName = prefs.getString("display_name", null) ?: return null
        val sessionValue = prefs.getString("session_value", null) ?: return null
        val refreshValue = prefs.getString("refresh_value", "") ?: ""
        val isConnected = prefs.getBoolean("is_connected", false)
        return UserSession(
            displayName = displayName,
            sessionValue = sessionValue,
            refreshValue = refreshValue,
            isConnected = isConnected
        )
    }

    fun save(session: UserSession) {
        prefs.edit()
            .putString("display_name", session.displayName)
            .putString("session_value", session.sessionValue)
            .putString("refresh_value", session.refreshValue)
            .putBoolean("is_connected", session.isConnected)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
