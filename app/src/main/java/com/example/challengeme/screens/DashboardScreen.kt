package com.example.challengeme.screens

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
import androidx.navigation.NavController
import com.example.challengeme.data.ChallengeMeDatabase
import com.example.challengeme.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DashboardScreen(navController: NavController, userId: Int) {
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
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF141E30), Color(0xFF243B55))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "👋 Welcome Back!",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            user?.let {
                Text(
                    text = "🔥 Your current streak: ${it.streak_days} day(s)",
                    color = Color(0xFF7CFC00),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            DashboardButton(
                text = "🗓️  Daily Challenge",
                onClick = { navController.navigate("daily") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DashboardButton(
                text = "🎲  Random Challenge",
                onClick = { navController.navigate("random") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DashboardButton(
                text = "🔥  Streak",
                onClick = { navController.navigate("streak/$userId") }
            )


            Spacer(modifier = Modifier.height(16.dp))

            DashboardButton(
                text = "⚙️  Settings",
                onClick = { navController.navigate("settings") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DashboardButton(
                text = "👤  Profile",
                onClick = { navController.navigate("profile/$userId") }
            )
        }
    }
}

@Composable
fun DashboardButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A6BA5)),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}
