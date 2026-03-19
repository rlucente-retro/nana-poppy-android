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

package com.nanapoppy

import com.nanapoppy.utils.MessageGenerator
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class MessageGeneratorTest {

    @Test
    fun testGenerateDateMsgMorning() {
        val now = LocalDateTime.of(2024, 3, 16, 10, 15)
        val msg = MessageGenerator.generateDateMsg(now)
        val expected = listOf("good", "morning", "nana_and_poppy", "today", "is", "march", "sixteenth")
        assertEquals(expected, msg)
    }

    @Test
    fun testGenerateDateMsgAfternoon() {
        val now = LocalDateTime.of(2024, 3, 16, 14, 15) // 2 PM
        val msg = MessageGenerator.generateDateMsg(now)
        val expected = listOf("good", "afternoon", "nana_and_poppy", "today", "is", "march", "sixteenth")
        assertEquals(expected, msg)
    }

    @Test
    fun testGenerateDateMsgEvening() {
        val now = LocalDateTime.of(2024, 3, 16, 18, 15) // 6 PM
        val msg = MessageGenerator.generateDateMsg(now)
        val expected = listOf("good", "evening", "nana_and_poppy", "today", "is", "march", "sixteenth")
        assertEquals(expected, msg)
    }

    @Test
    fun testGenerateDateMsgNight() {
        val now = LocalDateTime.of(2024, 3, 16, 22, 15) // 10 PM
        val msg = MessageGenerator.generateDateMsg(now)
        val expected = listOf("good", "night", "nana_and_poppy", "today", "is", "march", "sixteenth")
        assertEquals(expected, msg)
    }

    @Test
    fun testGenerateTimeMsgMorning() {
        val now = LocalDateTime.of(2024, 3, 16, 10, 15)
        val msg = MessageGenerator.generateTimeMsg(now)
        val expected = listOf("the_time", "is", "ten", "fifteen", "am")
        assertEquals(expected, msg)
    }

    @Test
    fun testGenerateTimeMsgPM() {
        val now = LocalDateTime.of(2024, 3, 16, 14, 15) // 2 PM
        val msg = MessageGenerator.generateTimeMsg(now)
        val expected = listOf("the_time", "is", "two", "fifteen", "pm")
        assertEquals(expected, msg)
    }

    @Test
    fun testGenerateTimeMsgNoon() {
        val now = LocalDateTime.of(2024, 3, 16, 12, 0) // 12 PM
        val msg = MessageGenerator.generateTimeMsg(now)
        val expected = listOf("the_time", "is", "twelve", "pm")
        assertEquals(expected, msg)
    }

    @Test
    fun testGenerateTimeMsgMidnight() {
        val now = LocalDateTime.of(2024, 3, 16, 0, 0) // 12 AM
        val msg = MessageGenerator.generateTimeMsg(now)
        val expected = listOf("the_time", "is", "twelve", "am")
        assertEquals(expected, msg)
    }

    @Test
    fun testGenerateTimeMsgWithOh() {
        // 10:05 AM
        val now = LocalDateTime.of(2024, 3, 16, 10, 5)
        val msg = MessageGenerator.generateTimeMsg(now)
        val expected = listOf("the_time", "is", "ten", "oh", "five", "am")
        assertEquals(expected, msg)
    }

    @Test
    fun testGenerateTempMsg() {
        val msg = MessageGenerator.generateTempMsg("location1", 72)
        val expected = listOf("the_current_temperature_for", "location1", "is", "seventy", "two", "degrees")
        assertEquals(expected, msg)
    }

    @Test
    fun testGenerateTempMsg2() {
        val msg = MessageGenerator.generateTempMsg("location2", 55)
        val expected = listOf("the_current_temperature_for", "location2", "is", "fifty", "five", "degrees")
        assertEquals(expected, msg)
    }

    @Test
    fun testGenerateTempMsgNegative() {
        val msg = MessageGenerator.generateTempMsg("location1", -5)
        val expected = listOf("the_current_temperature_for", "location1", "is", "minus", "five", "degrees")
        assertEquals(expected, msg)
    }

    @Test
    fun testGenerateTempMsgNull() {
        val msg = MessageGenerator.generateTempMsg("location1", null)
        val expected = listOf("the_current_temperature_for", "location1", "is", "minus", "minus", "degrees")
        assertEquals(expected, msg)
    }
}
