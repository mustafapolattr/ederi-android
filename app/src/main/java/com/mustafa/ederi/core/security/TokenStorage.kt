package com.mustafa.ederi.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores only the auth token (never the password), per spec §12. Backed by
 * Android's Keystore-derived master key so the value is encrypted at rest
 * and survives process death/app restarts without ever being held in plain
 * SharedPreferences.
 */
interface TokenStorage {
    fun getAccessToken(): String?
    fun saveAccessToken(token: String)
    fun clear()
}

@Singleton
class EncryptedTokenStorage @Inject constructor(
    context: Context
) : TokenStorage {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    override fun saveAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    override fun clear() {
        prefs.edit().remove(KEY_ACCESS_TOKEN).apply()
    }

    private companion object {
        const val PREFS_FILE_NAME = "ederi_secure_prefs"
        const val KEY_ACCESS_TOKEN = "access_token"
    }
}
