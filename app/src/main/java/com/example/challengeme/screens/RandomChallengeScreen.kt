package com.example.challengeme.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.challengeme.viewmodel.RandomChallengeViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RandomChallengeScreen() {
    val context = LocalContext.current
    val viewModel = remember { RandomChallengeViewModel(context) }

    val randomChallenges = viewModel.challenges
    val completedChallenges = viewModel.completedChallenges

    LaunchedEffect(Unit) {
        viewModel.generateChallenges()
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🎲 Random Challenges",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Complete any of these fun tasks!",
            fontSize = 16.sp,
            color = Color(0xFFB0C4DE),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        randomChallenges.forEachIndexed { index, challenge ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2E4D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = challenge,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )

                    if (!completedChallenges.getOrNull(index).orFalse()) {
                        Button(
                            onClick = {
                                viewModel.markChallengeAsCompleted(index)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                        ) {
                            Text("Done", color = Color.White)
                        }
                    } else {
                        Text(
                            text = "✅",
                            fontSize = 18.sp,
                            color = Color(0xFF7CFC00)
                        )
                    }
                }
            }
        }
    }
}

private fun Boolean?.orFalse() = this ?: false
