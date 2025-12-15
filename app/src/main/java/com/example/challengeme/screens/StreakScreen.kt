package com.example.challengeme.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.challengeme.data.ChallengeMeDatabase
import com.example.challengeme.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun StreakScreen(userId: Int) {
    val context = LocalContext.current
    var user by remember { mutableStateOf<UserEntity?>(null) }

    LaunchedEffect(userId) {
        val db = ChallengeMeDatabase.getDatabase(context)
        val dao = db.userDao()
        withContext(Dispatchers.IO) {
            user = dao.findById(userId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF360033), Color(0xFF0b8793))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "🔥 Your Streak",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                shape = CircleShape,
                color = Color(0xFF7CFC00),
                shadowElevation = 8.dp,
                modifier = Modifier.size(150.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${user?.streak_days ?: 0}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Keep it up! One day at a time 💪",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
