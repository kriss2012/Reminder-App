package com.vasant.pillpal.ui.screens.AuthScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import com.vasant.pillpal.ui.navigation.NavigationRoute
import com.vasant.pillpal.ui.theme.BackgroundColor
import com.vasant.pillpal.ui.theme.SecondaryContainerColor
import com.vasant.pillpal.ui.theme.fontColor
import com.vasant.pillpal.ui.theme.rubikFamily

@Composable
fun GuestLoginScreen(navController: NavController, windowSizeClass: WindowSizeClass? = null) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SecondaryContainerColor.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                )
            )
    ) {
        // Back Button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = fontColor,
                modifier = Modifier.size(24.dp)
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(24.dp)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(SecondaryContainerColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Guest Access",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                fontFamily = rubikFamily,
                color = fontColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "Explore Kiri Reminder Without Creating an Account",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                fontFamily = rubikFamily,
                color = SecondaryContainerColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Features List
            GuestFeatureItem(
                icon = "📋",
                title = "Track Medications",
                description = "Add and manage your medications easily"
            )

            Spacer(modifier = Modifier.height(16.dp))

            GuestFeatureItem(
                icon = "⏰",
                title = "Set Reminders",
                description = "Never miss a dose with smart notifications"
            )

            Spacer(modifier = Modifier.height(16.dp))

            GuestFeatureItem(
                icon = "💬",
                title = "Get Health Advice",
                description = "Chat with our AI health assistant"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Warning Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFF3CD).copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "⚠️ Guest data is temporary and will be cleared when you close the app.",
                    fontFamily = rubikFamily,
                    fontSize = 12.sp,
                    color = Color(0xFF856404),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Continue Button
            Button(
                onClick = {
                    navController.navigate(NavigationRoute.MainScreens) {
                        popUpTo(NavigationRoute.AuthScreens) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryContainerColor,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Continue as Guest",
                    fontFamily = rubikFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun GuestFeatureItem(
    icon: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = SecondaryContainerColor.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 28.sp,
            modifier = Modifier.size(44.dp)
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column {
            Text(
                text = title,
                fontFamily = rubikFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = fontColor
            )
            Text(
                text = description,
                fontFamily = rubikFamily,
                fontSize = 12.sp,
                color = fontColor.copy(0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

