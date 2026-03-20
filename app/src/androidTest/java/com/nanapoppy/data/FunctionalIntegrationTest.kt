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
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nanapoppy.BuildConfig
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * This test uses real API keys and URLs from secrets.properties (via BuildConfig)
 * to perform live network operations.
 */
@RunWith(AndroidJUnit4::class)
class FunctionalIntegrationTest {

    private lateinit var context: Context
    private lateinit var weatherService: WeatherService
    private lateinit var audioDownloader: AudioDownloader

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        
        weatherService = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
            
        audioDownloader = AudioDownloader(context)
    }

    @Test
    fun testWeatherApiIntegration() = runBlocking {
        val apiKey = BuildConfig.OWM_API_KEY
        // Skip if API key is the default/example one
        if (apiKey == "your_real_api_key_here" || apiKey.isBlank()) {
            return@runBlocking
        }

        val response = weatherService.getCurrentWeather("New York", apiKey)
        assertNotNull("Weather response should not be null", response)
        assertNotNull("Main data should not be null", response.main)
        assertTrue("Temperature should be a valid number", response.main.temp != 0f)
        assertEquals("New York", response.name)
    }

    @Test
    fun testAudioDownloadIntegration() = runBlocking {
        val zipUrl = BuildConfig.ZIP_URL
        // Skip if ZIP URL is the default/example one or empty
        if (zipUrl == "https://example.com/your_audio_assets.zip" || 
            zipUrl.isBlank()) {
            return@runBlocking
        }

        // Ensure the audio directory is clean or at least exists
        val audioDir = File(context.filesDir, "audio")
        
        val success = audioDownloader.downloadAndUnzip(zipUrl)
        assertTrue("Audio download and unzip should be successful", success)
        
        // Verify that some files were actually created in the audio directory
        assertTrue("Audio directory should exist", audioDir.exists())
        val files = audioDir.listFiles()
        assertTrue("Audio directory should not be empty", files != null && files.isNotEmpty())
    }

    private fun assertEquals(expected: Any?, actual: Any?) {
        org.junit.Assert.assertEquals(expected, actual)
    }
}
