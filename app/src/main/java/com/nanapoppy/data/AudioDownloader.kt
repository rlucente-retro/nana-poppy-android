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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

class AudioDownloader(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .followRedirects(true)
        .build()

    private fun convertGoogleDriveUrl(url: String): String {
        if (!url.contains("drive.google.com")) return url
        
        val fileId = when {
            url.contains("/file/d/") -> {
                url.substringAfter("/file/d/").substringBefore("/")
            }
            url.contains("id=") -> {
                url.substringAfter("id=").substringBefore("&")
            }
            else -> return url
        }
        
        return "https://drive.google.com/uc?export=download&id=$fileId"
    }

    suspend fun downloadAndUnzip(url: String): Boolean = withContext(Dispatchers.IO) {
        val convertedUrl = convertGoogleDriveUrl(url)
        val request = Request.Builder().url(convertedUrl).build()
        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext false

            val body = response.body ?: return@withContext false
            val contentType = body.contentType()?.toString()
            
            // If we got an HTML page instead of a zip, it might be a Google Drive "large file" warning
            // or just the wrong page.
            if (contentType?.contains("text/html") == true) {
                val html = body.string()
                if (html.contains("confirm=")) {
                    val confirmToken = html.substringAfter("confirm=").substringBefore("&")
                    val confirmUrl = "$convertedUrl&confirm=$confirmToken"
                    val confirmRequest = Request.Builder().url(confirmUrl).build()
                    val confirmResponse = client.newCall(confirmRequest).execute()
                    if (!confirmResponse.isSuccessful) return@withContext false
                    return@withContext unzipStream(confirmResponse.body?.byteStream() ?: return@withContext false)
                }
                return@withContext false
            }

            unzipStream(body.byteStream())
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    data class ValidationResult(
        val children: List<ChildStatus>
    )

    data class ChildStatus(
        val name: String,
        val missingPhrases: List<String>
    )

    fun validate(): ValidationResult {
        val phraseList = try {
            context.assets.open("phrase-list.txt").bufferedReader().useLines { lines ->
                lines.map { it.trim() }.filter { it.isNotEmpty() }.toList()
            }
        } catch (e: Exception) {
            emptyList()
        }

        val audioDir = File(context.filesDir, "audio")
        val children = audioDir.listFiles { file -> file.isDirectory } ?: emptyArray()

        val results = children.map { childDir ->
            val existingPhrases = childDir.listFiles { file -> file.extension == "mp3" }
                ?.map { it.nameWithoutExtension }
                ?.toSet() ?: emptySet()

            val missing = phraseList.filter { !existingPhrases.contains(it) }
            ChildStatus(childDir.name, missing)
        }

        return ValidationResult(results)
    }

    private fun unzipStream(inputStream: java.io.InputStream): Boolean {
        try {
            val audioDir = File(context.filesDir, "audio")
            if (!audioDir.exists()) audioDir.mkdirs()

            ZipInputStream(inputStream).use { zipInputStream ->
                val audioDirCanonicalPath = audioDir.canonicalPath + File.separator
                var entry = zipInputStream.nextEntry
                while (entry != null) {
                    val file = File(audioDir, entry.name)
                    
                    if (!file.canonicalPath.startsWith(audioDirCanonicalPath)) {
                        throw SecurityException("Zip Path Traversal Vulnerability")
                    }

                    if (entry.isDirectory) {
                        file.mkdirs()
                    } else {
                        file.parentFile?.mkdirs()
                        FileOutputStream(file).use { outputStream ->
                            zipInputStream.copyTo(outputStream)
                        }
                    }
                    zipInputStream.closeEntry()
                    entry = zipInputStream.nextEntry
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
