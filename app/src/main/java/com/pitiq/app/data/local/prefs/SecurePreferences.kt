package com.pitiq.app.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "pitiq_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    var locationId: String?
        get() = prefs.getString(KEY_LOCATION_ID, null)
        set(value) = prefs.edit().putString(KEY_LOCATION_ID, value).apply()

    var operatorPin: String?
        get() = prefs.getString(KEY_OPERATOR_PIN, null)
        set(value) = prefs.edit().putString(KEY_OPERATOR_PIN, value).apply()

    var bluetoothSharedSecret: String?
        get() = prefs.getString(KEY_BT_SHARED_SECRET, null)
        set(value) = prefs.edit().putString(KEY_BT_SHARED_SECRET, value).apply()

    val isConfigured: Boolean
        get() = locationId != null

    companion object {
        private const val KEY_LOCATION_ID = "location_id"
        private const val KEY_OPERATOR_PIN = "operator_pin"
        private const val KEY_BT_SHARED_SECRET = "bt_shared_secret"
    }
}
