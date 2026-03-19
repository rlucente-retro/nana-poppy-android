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

object NumberToWords {
    private val lowNames = arrayOf(
        "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
        "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"
    )

    private val tensNames = arrayOf(
        "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
    )

    private val ordinalNames = mapOf(
        "one" to "first", "two" to "second", "three" to "third", "four" to "fourth", "five" to "fifth",
        "six" to "sixth", "seven" to "seventh", "eight" to "eighth", "nine" to "ninth", "ten" to "tenth",
        "eleven" to "eleventh", "twelve" to "twelfth", "thirteen" to "thirteenth", "fourteen" to "fourteenth",
        "fifteen" to "fifteenth", "sixteen" to "sixteenth", "seventeen" to "seventeenth", "eighteen" to "eighteenth",
        "nineteen" to "nineteenth", "twenty" to "twentieth", "thirty" to "thirtieth", "forty" to "fortieth",
        "fifty" to "fiftieth", "sixty" to "sixtieth", "seventy" to "seventieth", "eighty" to "eightieth",
        "ninety" to "ninetieth"
    )

    fun convert(number: Int): List<String> {
        if (number < 20) return listOf(lowNames[number])
        if (number < 100) {
            val tens = tensNames[number / 10 - 2]
            val ones = number % 10
            return if (ones == 0) listOf(tens) else listOf(tens, lowNames[ones])
        }
        return listOf(number.toString()) // Fallback for simplicity
    }

    fun convertOrdinal(number: Int): List<String> {
        val words = convert(number)
        val lastWord = words.last()
        val ordinal = ordinalNames[lastWord] ?: (lastWord + "th") // Simple rule for others
        return words.dropLast(1) + ordinal
    }
}
