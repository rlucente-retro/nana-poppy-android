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

package com.nanapoppy.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nanapoppy.data.AudioDownloader
import com.nanapoppy.data.SettingsRepository
import com.nanapoppy.databinding.ActivitySettingsBinding
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settings: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settings = SettingsRepository(this)

        // Load existing settings
        binding.etApiKey.setText(settings.owmApiKey)
        binding.etZipUrl.setText(settings.zipUrl)
        binding.etLocation1Query.setText(settings.location1Query)
        binding.etLocation2Query.setText(settings.location2Query)

        binding.btnSave.setOnClickListener {
            settings.owmApiKey = binding.etApiKey.text.toString()
            settings.zipUrl = binding.etZipUrl.text.toString()
            settings.location1Query = binding.etLocation1Query.text.toString()
            settings.location2Query = binding.etLocation2Query.text.toString()
            Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show()
        }

        binding.btnSync.setOnClickListener {
            syncAudio()
        }
    }

    private fun syncAudio() {
        val zipUrl = settings.zipUrl

        if (zipUrl.isNullOrBlank()) {
            Toast.makeText(this, "ZIP URL is required", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSync.isEnabled = false
        val downloader = AudioDownloader(this)

        lifecycleScope.launch {
            val success = downloader.downloadAndUnzip(zipUrl)

            if (success) {
                val validation = downloader.validate()
                showValidationDialog(validation)
            } else {
                Toast.makeText(this@SettingsActivity, "Sync Failed", Toast.LENGTH_SHORT).show()
            }
            binding.btnSync.isEnabled = true
        }
    }

    private fun showValidationDialog(result: AudioDownloader.ValidationResult) {
        val message = StringBuilder()
        if (result.children.isEmpty()) {
            message.append("No children found in the zip file.")
        } else {
            result.children.forEach { child ->
                message.append("Child: ${child.name}\n")
                if (child.missingPhrases.isEmpty()) {
                    message.append("✓ All phrases present\n")
                } else {
                    message.append("✗ Missing: ${child.missingPhrases.joinToString(", ")}\n")
                }
                message.append("\n")
            }
        }

        val textView = android.widget.TextView(this).apply {
            text = message.toString()
            setPadding(48, 24, 48, 24)
            setTextIsSelectable(true)
        }

        val scrollView = android.widget.ScrollView(this).apply {
            addView(textView)
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Sync Complete")
            .setView(scrollView)
            .setPositiveButton("OK", null)
            .show()
    }
}
