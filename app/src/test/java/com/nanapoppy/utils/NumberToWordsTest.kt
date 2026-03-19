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

import org.junit.Assert.assertEquals
import org.junit.Test

class NumberToWordsTest {

    @Test
    fun testConvertLowNumbers() {
        assertEquals(listOf("zero"), NumberToWords.convert(0))
        assertEquals(listOf("five"), NumberToWords.convert(5))
        assertEquals(listOf("ten"), NumberToWords.convert(10))
        assertEquals(listOf("nineteen"), NumberToWords.convert(19))
    }

    @Test
    fun testConvertTens() {
        assertEquals(listOf("twenty"), NumberToWords.convert(20))
        assertEquals(listOf("thirty"), NumberToWords.convert(30))
        assertEquals(listOf("ninety"), NumberToWords.convert(90))
    }

    @Test
    fun testConvertCompoundNumbers() {
        assertEquals(listOf("twenty", "one"), NumberToWords.convert(21))
        assertEquals(listOf("fifty", "five"), NumberToWords.convert(55))
        assertEquals(listOf("ninety", "nine"), NumberToWords.convert(99))
    }

    @Test
    fun testConvertOrdinal() {
        assertEquals(listOf("first"), NumberToWords.convertOrdinal(1))
        assertEquals(listOf("second"), NumberToWords.convertOrdinal(2))
        assertEquals(listOf("third"), NumberToWords.convertOrdinal(3))
        assertEquals(listOf("fourth"), NumberToWords.convertOrdinal(4))
        assertEquals(listOf("twenty", "first"), NumberToWords.convertOrdinal(21))
        assertEquals(listOf("thirtieth"), NumberToWords.convertOrdinal(30))
        assertEquals(listOf("thirty", "first"), NumberToWords.convertOrdinal(31))
    }
}
