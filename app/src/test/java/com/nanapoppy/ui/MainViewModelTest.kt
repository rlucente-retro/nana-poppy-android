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

package com.nanapoppy.ui

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nanapoppy.data.MainData
import com.nanapoppy.data.SettingsRepository
import com.nanapoppy.data.WeatherResponse
import com.nanapoppy.data.WeatherService
import com.nanapoppy.player.AudioQueuePlayer
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel
    private val application = mockk<Application>(relaxed = true)
    private val weatherService = mockk<WeatherService>()
    private val settings = mockk<SettingsRepository>(relaxed = true)
    private val player = mockk<AudioQueuePlayer>(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mocking the behavior of application for file operations
        val mockFilesDir = File("build/tmp/test-audio")
        mockFilesDir.mkdirs()
        File(mockFilesDir, "audio/child1").mkdirs() // Mock one child directory
        every { application.filesDir } returns mockFilesDir

        viewModel = MainViewModel(application, weatherService, settings, player)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `play calls weather service and plays messages when configured`() = runTest {
        // Arrange
        every { settings.isConfigured() } returns true
        every { settings.location1Query } returns "Waynesboro,PA,US"
        every { settings.location2Query } returns "Ocean City,MD,US"
        every { settings.owmApiKey } returns "fake_key"

        val mockWeatherResponse1 = WeatherResponse(MainData(72.5f), "Waynesboro")
        val mockWeatherResponse2 = WeatherResponse(MainData(65.0f), "Ocean City")

        coEvery { weatherService.getCurrentWeather("Waynesboro,PA,US", "fake_key") } returns mockWeatherResponse1
        coEvery { weatherService.getCurrentWeather("Ocean City,MD,US", "fake_key") } returns mockWeatherResponse2

        // Act
        viewModel.play()

        // Assert
        // Verify that weather service was called for both locations
        coVerify { weatherService.getCurrentWeather("Waynesboro,PA,US", "fake_key") }
        coVerify { weatherService.getCurrentWeather("Ocean City,MD,US", "fake_key") }
        
        // Verify that the player was called to play the playlist
        verify { player.playPlaylist(any(), any()) }
    }
}
