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

import android.content.Context
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import io.mockk.every
import io.mockk.mockk

class AudioDownloaderTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var downloader: AudioDownloader
    private val context = mockk<Context>()
    private lateinit var tempDir: File

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        downloader = AudioDownloader(context)
        tempDir = File("build/tmp/test-download")
        tempDir.deleteRecursively()
        tempDir.mkdirs()
        every { context.filesDir } returns tempDir
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        tempDir.deleteRecursively()
    }

    @Test
    fun `downloadAndUnzip successfully downloads and extracts files`() = runBlocking {
        // Create a mock zip in memory
        val baos = ByteArrayOutputStream()
        ZipOutputStream(baos).use { zos ->
            zos.putNextEntry(ZipEntry("child1/test.txt"))
            zos.write("hello".toByteArray())
            zos.closeEntry()
            
            zos.putNextEntry(ZipEntry("child2/audio.mp3"))
            zos.write("fake audio data".toByteArray())
            zos.closeEntry()
        }
        val zipData = baos.toByteArray()

        mockWebServer.enqueue(MockResponse().setBody(Buffer().write(zipData)))

        val success = downloader.downloadAndUnzip(mockWebServer.url("/audio.zip").toString())

        assertTrue(success)
        assertTrue(File(tempDir, "audio/child1/test.txt").exists())
        assertTrue(File(tempDir, "audio/child2/audio.mp3").exists())
    }

    @Test
    fun `unzipStream deletes existing audio directory contents before extracting`() = runBlocking {
        // Pre-create an "audio" directory with some files
        val audioDir = File(tempDir, "audio")
        audioDir.mkdirs()
        File(audioDir, "old-child/old-file.txt").apply {
            parentFile?.mkdirs()
            writeText("old content")
        }
        
        // Create a mock zip in memory
        val baos = ByteArrayOutputStream()
        ZipOutputStream(baos).use { zos ->
            zos.putNextEntry(ZipEntry("new-child/new-file.txt"))
            zos.write("new content".toByteArray())
            zos.closeEntry()
        }
        val zipData = baos.toByteArray()

        mockWebServer.enqueue(MockResponse().setBody(Buffer().write(zipData)))

        val success = downloader.downloadAndUnzip(mockWebServer.url("/audio.zip").toString())

        assertTrue(success)
        assertTrue(File(tempDir, "audio/new-child/new-file.txt").exists())
        // Verify old file is gone
        assertTrue(!File(tempDir, "audio/old-child/old-file.txt").exists())
        assertTrue(!File(tempDir, "audio/old-child").exists())
    }
}
