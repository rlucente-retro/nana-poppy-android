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

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nanapoppy.R
import com.nanapoppy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPlay.setOnClickListener {
            viewModel.play()
        }

        viewModel.isPlaying.observe(this) { isPlaying ->
            binding.btnPlay.isEnabled = !isPlaying
            binding.progressBar.visibility = if (isPlaying) View.VISIBLE else View.GONE
        }

        viewModel.currentSpeakerPhoto.observe(this) { photoFile ->
            if (photoFile != null && photoFile.exists()) {
                binding.ivSpeakerPhoto.setImageURI(android.net.Uri.fromFile(photoFile))
                binding.ivSpeakerPhoto.visibility = View.VISIBLE
            } else {
                binding.ivSpeakerPhoto.setImageDrawable(null)
                binding.ivSpeakerPhoto.visibility = View.GONE
            }
        }

        viewModel.status.observe(this) { status ->
            status?.let {
                android.widget.Toast.makeText(this, it, android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
