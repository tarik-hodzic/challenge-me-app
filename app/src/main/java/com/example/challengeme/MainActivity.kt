package com.example.challengeme

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.challengeme.data.ChallengeMeDatabase
import com.example.challengeme.data.UserEntity
import com.example.challengeme.ui.theme.ChallengeMeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
        NotificationScheduler.scheduleDailyChallengeNotifications(this)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "challenge_channel",
                "Challenge Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // 👤 Dummy user init
        val db = ChallengeMeDatabase.getDatabase(applicationContext)
        val userDao = db.userDao()
        CoroutineScope(Dispatchers.IO).launch {
            val dummy = userDao.findByEmail("selma@example.com")
            if (dummy == null) {
                userDao.insert(
                    UserEntity(
                        email = "selma@example.com",
                        password = "Password",
                        firstName = "Test",
                        lastName = "User"
                    )
                )
            }
        }

        setContent {
            ChallengeMeTheme {
                androidx.compose.material3.Surface(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(navController)
                }
            }
        }
    }
}
