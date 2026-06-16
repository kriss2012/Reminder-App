package com.vasant.pillpal.ui.screens.AuthScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import com.vasant.pillpal.ui.navigation.AuthenticationRoute
import com.vasant.pillpal.ui.theme.BackgroundColor
import com.vasant.pillpal.ui.theme.SecondaryContainerColor
import com.vasant.pillpal.ui.theme.fontColor
import com.vasant.pillpal.ui.theme.rubikFamily

@Composable
fun WelcomeScreen(
    navController: NavController,
    windowSizeClass: WindowSizeClass
) {
    val context = LocalContext.current

    // Adaptive tokens
    val horizontalPadding = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 24.dp
        WindowWidthSizeClass.Medium -> 32.dp
        else -> 48.dp
    }
    val verticalSpacing = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 24.dp
        WindowWidthSizeClass.Medium -> 32.dp
        else -> 40.dp
    }
    val titleSize = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 48.sp
        WindowWidthSizeClass.Medium -> 56.sp
        else -> 64.sp
    }
    val subtitleSize = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 20.sp
        WindowWidthSizeClass.Medium -> 24.sp
        else -> 28.sp
    }
    val bodySize = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 15.sp
        WindowWidthSizeClass.Medium -> 17.sp
        else -> 19.sp
    }
    val contentMaxWidth = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 560.dp
        WindowWidthSizeClass.Medium -> 640.dp
        else -> 720.dp
    }

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

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = horizontalPadding)
                .widthIn(max = contentMaxWidth)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App logo/icon placeholder (decorative circle)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(SecondaryContainerColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💊",
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(verticalSpacing))

            // App name
            Text(
                text = "Kiri Reminder",
                fontWeight = FontWeight.Bold,
                fontSize = titleSize,
                fontFamily = rubikFamily,
                color = fontColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Your Medication Companion",
                fontWeight = FontWeight.Medium,
                fontSize = subtitleSize,
                fontFamily = rubikFamily,
                color = SecondaryContainerColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(verticalSpacing))

            // Description
            Text(
                text = "Track your medicine and never miss a dose. Kiri Reminder helps you manage your medication schedule, sends timely reminders, and ensures you stay on top of your health journey with minimal effort.",
                color = fontColor.copy(0.7f),
                fontWeight = FontWeight.Normal,
                fontSize = bodySize,
                lineHeight = (bodySize.value + 6).sp,
                fontFamily = rubikFamily,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(verticalSpacing * 1.5f))

            // CTA Button
            Button(
                onClick = { navController.navigate(AuthenticationRoute.LoginScreen) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryContainerColor,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Get Started",
                        fontFamily = rubikFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Guest Login Button
            Button(
                onClick = {
                    // Save guest mode to shared preferences
                    val prf = context.getSharedPreferences("login", Context.MODE_PRIVATE)
                    prf.edit().putBoolean("IS_GUEST", true).apply()
                    prf.edit().putBoolean("IS_LOGGED_IN", true).apply()
                    navController.navigate(AuthenticationRoute.GuestLoginScreen) {
                        popUpTo(AuthenticationRoute.WelcomeScreen) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryContainerColor.copy(alpha = 0.2f),
                    contentColor = SecondaryContainerColor
                )
            ) {
                Text(
                    text = "Continue as Guest",
                    fontFamily = rubikFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Secondary action
            Text(
                text = "Already have an account? Sign in",
                color = fontColor.copy(0.6f),
                fontSize = 14.sp,
                fontFamily = rubikFamily,
                modifier = Modifier
                    .clickable { navController.navigate(AuthenticationRoute.LoginScreen) }
                    .padding(8.dp)
            )
        }
    }
}
