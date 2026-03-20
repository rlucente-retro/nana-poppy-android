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
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import com.nanapoppy.BuildConfig

@RunWith(AndroidJUnit4::class)
class SettingsRepositoryIntegrationTest {

    @Test
    fun `owmApiKey returns BuildConfig value when preference is null`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = SettingsRepository(context)

        // Clear existing preference to ensure fallback to BuildConfig
        val securePrefs = context.getSharedPreferences("nana_poppy_secure_prefs", Context.MODE_PRIVATE)
        securePrefs.edit().remove("owm_api_key").apply()

        // Expect the value from BuildConfig (which comes from secrets.properties)
        assertEquals(BuildConfig.OWM_API_KEY, repository.owmApiKey)
    }
}
