package net.gabor7d2.simpleinventory.persistence

import android.content.Context
import android.content.Context.MODE_PRIVATE

class Preferences(private val context: Context) {

    companion object {
        private const val CREDENTIALS_SAVED = "CredentialsSaved"
        private const val EMAIL = "Email"
        private const val PASSWORD = "Password"
        private const val SIMPLE_INVENTORY_CREDENTIALS = "SimpleInventoryCredentials"
    }

    private val prefs = context.getSharedPreferences(SIMPLE_INVENTORY_CREDENTIALS, MODE_PRIVATE)

    fun areLoginCredentialsSaved(): Boolean {
        return prefs.getBoolean(CREDENTIALS_SAVED, false)
    }

    fun storeLoginCredentials(email: String, password: String) {
        val editor = prefs.edit()
        editor.putBoolean(CREDENTIALS_SAVED, true)
        editor.putString(EMAIL, email)
        editor.putString(PASSWORD, password)
        editor.apply()
    }

    fun clearLoginCredentials() {
        val editor = prefs.edit()
        editor.putBoolean(CREDENTIALS_SAVED, false)
        editor.remove(EMAIL)
        editor.remove(PASSWORD)
        editor.apply()
    }

    fun getEmail(): String {
        return prefs.getString(EMAIL, "") ?: ""
    }

    fun getPassword(): String {
        return prefs.getString(PASSWORD, "") ?: ""
    }
}