package com.example.challengeme.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import com.example.challengeme.data.UserEntity
class DailyChallengeViewModel(private val context: Context) : ViewModel() {

    private val sessionPrefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val userId = sessionPrefs.getInt("user_id", -1)

    //  user-specific SharedPrefs for daily challenges
    private val prefs = context.getSharedPreferences("daily_challenge_prefs_user_$userId", Context.MODE_PRIVATE)

    private val challengeList = listOf(
        "Walk 10,000 steps today", "Drink 8 glasses of water", "Write down 3 things you're grateful for",
        "Do 15 pushups", "Read 10 pages of a book", "Meditate for 5 minutes",
        "Learn one new word", "Compliment someone genuinely", "Try a new healthy recipe"
    )

    var currentChallenge = mutableStateOf("")
    var isChallengeCompleted = mutableStateOf(false)

    init {
        loadChallenge()
    }

    private fun loadChallenge() {
        val lastTime = prefs.getLong("last_generated_time", 0L)
        val now = System.currentTimeMillis()
        val elapsed = now - lastTime

        if (elapsed > TimeUnit.HOURS.toMillis(24) || prefs.getString("challenge", null) == null) {
            val newChallenge = challengeList.random()
            saveChallenge(newChallenge, false, now)
            currentChallenge.value = newChallenge
            isChallengeCompleted.value = false
        } else {
            currentChallenge.value = prefs.getString("challenge", "") ?: ""
            isChallengeCompleted.value = prefs.getBoolean("completed", false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun completeChallenge() {
        if (userId == -1) return

        isChallengeCompleted.value = true
        prefs.edit().putBoolean("completed", true).apply()

        // ✅ Use user-specific challenge tracking
        val sharedPrefs = context.getSharedPreferences("challenge_prefs_user_$userId", Context.MODE_PRIVATE)
        val today = java.time.LocalDate.now().toString()
        val lastDate = sharedPrefs.getString("last_completed_date", null)

        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            val db = com.example.challengeme.data.ChallengeMeDatabase.getDatabase(context)
            val userDao = db.userDao()
            val user = userDao.findById(userId)

            if (user != null) {
                val updatedStreak = when {
                    lastDate == null -> 1
                    java.time.LocalDate.parse(lastDate).plusDays(1) == java.time.LocalDate.now() -> user.streak_days + 1
                    java.time.LocalDate.parse(lastDate) == java.time.LocalDate.now() -> user.streak_days
                    else -> 1
                }

                val updatedUser = user.copy(
                    streak_days = updatedStreak,
                    lastCompletedDate = today
                )
                userDao.update(updatedUser)
                sharedPrefs.edit().putString("last_completed_date", today).apply()
            }
        }
    }

    private fun saveChallenge(challenge: String, completed: Boolean, time: Long) {
        prefs.edit()
            .putString("challenge", challenge)
            .putBoolean("completed", completed)
            .putLong("last_generated_time", time)
            .apply()
    }
}