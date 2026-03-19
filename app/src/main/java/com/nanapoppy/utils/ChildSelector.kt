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

object ChildSelector {
    /**
     * Selects [count] child IDs from [available] children.
     * 
     * If there are at least [count] children, it selects [count] unique children randomly.
     * If there are fewer than [count] children, it ensures each child is selected at least once
     * and fills the remaining slots until [count] are selected.
     */
    fun select(available: List<String>, count: Int): List<String> {
        if (available.isEmpty()) return emptyList()
        
        return if (available.size >= count) {
            available.shuffled().take(count)
        } else {
            val list = mutableListOf<String>()
            list.addAll(available.shuffled())
            while (list.size < count) {
                list.add(available.random())
            }
            list.shuffled()
        }
    }
}
