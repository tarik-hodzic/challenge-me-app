package com.example.challengeme

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.challengeme.data.ChallengeMeDatabase
import com.example.challengeme.repository.UserRepository
import com.example.challengeme.viewmodel.UserViewModel
import com.example.challengeme.viewmodel.UserViewModelFactory

@Composable
fun LoginScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val db = remember { ChallengeMeDatabase.getDatabase(context) }
    val repo = remember { UserRepository(db.userDao()) }
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(repo))

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(R.drawable.applogo), contentDescription = "App logo")
        Spacer(modifier = Modifier.height(20.dp))

        Text("Welcome Achiever!")
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter email address") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                viewModel.login(email, password) { user ->
                    if (user != null) {
                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                        val sessionPrefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        sessionPrefs.edit().putInt("user_id", user.userId).apply()
                        navController.navigate("dashboard/${user.userId}")

                    } else {
                        Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }

        Spacer(modifier = Modifier.height(15.dp))

        TextButton(onClick = {
            Toast.makeText(context, "Password forgotten clicked!", Toast.LENGTH_SHORT).show()
        }) {
            Text("Password Forgotten?")
        }

        TextButton(onClick = {
            navController.navigate("register")
        }) {
            Text("Don't have an account? Register now!")
        }

    }

}
