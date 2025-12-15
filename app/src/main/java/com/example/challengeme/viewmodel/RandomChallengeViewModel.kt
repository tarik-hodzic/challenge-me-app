package com.example.challengeme.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class RandomChallengeViewModel(private val context: Context) : ViewModel() {

    // userId from session
    private val sessionPrefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val userId = sessionPrefs.getInt("user_id", -1)

    // user-specific prefs
    private val prefs: SharedPreferences = context.getSharedPreferences("random_challenge_prefs_user_$userId", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    var challenges = mutableStateListOf<String>()
    var completedChallenges = mutableStateListOf<Boolean>()
    var lastGeneratedTime = mutableStateOf<LocalDateTime?>(null)

    init {
        if (userId == -1) {} // Prevent loading if user not logged in
        loadChallenges()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateChallenges() {
        val now = LocalDateTime.now()
        val lastTime = lastGeneratedTime.value

        if (lastTime == null || Duration.between(lastTime, now).toHours() >= 24 || challenges.isEmpty()) {
            val newChallenges = listOf(
                "Do 20 squats", "Try a 1-minute plank", "Take a 15-minute walk",
                "Stretch for 5 minutes", "Write a short journal entry", "Listen to a new podcast",
                "Try a breathing exercise", "Eat a fruit you haven't had in a while",
                "Say something kind to yourself", "Organize your workspace"
            ).shuffled().take(10)

            challenges.clear()
            challenges.addAll(newChallenges)

            completedChallenges.clear()
            completedChallenges.addAll(List(newChallenges.size) { false })

            lastGeneratedTime.value = now
            saveChallenges()
        }
    }

    fun markChallengeAsCompleted(index: Int) {
        if (index in completedChallenges.indices) {
            completedChallenges[index] = true
            saveChallenges()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadChallenges() {
        val type: Type = object : TypeToken<List<String>>() {}.type
        val savedChallenges = gson.fromJson<List<String>>(prefs.getString("challenges", null), type)
        val savedCompleted = gson.fromJson<List<Boolean>>(prefs.getString("completed", null), object : TypeToken<List<Boolean>>() {}.type)
        val savedTime = prefs.getString("last_time", null)

        if (savedChallenges != null && savedCompleted != null) {
            challenges.addAll(savedChallenges)
            completedChallenges.addAll(savedCompleted)
        }

        if (savedTime != null) {
            lastGeneratedTime.value = LocalDateTime.parse(savedTime, formatter)
        }
    }

    private fun saveChallenges() {
        prefs.edit()
            .putString("challenges", gson.toJson(challenges))
            .putString("completed", gson.toJson(completedChallenges))
            .putString("last_time", lastGeneratedTime.value?.format(formatter))
            .apply()
    }
}