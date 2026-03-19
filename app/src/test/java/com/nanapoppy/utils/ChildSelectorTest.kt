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
import org.junit.Assert.assertTrue
import org.junit.Test

class ChildSelectorTest {

    @Test
    fun testSelectMoreThanAvailable() {
        val available = listOf("A", "B")
        val selected = ChildSelector.select(available, 4)
        
        assertEquals(4, selected.size)
        assertTrue(selected.contains("A"))
        assertTrue(selected.contains("B"))
    }

    @Test
    fun testSelectFewerThanAvailable() {
        val available = listOf("A", "B", "C", "D", "E")
        val selected = ChildSelector.select(available, 4)
        
        assertEquals(4, selected.size)
        assertEquals(4, selected.distinct().size)
    }

    @Test
    fun testSelectExactlyAvailable() {
        val available = listOf("A", "B", "C", "D")
        val selected = ChildSelector.select(available, 4)
        
        assertEquals(4, selected.size)
        assertEquals(4, selected.distinct().size)
        assertTrue(selected.containsAll(available))
    }

    @Test
    fun testSelectOneAvailable() {
        val available = listOf("A")
        val selected = ChildSelector.select(available, 4)
        
        assertEquals(4, selected.size)
        assertTrue(selected.all { it == "A" })
    }
}
