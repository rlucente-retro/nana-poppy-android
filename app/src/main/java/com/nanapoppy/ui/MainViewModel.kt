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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nanapoppy.data.SettingsRepository
import com.nanapoppy.data.WeatherService
import com.nanapoppy.player.AudioQueuePlayer
import com.nanapoppy.utils.ChildSelector
import com.nanapoppy.utils.MessageGenerator
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.time.LocalDateTime

class MainViewModel @JvmOverloads constructor(
    application: Application,
    private val weatherService: WeatherService = createWeatherService(),
    private val settings: SettingsRepository = SettingsRepository(application),
    private val player: AudioQueuePlayer = AudioQueuePlayer(application)
) : AndroidViewModel(application) {
    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    companion object {
        private fun createWeatherService(): WeatherService {
            return Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherService::class.java)
        }
    }

    fun play() {
        if (!settings.isConfigured()) return
        
        val audioDir = File(getApplication<Application>().filesDir, "audio")
        val availableChildren = audioDir.listFiles { file -> file.isDirectory }?.map { it.name } ?: emptyList()
        
        if (availableChildren.isEmpty()) return

        _isPlaying.value = true
        viewModelScope.launch {
            val now = LocalDateTime.now()
            val temp1 = fetchWeather(settings.location1Query)
            val temp2 = fetchWeather(settings.location2Query)

            // Randomly select 4 children for the 4 messages
            val selectedChildren = ChildSelector.select(availableChildren, 4)

            // Assign each selected child to a message
            val messages = listOf(
                selectedChildren[0] to MessageGenerator.generateDateMsg(now),
                selectedChildren[1] to MessageGenerator.generateTimeMsg(now),
                selectedChildren[2] to MessageGenerator.generateTempMsg("location1", temp1),
                selectedChildren[3] to MessageGenerator.generateTempMsg("location2", temp2)
            )

            playMessagesSequentially(messages, 0)
        }
    }

    private suspend fun fetchWeather(location: String): Int? {
        return try {
            val response = weatherService.getCurrentWeather(location, settings.owmApiKey!!)
            response.main.temp.toInt()
        } catch (e: Exception) {
            null
        }
    }

    private fun playMessagesSequentially(messages: List<Pair<String, List<String>>>, index: Int) {
        if (index >= messages.size) {
            _isPlaying.postValue(false)
            return
        }

        val (childId, words) = messages[index]
        player.playQueue(childId, words) {
            playMessagesSequentially(messages, index + 1)
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
