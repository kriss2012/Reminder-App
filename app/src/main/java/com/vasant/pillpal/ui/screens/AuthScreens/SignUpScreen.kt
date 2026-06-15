package com.vasant.pillpal.ui.screens.AuthScreens

import android.widget.Toast
import android.util.Patterns
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vasant.pillpal.R
import com.vasant.pillpal.ui.navigation.AuthenticationRoute
import com.vasant.pillpal.ui.theme.BackgroundColor
import com.vasant.pillpal.ui.theme.SecondaryContainerColor
import com.vasant.pillpal.ui.theme.rubikFamily
import com.vasant.pillpal.ui.viewmodel.FirebaseViewModel
import com.vasant.pillpal.utils.FirebaseState

@Composable
fun SignUpScreen(
    navController: NavController,
    windowSizeClass: WindowSizeClass,
    firebase: FirebaseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val isPasswordShown = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Derived validation states
    val emailError = email.value.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email.value).matches()
    val passwordError = password.value.isNotBlank() && password.value.length < 6
    val showMismatchError = confirmPassword.value.isNotEmpty() && password.value != confirmPassword.value

    val isLoading = firebase.firebaseState == FirebaseState.Loading

    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    // One-shot side effects for auth outcome
    LaunchedEffect(firebase.authState.Success) {
        if (firebase.authState.Success) {
            Toast.makeText(context, "Sign Up Successful", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            firebase.firebaseState = FirebaseState.IsIdle
        }
    }
    LaunchedEffect(firebase.authState.Error) {
        firebase.authState.Error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    // Adaptive tokens
    val horizontalPadding = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 20.dp
        WindowWidthSizeClass.Medium -> 32.dp
        else -> 48.dp
    }
    val verticalPadding = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 16.dp
        WindowWidthSizeClass.Medium -> 20.dp
        else -> 24.dp
    }
    val titleSize = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 36.sp
        WindowWidthSizeClass.Medium -> 44.sp
        else -> 52.sp
    }
    val formMaxWidth = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 560.dp
        WindowWidthSizeClass.Medium -> 640.dp
        else -> 720.dp
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundColor,
                        BackgroundColor.copy(alpha = 0.95f),
                        SecondaryContainerColor.copy(alpha = 0.1f)
                    )
                )
            )
            .systemBarsPadding()
            .imePadding()
    ) {
        // Decorative circles in background
        DecorativeBackground()

        // Main content with scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                    animationSpec = tween(600),
                    initialOffsetY = { -100 }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = formMaxWidth),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo/Icon section
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        SecondaryContainerColor,
                                        SecondaryContainerColor.copy(alpha = 0.8f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Card container
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.95f)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 8.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp)
                        ) {
                            // Title
                            Text(
                                "Create Account",
                                fontFamily = rubikFamily,
                                fontSize = titleSize,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryContainerColor,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Text(
                                "Sign up to get started",
                                fontFamily = rubikFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Gray,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 4.dp, bottom = 24.dp)
                            )

                            // Email Field
                            Text(
                                text = stringResource(R.string.text_email),
                                fontFamily = rubikFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = email.value,
                                onValueChange = { email.value = it },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Email,
                                        contentDescription = null,
                                        tint = if (emailError) MaterialTheme.colorScheme.error
                                               else SecondaryContainerColor
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Enter your email", color = Color.Gray.copy(alpha = 0.6f)) },
                                isError = emailError,
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SecondaryContainerColor,
                                    unfocusedBorderColor = Color.LightGray,
                                    unfocusedContainerColor = Color(0xFFF8F9FA),
                                    focusedContainerColor = Color(0xFFF8F9FA),
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black.copy(0.85f),
                                    errorBorderColor = MaterialTheme.colorScheme.error
                                ),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next,
                                    keyboardType = KeyboardType.Email
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                enabled = !isLoading
                            )

                            if (emailError) {
                                Text(
                                    "Please enter a valid email address",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            // Password Field
                            Text(
                                text = stringResource(R.string.text_password),
                                fontFamily = rubikFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = password.value,
                                onValueChange = { password.value = it },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (passwordError) MaterialTheme.colorScheme.error
                                               else SecondaryContainerColor
                                    )
                                },
                                visualTransformation = if (isPasswordShown.value) VisualTransformation.None
                                                       else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordShown.value = !isPasswordShown.value }) {
                                        Icon(
                                            painter = painterResource(
                                                if (isPasswordShown.value) R.drawable.eye else R.drawable.hidden
                                            ),
                                            contentDescription = if (isPasswordShown.value) "Hide password" else "Show password",
                                            modifier = Modifier.size(20.dp),
                                            tint = SecondaryContainerColor
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Enter password", color = Color.Gray.copy(alpha = 0.6f)) },
                                isError = passwordError,
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SecondaryContainerColor,
                                    unfocusedBorderColor = Color.LightGray,
                                    unfocusedContainerColor = Color(0xFFF8F9FA),
                                    focusedContainerColor = Color(0xFFF8F9FA),
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black.copy(0.85f),
                                    errorBorderColor = MaterialTheme.colorScheme.error
                                ),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next,
                                    keyboardType = KeyboardType.Password
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                enabled = !isLoading
                            )

                            // Enhanced password strength indicator
                            AnimatedVisibility(
                                visible = password.value.isNotBlank(),
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                PasswordStrengthIndicator(password = password.value)
                            }

                            if (passwordError) {
                                Text(
                                    "Password must be at least 6 characters",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            // Confirm Password Field
                            Text(
                                text = stringResource(R.string.text_confirm_password),
                                fontFamily = rubikFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = confirmPassword.value,
                                onValueChange = { confirmPassword.value = it },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (showMismatchError) MaterialTheme.colorScheme.error
                                               else SecondaryContainerColor
                                    )
                                },
                                visualTransformation = if (isPasswordShown.value) VisualTransformation.None
                                                       else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordShown.value = !isPasswordShown.value }) {
                                        Icon(
                                            painter = painterResource(
                                                if (isPasswordShown.value) R.drawable.eye else R.drawable.hidden
                                            ),
                                            contentDescription = if (isPasswordShown.value) "Hide password" else "Show password",
                                            modifier = Modifier.size(20.dp),
                                            tint = SecondaryContainerColor
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Re-enter password", color = Color.Gray.copy(alpha = 0.6f)) },
                                isError = showMismatchError,
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SecondaryContainerColor,
                                    unfocusedBorderColor = Color.LightGray,
                                    unfocusedContainerColor = Color(0xFFF8F9FA),
                                    focusedContainerColor = Color(0xFFF8F9FA),
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black.copy(0.85f),
                                    errorBorderColor = MaterialTheme.colorScheme.error
                                ),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Password
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                ),
                                enabled = !isLoading
                            )

                            if (showMismatchError) {
                                Text(
                                    "Passwords do not match",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }

                            Spacer(Modifier.height(28.dp))

                            // Sign Up Button with animation
                            val buttonScale by animateFloatAsState(
                                targetValue = if (isLoading) 0.95f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )

                            Button(
                                onClick = {
                                    val empty = email.value.isBlank() || password.value.isBlank() || confirmPassword.value.isBlank()
                                    if (!empty && !emailError && !passwordError && !showMismatchError) {
                                        firebase.singUp(email = email.value, password = password.value)
                                    } else {
                                        Toast.makeText(context, "Please check the details", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = !isLoading,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .scale(buttonScale),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SecondaryContainerColor,
                                    contentColor = Color.White,
                                    disabledContainerColor = Color(0xFFB0B0B0),
                                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation = 8.dp
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.5.dp
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Creating Account...",
                                        fontFamily = rubikFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )
                                } else {
                                    Text(
                                        "Sign Up",
                                        fontFamily = rubikFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            // Divider with text
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = Color.LightGray.copy(alpha = 0.5f)
                                )
                                Text(
                                    "OR",
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = Color.LightGray.copy(alpha = 0.5f)
                                )
                            }

                            Spacer(Modifier.height(20.dp))

                            // Sign In link with better styling
                            val annotated = buildAnnotatedString {
                                withStyle(SpanStyle(color = Color.Gray, fontSize = 14.sp)) {
                                    append("Already have an account? ")
                                }
                                pushStringAnnotation(tag = "login", annotation = "login")
                                withStyle(
                                    SpanStyle(
                                        color = SecondaryContainerColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                ) {
                                    append("Sign In")
                                }
                                pop()
                            }

                            ClickableText(
                                text = annotated,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textAlign = TextAlign.Center,
                                    fontFamily = rubikFamily
                                ),
                                onClick = { offset ->
                                    annotated.getStringAnnotations(tag = "login", start = offset, end = offset)
                                        .firstOrNull()?.let {
                                            navController.navigate(AuthenticationRoute.LoginScreen)
                                        }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Loading overlay with blur effect
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = SecondaryContainerColor)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Creating your account...",
                            fontFamily = rubikFamily,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordStrengthIndicator(password: String) {
    val strength = remember(password) {
        when {
            password.length < 6 -> 0
            password.length < 8 -> 1
            password.length < 10 -> 2
            password.any { it.isUpperCase() } &&
            password.any { it.isLowerCase() } &&
            password.any { it.isDigit() } -> 3
            else -> 2
        }
    }

    val (label, color, progress) = when (strength) {
        0 -> Triple("Too Short", Color(0xFFD32F2F), 0.25f)
        1 -> Triple("Weak", Color(0xFFFF6F00), 0.4f)
        2 -> Triple("Medium", Color(0xFFF9A825), 0.65f)
        3 -> Triple("Strong", Color(0xFF388E3C), 1f)
        else -> Triple("", Color.Gray, 0f)
    }

    Column(modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password Strength:",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun DecorativeBackground() {
    // Floating decorative circles
    Box(modifier = Modifier.fillMaxSize()) {
        // Top right circle
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = 100.dp, y = (-60).dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SecondaryContainerColor.copy(alpha = 0.15f),
                            SecondaryContainerColor.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Bottom left circle
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-80).dp, y = 80.dp)
                .align(Alignment.BottomStart)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SecondaryContainerColor.copy(alpha = 0.12f),
                            SecondaryContainerColor.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Center circle
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.Center)
                .offset(x = 120.dp, y = (-150).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SecondaryContainerColor.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}