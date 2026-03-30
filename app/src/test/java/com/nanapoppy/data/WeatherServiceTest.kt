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

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: WeatherService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getCurrentWeather returns successful response`() {
        runBlocking {
            val mockResponse = MockResponse()
                .setResponseCode(200)
                .setBody("""
                    {
                        "main": {
                            "temp": 72.5
                        },
                        "name": "Test City,US"
                    }
                """.trimIndent())
            mockWebServer.enqueue(mockResponse)

            val response = service.getCurrentWeather("Test City,US", "fake_key")

            assertEquals(72.5f, response.main.temp)
            assertEquals("Test City,US", response.name)
        }
    }

    @Test(expected = retrofit2.HttpException::class)
    fun `getCurrentWeather throws exception on 401 error`() {
        runBlocking {
            val mockResponse = MockResponse()
                .setResponseCode(401)
                .setBody("""{"cod":401, "message": "Invalid API key"}""")
            mockWebServer.enqueue(mockResponse)

            service.getCurrentWeather("Test City,US", "invalid_key")
        }
    }
}
