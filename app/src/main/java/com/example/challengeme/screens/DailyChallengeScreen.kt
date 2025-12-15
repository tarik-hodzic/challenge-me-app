package com.example.challengeme.screens

import com.example.challengeme.data.ChallengeMeDatabase
import android.content.*
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.challengeme.StopwatchService
import com.example.challengeme.viewmodel.DailyChallengeViewModel
import kotlinx.coroutines.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyChallengeScreen() {
    val context = LocalContext.current
    var refreshKey by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    val viewModel = remember(refreshKey) { DailyChallengeViewModel(context) }
    val currentChallenge = viewModel.currentChallenge.value
    val isCompleted = viewModel.isChallengeCompleted.value

    var stopwatchTime by remember { mutableStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val filter = IntentFilter("STOPWATCH_TICK")
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                stopwatchTime = intent?.getIntExtra("seconds", 0) ?: 0
            }
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: IllegalArgumentException) {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F2027), Color(0xFF2C5364))
                )
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🗓️ Daily Challenge",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Your challenge resets every 24 hours.\nComplete it to boost your streak!",
                fontSize = 14.sp,
                color = Color(0xFFB0C4DE),
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2E4D)),
                modifier = Modifier.fillMaxWidth(0.95f),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentChallenge,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isCompleted) {
                        Button(
                            onClick = {
                                viewModel.completeChallenge()

                                val intent = Intent(context, StopwatchService::class.java).apply {
                                    action = "STOP"
                                }
                                context.startService(intent)
                                isTimerRunning = false
                            }
                            ,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A6BA5))
                        ) {
                            Text("✅ Finish Challenge", color = Color.White)
                        }
                    } else {
                        Text(
                            text = "🎉 Completed!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7CFC00)
                        )
                    }
                }
            }

            if (isCompleted && stopwatchTime > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "🏁 Well done! You finished in $stopwatchTime seconds.",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (!isCompleted) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        stopwatchTime = 0
                        isTimerRunning = true
                        val intent = Intent(context, StopwatchService::class.java).apply {
                            action = "START"
                        }
                        ContextCompat.startForegroundService(context, intent)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(bottom = 16.dp)
                ) {
                    Text("⏱️ Start Timer", color = Color.White)
                }
            }
        }

        Button(
            onClick = {
                val sessionPrefs = context.getSharedPreferences("user_session", MODE_PRIVATE)
                val userId = sessionPrefs.getInt("user_id", -1)
                if (userId != -1) {
                    context.getSharedPreferences("daily_challenge_prefs_user_$userId", MODE_PRIVATE)
                        .edit().clear().apply()
                }
                stopwatchTime = 0
                refreshKey++
            },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            modifier = Modifier
                .wrapContentWidth()
                .height(50.dp)
        ) {
            Text(
                text = "🔁 Reset Challenge",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
    }
}
