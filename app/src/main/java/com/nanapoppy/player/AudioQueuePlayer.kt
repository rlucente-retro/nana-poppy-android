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
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import java.io.File

class AudioQueuePlayer(private val context: Context) {
    private var exoPlayer: ExoPlayer? = null
    private var onComplete: (() -> Unit)? = null

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                onComplete?.invoke()
            }
        }
    }

    private fun ensurePlayer(): ExoPlayer {
        return exoPlayer ?: ExoPlayer.Builder(context).build().apply {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
                .setUsage(C.USAGE_MEDIA)
                .build()
            setAudioAttributes(audioAttributes, true)
            addListener(playerListener)
            exoPlayer = this
        }
    }

    fun playPlaylist(segments: List<Pair<String, List<String>>>, onComplete: () -> Unit) {
        this.onComplete = onComplete
        val player = ensurePlayer()
        player.stop()
        player.clearMediaItems()

        val mediaItems = mutableListOf<MediaItem>()
        segments.forEach { (childId, words) ->
            words.forEach { word ->
                val file = File(context.filesDir, "audio/$childId/$word.mp3")
                if (file.exists()) {
                    mediaItems.add(MediaItem.fromUri(Uri.fromFile(file)))
                }
            }
        }

        if (mediaItems.isEmpty()) {
            onComplete()
            return
        }

        player.setMediaItems(mediaItems)
        player.prepare()
        player.play()
    }

    fun playQueue(childId: String, words: List<String>, onComplete: () -> Unit) {
        playPlaylist(listOf(childId to words), onComplete)
    }

    private fun stopAndRelease() {
        exoPlayer?.let {
            it.removeListener(playerListener)
            it.stop()
            it.release()
        }
        exoPlayer = null
    }

    fun release() {
        stopAndRelease()
    }
}
