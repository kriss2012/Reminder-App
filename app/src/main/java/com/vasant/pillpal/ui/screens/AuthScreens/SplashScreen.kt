package com.vasant.pillpal.ui.screens.AuthScreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vasant.pillpal.ui.navigation.AuthenticationRoute
import com.vasant.pillpal.ui.navigation.MainUiRoute
import com.vasant.pillpal.ui.navigation.NavigationRoute
import com.vasant.pillpal.ui.theme.BackgroundColor
import com.vasant.pillpal.ui.theme.SecondaryContainerColor
import com.vasant.pillpal.ui.theme.fontColor
import com.vasant.pillpal.ui.theme.rubikFamily
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, isLoggedIn: Boolean = false) {
    val logoVisible = remember { mutableStateOf(false) }
    val textVisible = remember { mutableStateOf(false) }
    val loadingVisible = remember { mutableStateOf(false) }
    val pillsAnimating = remember { mutableStateOf(false) }

    val logoScaleState = animateFloatAsState(
        targetValue = if (logoVisible.value) 1f else 0.1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val logoAlphaState = animateFloatAsState(
        targetValue = if (logoVisible.value) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = EaseInOutCubic),
        label = "logoAlpha"
    )

    val textScaleState = animateFloatAsState(
        targetValue = if (textVisible.value) 1f else 0.8f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "textScale"
    )

    // Infinite transitions for pulse and glow animations
    val infiniteTransition = rememberInfiniteTransition(label = "pulseRing")
    
    // Heartbeat pulse for the logo itself
    val heartbeatScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartbeatScale"
    )

    val ringScale1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseOutCubic),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringScale1"
    )
    val ringAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseOutCubic),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringAlpha1"
    )

    val ringScale2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, delayMillis = 1100, easing = EaseOutCubic),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringScale2"
    )
    val ringAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, delayMillis = 1100, easing = EaseOutCubic),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringAlpha2"
    )

    val loadingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loadingAlpha"
    )

    LaunchedEffect(Unit) {
        logoVisible.value = true
        delay(300)
        textVisible.value = true
        delay(200)
        pillsAnimating.value = true
        delay(300)
        loadingVisible.value = true
        delay(2200)

        if (isLoggedIn) {
            navController.navigate(MainUiRoute.HomeScreen) {
                popUpTo(NavigationRoute.AuthScreens) { inclusive = true }
            }
        } else {
            navController.navigate(AuthenticationRoute.WelcomeScreen) {
                popUpTo(AuthenticationRoute.SplashScreen) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundColor,
                        BackgroundColor,
                        Color(0xFFFFECE4) // Warm peach glow at the bottom
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .scale(textScaleState.value)
        ) {
            // Animated logo container with pulsing rings
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                if (pillsAnimating.value) {
                    // Ring 1 (Primary brand color)
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(ringScale1)
                            .alpha(ringAlpha1)
                            .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), CircleShape)
                    )
                    // Ring 2 (Warm accent color)
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(ringScale2)
                            .alpha(ringAlpha2)
                            .border(1.5.dp, Color(0xFFFFD166).copy(alpha = 0.6f), CircleShape)
                    )
                }

                // Core Pill Logo with shadow and heartbeat animation
                CustomPillLogo(
                    modifier = Modifier
                        .scale(logoScaleState.value)
                        .alpha(logoAlphaState.value),
                    pulseScale = if (pillsAnimating.value) heartbeatScale else 1.0f
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Text section
            AnimatedVisibility(
                visible = textVisible.value,
                enter = fadeIn(animationSpec = tween(700)) + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(700)
                ),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Kiri Reminder",
                            fontFamily = rubikFamily,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 50.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = 2.dp)
                                .blur(2.dp),
                            color = fontColor.copy(alpha = 0.15f)
                        )
                        Text(
                            text = "Kiri Reminder",
                            fontFamily = rubikFamily,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 50.sp,
                            modifier = Modifier.fillMaxWidth(),
                            color = fontColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your Medication Companion",
                        fontFamily = rubikFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryContainerColor,
                        letterSpacing = 1.2.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Smart reminders • Health tracking • AI guidance",
                        fontFamily = rubikFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = fontColor.copy(alpha = 0.6f),
                        letterSpacing = 0.5.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Loading Section
            AnimatedVisibility(
                visible = loadingVisible.value,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .width(130.dp)
                            .height(4.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading your health companion...",
                        fontFamily = rubikFamily,
                        fontSize = 13.sp,
                        color = fontColor.copy(alpha = loadingAlpha),
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Branding subtitle
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = "✨ Powered by AI Health Assistant",
                fontFamily = rubikFamily,
                fontSize = 12.sp,
                color = fontColor.copy(alpha = 0.45f),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
fun CustomPillLogo(modifier: Modifier = Modifier, pulseScale: Float) {
    Box(
        modifier = modifier
            .scale(pulseScale)
            .size(width = 110.dp, height = 55.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            .background(Color.White, RoundedCornerShape(28.dp))
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Left Half: Primary Accent Coral/Orange
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(topStart = 26.dp, bottomStart = 26.dp)
                    )
            )
            // Right Half: Bright Golden Yellow
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        Color(0xFFFFE66D),
                        RoundedCornerShape(topEnd = 26.dp, bottomEnd = 26.dp)
                    )
            )
        }
        
        // Premium Gloss Highlight to make it look smooth and 3D
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 5.dp, start = 16.dp)
                .size(width = 36.dp, height = 6.dp)
                .background(Color.White.copy(alpha = 0.45f), CircleShape)
        )
    }
}
