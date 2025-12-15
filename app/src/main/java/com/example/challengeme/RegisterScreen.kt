package com.example.challengeme

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
import com.example.challengeme.data.UserEntity
import com.example.challengeme.repository.UserRepository
import com.example.challengeme.viewmodel.UserViewModel
import com.example.challengeme.viewmodel.UserViewModelFactory

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val db = remember { ChallengeMeDatabase.getDatabase(context) }
    val repo = remember { UserRepository(db.userDao()) }
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(repo))

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(R.drawable.applogo), contentDescription = "App logo")
        Spacer(modifier = Modifier.height(20.dp))

        Text("Create your ChallengeMe account")
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email address") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter your password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Repeat the password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password == confirmPassword && password.length >= 6) {
                    val user = UserEntity(
                        firstName = "Ime", // placeholder for now
                        lastName = "Prezime",
                        username = email.substringBefore("@"), // generate username
                        email = email,
                        password = password,
                        gender = null,
                        dateOfBirth = null,
                        address = null,
                        streak_days = 0,
                        achievementId = 1
                    )
                    viewModel.register(
                        user,
                        onSuccess = {
                            Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") { popUpTo("register") { inclusive = true } }
                        },
                        onError = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(context, "Please fill all fields correctly!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register!")
        }

        Spacer(modifier = Modifier.height(15.dp))

        TextButton(onClick = {
            navController.navigate("login")
        }) {
            Text("Already have an account? Log In!")
        }
    }
}
