package com.example.challengeme

import androidx.compose.ui.graphics.Color
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.challengeme.data.ChallengeMeDatabase
import com.example.challengeme.repository.UserRepository
import com.example.challengeme.viewmodel.ProfileViewModel
import com.example.challengeme.viewmodel.ProfileViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, userId: Int) {
    val context = LocalContext.current
    val db = remember { ChallengeMeDatabase.getDatabase(context) }
    val repo = remember { UserRepository(db.userDao()) }
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(repo))

    
    val userState by viewModel.user.collectAsState()
    val isEditModeState by viewModel.isEditMode.collectAsState()

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var showDatePicker by remember { mutableStateOf(false) }


    LaunchedEffect(userId) {
        viewModel.loadUserData(userId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(R.drawable.applogo),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = userState?.username ?: "Username",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))


            OutlinedTextField(
                value = userState?.firstName ?: "",
                onValueChange = { viewModel.updateFirstName(it) },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditModeState,
                readOnly = !isEditModeState
            )

            Spacer(modifier = Modifier.height(8.dp))


            OutlinedTextField(
                value = userState?.lastName ?: "",
                onValueChange = { viewModel.updateLastName(it) },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditModeState,
                readOnly = !isEditModeState
            )

            Spacer(modifier = Modifier.height(8.dp))


            OutlinedTextField(
                value = userState?.email ?: "",
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                readOnly = true
            )

            Spacer(modifier = Modifier.height(8.dp))


            if (isEditModeState) {
                var expanded by remember { mutableStateOf(false) }
                val genders = listOf("Male", "Female", "Other")

                Box(modifier = Modifier.fillMaxWidth()) {
                    val interactionSource = remember { MutableInteractionSource() }

                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                expanded = true
                            }
                        }
                    }

                    OutlinedTextField(
                        value = userState?.gender ?: "",
                        onValueChange = {},
                        label = { Text("Gender") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        interactionSource = interactionSource
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        genders.forEach { gender ->
                            DropdownMenuItem(
                                text = { Text(gender) },
                                onClick = {
                                    viewModel.updateGender(gender)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = userState?.gender ?: "",
                    onValueChange = {},
                    label = { Text("Gender") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    readOnly = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            OutlinedTextField(
                value = userState?.dateOfBirth ?: "",
                onValueChange = {},
                label = { Text("Date of Birth") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditModeState,
                readOnly = true,
                trailingIcon = {
                    if (isEditModeState) {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                        }
                    }
                }
            )

            if (showDatePicker) {
                val calendar = Calendar.getInstance()
                val datePicker = android.app.DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        viewModel.updateDateOfBirth("$year-${month+1}-$day")
                        showDatePicker = false
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = userState?.address ?: "",
                onValueChange = { viewModel.updateAddress(it) },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditModeState,
                readOnly = !isEditModeState
            )

            Spacer(modifier = Modifier.height(8.dp))


            OutlinedTextField(
                value = userState?.streak_days?.toString() ?: "0",
                onValueChange = {},
                label = { Text("Streak Days") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                readOnly = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = {
                    if (isEditModeState) {
                        viewModel.saveProfile(
                            onSuccess = {
                                Toast.makeText(context, "Profile saved!", Toast.LENGTH_SHORT).show()
                                viewModel.toggleEditMode()
                            },
                            onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                        )
                    } else {
                        viewModel.toggleEditMode()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditModeState) "Save Profile" else "Edit Profile")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    viewModel.deleteProfile(
                        onSuccess = {
                            Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                popUpTo(0)
                            }
                        },
                        onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Account")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val prefs = context.getSharedPreferences("user_session", 0)
                    prefs.edit().clear().apply()

                    navController.navigate("login") {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log out", color = Color.White)
            }

        }
    }
}