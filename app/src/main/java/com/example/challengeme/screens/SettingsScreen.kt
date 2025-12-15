package com.example.challengeme.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.challengeme.NotificationScheduler

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    var selectedInterval by remember { mutableStateOf("2h") }

    val intervals = listOf("5s", "1h", "2h", "4h", "6h")
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Notification Settings",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "How often do you want to be reminded?",
                fontSize = 16.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box {
                Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A6BA5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(selectedInterval, fontSize = 16.sp, color = Color.White)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    intervals.forEach {
                        DropdownMenuItem(
                            text = {
                                Text(text = it, fontSize = 16.sp)
                            },
                            onClick = {
                                selectedInterval = it
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    prefs.edit().putString("notif_interval", selectedInterval).apply()
                    NotificationScheduler.scheduleRepeatingNotification(context, selectedInterval)
                    NotificationScheduler.scheduleDailyChallengeNotifications(context)
                    Toast.makeText(context, "Notification in $selectedInterval", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("Save & Activate", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
