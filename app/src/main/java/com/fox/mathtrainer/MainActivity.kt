package com.fox.mathtrainer

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fox.mathtrainer.databinding.ActivityMainBinding
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private var timer: CountDownTimer? = null

    private var rightAnswer = 0
    private var rightAnswerPosition = 0
    private var isPositive = false
    private var countOfQuestions = 0
    private var countOfRightAnswers = 0
    private var gameOver = false

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("ActivityMainBinding = null")

    private val tvOpinions by lazy {
        mutableListOf<TextView>().apply {
            add(binding.tvOpinion0)
            add(binding.tvOpinion1)
            add(binding.tvOpinion2)
            add(binding.tvOpinion3)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playNext()
        startTimer()
        setClickListenerToOptions()

    }

    private fun playNext() {
        generateQuestion()
        for (i in 0 until tvOpinions.size) {
            if (i == rightAnswerPosition) {
                tvOpinions[i].text = rightAnswer.toString()
            } else {
                tvOpinions[i].text = generateWrongAnswer().toString()
            }
        }
        val score = String.format("%s / %s", countOfRightAnswers, countOfQuestions)
        binding.tvScore.text = score
    }

    private fun generateQuestion() {
        val a = Random.nextInt(MIN_NUMBER, MAX_NUMBER + 1)
        val b = Random.nextInt(MIN_NUMBER, MAX_NUMBER + 1)
        val mark = Random.nextInt(0, 2)
        isPositive = mark == 1
        if (isPositive) {
            rightAnswer = a + b
            binding.tvQuestion.text = String.format("%s + %s", a, b)
        } else {
            rightAnswer = a - b
            binding.tvQuestion.text = String.format("%s - %s", a, b)
        }
        rightAnswerPosition = Random.nextInt(0, 4)
    }

    private fun generateWrongAnswer(): Int {
        var result = 100500
        do {
            result = Random.nextInt(-25, 36)
        } while (result == rightAnswer)
        return result
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            MILLIS_IN_FUTURE,
            INTERVAL
        ) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text = formatTime(millisUntilFinished)
                if (millisUntilFinished < 10000) {
                    binding.tvTimer.setTextColor(
                        resources.getColor(
                            android.R.color.holo_red_light,
                            null
                        )
                    );
                }
            }

            override fun onFinish() {
                gameOver = true
                val preferences = PreferenceManager.getDefaultSharedPreferences(
                    applicationContext
                )
                val max = preferences.getInt(KEY_MAX, 0)
                if (countOfRightAnswers >= max) {
                    preferences.edit().putInt(KEY_MAX, countOfRightAnswers).apply()
                }
                val intent = Intent(this@MainActivity, ScoreActivity::class.java)
                intent.putExtra(RESULT, countOfRightAnswers)
                startActivity(intent)
            }
        }
        timer?.start()
    }

    private fun formatTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / MILLIS_IN_SECOND
        val minutes = seconds / SECONDS_IN_MINUTES
        val leftSeconds = seconds - (minutes * SECONDS_IN_MINUTES)
        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    private fun setClickListenerToOptions() {
        for (tvOpinion in tvOpinions) {
            tvOpinion.setOnClickListener {
                if (!gameOver) {
                    val answer = tvOpinion.text.toString().toInt()
                    if (answer == rightAnswer) {
                        countOfRightAnswers++
                        Toast.makeText(this, "Верно", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Неверно", Toast.LENGTH_SHORT).show()
                    }
                    countOfQuestions++
                    playNext()
                }
            }
        }
    }

    private fun myLog(message: Any?) {
        Log.d(TAG, "Main Activity: $message")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "myApp"

        const val KEY_MAX = "max"
        const val RESULT = "result"

        private const val MAX_NUMBER = 30
        private const val MIN_NUMBER = 5
        private const val MILLIS_IN_SECOND = 1000L
        private const val MILLIS_IN_FUTURE = 60000L
        private const val SECONDS_IN_MINUTES = 60
        private const val INTERVAL = 1000L
    }


}