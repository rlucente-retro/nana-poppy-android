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

package com.nanapoppy.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object MessageGenerator {

    fun generateDateMsg(now: LocalDateTime): List<String> {
        val msg = mutableListOf("good")
        val hour = now.hour

        when {
            hour < 12 -> msg.add("morning")
            hour < 17 -> msg.add("afternoon")
            hour < 20 -> msg.add("evening")
            else -> msg.add("night")
        }

        msg.addAll(listOf("nana_and_poppy", "today", "is"))
        msg.add(now.format(DateTimeFormatter.ofPattern("MMMM", Locale.US)).lowercase())

        val day = now.dayOfMonth
        msg.addAll(NumberToWords.convertOrdinal(day))
        
        return msg
    }

    fun generateTimeMsg(now: LocalDateTime): List<String> {
        val msg = mutableListOf("the_time", "is")
        var hour = now.hour
        val ampm = if (hour > 11) "pm" else "am"

        hour %= 12
        if (hour == 0) hour = 12

        msg.addAll(NumberToWords.convert(hour))

        val minute = now.minute
        if (minute in 1..9) {
            msg.add("oh")
        }
        if (minute > 0) {
            msg.addAll(NumberToWords.convert(minute))
        }

        msg.add(ampm)
        return msg
    }

    fun generateTempMsg(location: String, temp: Int?): List<String> {
        val formattedLocation = location.lowercase().replace(" ", "_")
        val msg = mutableListOf("the_current_temperature_for", formattedLocation, "is")

        if (temp == null) {
            msg.addAll(listOf("minus", "minus"))
        } else {
            var t = temp
            if (t < 0) {
                msg.add("minus")
                t = -t
            }
            msg.addAll(NumberToWords.convert(t))
        }

        msg.add("degrees")
        return msg
    }
}
