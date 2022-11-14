package com.fox.mathtrainer

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fox.mathtrainer.databinding.ActivityScoreBinding


class ScoreActivity : AppCompatActivity() {
    private var _binding: ActivityScoreBinding? = null
    private val binding get() = _binding?: throw RuntimeException("ScoreActivity = null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        if (intent != null && intent.hasExtra(MainActivity.RESULT)) {
            val result = intent.getIntExtra(MainActivity.RESULT, 0)
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            val max = preferences.getInt(MainActivity.KEY_MAX, 0)
            val score = String.format("Ваш результат: %s\nМаксимальный результат: %s", result, max)
            binding.tvAct2Result.text = score
        }

        binding.btnAct2StartGame.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}