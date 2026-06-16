package com.vasant.pillpal.ui.screens.AuthScreens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vasant.pillpal.ui.navigation.AuthenticationRoute
import com.vasant.pillpal.ui.navigation.MainUiRoute
import com.vasant.pillpal.ui.navigation.NavigationRoute
import com.vasant.pillpal.ui.theme.BackgroundColor
import com.vasant.pillpal.ui.theme.SecondaryContainerColor
import com.vasant.pillpal.ui.theme.rubikFamily
import com.vasant.pillpal.ui.viewmodel.FirebaseViewModel
import com.vasant.pillpal.utils.FirebaseState

const val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
private fun checkEmail(email: String): Boolean {
    return email.matches(emailRegex.toRegex())
}

@Composable
fun SignIn(
    navController: NavController,
    windowSizeClass: WindowSizeClass,
    viewModel: FirebaseViewModel = hiltViewModel()
) {
    val email = remember { mutableStateOf("") }
    val context = LocalContext.current
    val password = remember { mutableStateOf("") }
    val showErrors = remember { mutableStateOf(false) }
    val isPasswordShown = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Subtle gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SecondaryContainerColor.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Loading overlay
        if (viewModel.firebaseState == FirebaseState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SecondaryContainerColor)
            }
        } else if (viewModel.authState.Success) {
            val sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
            sharedPreferences.edit { putBoolean("IS_LOGGED_IN", true) }
            Toast.makeText(context, "Sign In successful!", Toast.LENGTH_SHORT).show()
            navController.navigate(MainUiRoute.HomeScreen) {
                popUpTo(NavigationRoute.AuthScreens) { inclusive = true }
            }
            viewModel.firebaseState = FirebaseState.IsIdle
        } else if (viewModel.authState.Error != null) {
            Toast.makeText(context, "${viewModel.authState.Error}", Toast.LENGTH_SHORT).show()
        }

        // Centered content card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Decorative icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SecondaryContainerColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💊",
                        fontSize = 40.sp
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Welcome Back",
                    fontFamily = rubikFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Sign in to continue",
                    fontFamily = rubikFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(Modifier.height(32.dp))

                // Email
                Text(
                    text = "Email",
                    fontFamily = rubikFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, bottom = 6.dp)
                )
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = SecondaryContainerColor
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("demo@email.com", color = Color.Gray) },
                    isError = showErrors.value && !checkEmail(email.value),
                    singleLine = true,
                    supportingText = if (showErrors.value && !checkEmail(email.value)) {
                        { Text("Enter a valid email address", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SecondaryContainerColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next, keyboardType = KeyboardType.Email
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                )

                Spacer(Modifier.height(16.dp))

                // Password
                Text(
                    text = "Password",
                    fontFamily = rubikFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, bottom = 6.dp)
                )
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = SecondaryContainerColor
                        )
                    },
                    visualTransformation = if (isPasswordShown.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordShown.value = !isPasswordShown.value }) {
                            val iconRes = if (isPasswordShown.value) com.vasant.pillpal.R.drawable.eye else com.vasant.pillpal.R.drawable.hidden
                            Icon(
                                painter = androidx.compose.ui.res.painterResource(iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your password", color = Color.Gray) },
                    isError = showErrors.value && password.value.isEmpty(),
                    singleLine = true,
                    supportingText = if (showErrors.value && password.value.isEmpty()) {
                        { Text("Password cannot be empty", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SecondaryContainerColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done, keyboardType = KeyboardType.Password
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            showErrors.value = true
                            if (checkEmail(email.value) && password.value.isNotEmpty()) {
                                viewModel.login(email = email.value, password = password.value)
                            } else {
                                focusManager.clearFocus()
                            }
                        }
                    ),
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        showErrors.value = true
                        if (checkEmail(email.value) && password.value.isNotEmpty()) {
                            viewModel.login(email = email.value, password = password.value)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecondaryContainerColor,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFB0B0B0),
                        disabledContentColor = Color.White.copy(alpha = 0.7f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Text(
                        "Sign In",
                        fontFamily = rubikFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Don't have an account?",
                        fontFamily = rubikFamily,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    TextButton(onClick = { navController.navigate(AuthenticationRoute.SingUpScreen) }) {
                        Text(
                            text = "Sign Up",
                            color = SecondaryContainerColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = rubikFamily,
                            fontSize = 14.sp
                        )
                    }
                }
            }

        }
    }
}