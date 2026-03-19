/*
 * Copyright 2026 Richard Lucente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nanapoppy.data

import android.content.Context
import android.content.SharedPreferences

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SettingsRepository(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    
    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        "nana_poppy_secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    private val prefs: SharedPreferences = context.getSharedPreferences("nana_poppy_prefs", Context.MODE_PRIVATE)

    var owmApiKey: String?
        get() = securePrefs.getString("owm_api_key", null)
        set(value) = securePrefs.edit().putString("owm_api_key", value).apply()

    var zipUrl: String?
        get() = securePrefs.getString("zip_url", null)
        set(value) = securePrefs.edit().putString("zip_url", value).apply()

    var location1Query: String
        get() = prefs.getString("location1_query", "Waynesboro,PA") ?: "Waynesboro,PA"
        set(value) = prefs.edit().putString("location1_query", value).apply()

    var location2Query: String
        get() = prefs.getString("location2_query", "Ocean City,MD") ?: "Ocean City,MD"
        set(value) = prefs.edit().putString("location2_query", value).apply()

    fun isConfigured(): Boolean {
        return !owmApiKey.isNullOrBlank() && !zipUrl.isNullOrBlank()
    }
}
