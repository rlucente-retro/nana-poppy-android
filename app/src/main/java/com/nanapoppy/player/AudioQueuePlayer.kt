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

package com.nanapoppy.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import java.io.File

class AudioQueuePlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val queue = mutableListOf<String>()
    private var currentChildId: String = ""
    private var onComplete: (() -> Unit)? = null

    fun playQueue(childId: String, words: List<String>, onComplete: () -> Unit) {
        this.currentChildId = childId
        this.queue.clear()
        this.queue.addAll(words)
        this.onComplete = onComplete
        
        stopAndRelease()
        playNext()
    }

    private fun playNext() {
        if (queue.isEmpty()) {
            onComplete?.invoke()
            return
        }

        val word = queue.removeAt(0)
        val file = File(context.filesDir, "audio/$currentChildId/$word.mp3")
        
        if (!file.exists()) {
            playNext()
            return
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                
                java.io.FileInputStream(file).use { fis ->
                    setDataSource(fis.fd)
                    prepare()
                }
                
                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                    playNext()
                }
                
                setOnErrorListener { mp, _, _ ->
                    mp.release()
                    mediaPlayer = null
                    playNext()
                    true
                }
                
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mediaPlayer?.release()
            mediaPlayer = null
            playNext()
        }
    }

    private fun stopAndRelease() {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    it.stop()
                }
            } catch (e: Exception) {
                // Ignore
            }
            it.release()
        }
        mediaPlayer = null
    }

    fun release() {
        stopAndRelease()
    }
}
