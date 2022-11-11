package com.fox.mathtrainer

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class MainViewModel : ViewModel() {

    private var timer: CountDownTimer? = null
//    private var question = ""
    private var rightAnswer = 0
    private var rightAnswerPosition = 0
    private var isPositive = false
    private var countOfQuestions = 0
    private var countOfAnswers = 0
    private var gameOver = false
    private var numbers = mutableListOf<Int>()


    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<String>()
    val question: LiveData<String>
        get() = _question

    init {
        startTimer()
    }

    private fun generateQuestion() {
        val a = Random.nextInt(MIN_NUMBER, MAX_NUMBER + 1)
        val b = Random.nextInt(MIN_NUMBER, MAX_NUMBER + 1)
        val mark = Random.nextInt(0, 2)
        isPositive = mark == 1
        if (isPositive) {
            rightAnswer = a + b
            _question.value = String.format("%s + %s", a, b)
        } else {
            rightAnswer = a - b
            _question.value = String.format("%s - %s", a, b)
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
                _formattedTime.value = formatTime(millisUntilFinished)
//
            }

            override fun onFinish() {
                myLog("Finish")
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

    private fun myLog(message: Any?) {
        Log.d(TAG, "Main View Model: $message")
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        private const val TAG = "myApp"
        private const val MAX_NUMBER = 30
        private const val MIN_NUMBER = 5
        private const val MILLIS_IN_SECOND = 1000L
        private const val MILLIS_IN_FUTURE = 60000L
        private const val SECONDS_IN_MINUTES = 60
        private const val INTERVAL = 1000L
    }
}