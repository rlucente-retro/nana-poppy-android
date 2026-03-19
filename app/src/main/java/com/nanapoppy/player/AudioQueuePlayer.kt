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
import android.media.MediaPlayer
import android.net.Uri
import java.io.File

class AudioQueuePlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var nextMediaPlayer: MediaPlayer? = null
    private val queue = mutableListOf<String>()
    private var currentChildId: String = ""
    private var onComplete: (() -> Unit)? = null

    fun playQueue(childId: String, words: List<String>, onComplete: () -> Unit) {
        this.currentChildId = childId
        this.queue.clear()
        this.queue.addAll(words)
        this.onComplete = onComplete
        
        release()
        playNext()
    }

    private fun playNext() {
        if (queue.isEmpty()) {
            onComplete?.invoke()
            return
        }

        val word = queue.removeAt(0)
        val file = File(context.filesDir, "audio/$currentChildId/$word.wav")
        
        if (!file.exists()) {
            playNext()
            return
        }

        mediaPlayer = MediaPlayer.create(context, Uri.fromFile(file))
        if (mediaPlayer == null) {
            playNext()
            return
        }

        setupNextPlayer()
        
        mediaPlayer?.setOnCompletionListener {
            it.release()
            mediaPlayer = nextMediaPlayer
            nextMediaPlayer = null
            if (mediaPlayer != null) {
                setupNextPlayer()
                mediaPlayer?.start()
            } else {
                onComplete?.invoke()
            }
        }
        mediaPlayer?.start()
    }

    private fun setupNextPlayer() {
        if (queue.isEmpty()) return

        val word = queue.removeAt(0)
        val file = File(context.filesDir, "audio/$currentChildId/$word.wav")
        
        if (!file.exists()) {
            setupNextPlayer()
            return
        }

        nextMediaPlayer = MediaPlayer.create(context, Uri.fromFile(file))
        if (nextMediaPlayer == null) {
            setupNextPlayer()
            return
        }

        mediaPlayer?.setNextMediaPlayer(nextMediaPlayer)
        nextMediaPlayer?.setOnCompletionListener { mp ->
            mp.release()
            mediaPlayer = nextMediaPlayer
            nextMediaPlayer = null
            if (mediaPlayer != null) {
                setupNextPlayer()
            } else {
                onComplete?.invoke()
            }
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        nextMediaPlayer?.release()
        nextMediaPlayer = null
    }
}
