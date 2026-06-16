package com.vasant.pillpal.ui.screens.AuthScreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun SplashScreen(navController: NavController, isLoggedIn: Boolean = false) {
    val logoVisible = remember { mutableStateOf(false) }
    val textVisible = remember { mutableStateOf(false) }
    val loadingVisible = remember { mutableStateOf(false) }
    val particlesVisible = remember { mutableStateOf(false) }
    val pillsAnimating = remember { mutableStateOf(false) }
    val pulseState = remember { mutableStateOf(0f) }
    val ringState = remember { mutableStateOf(0f) }
    val waveState = remember { mutableStateOf(0f) }

    val logoScale = animateFloatAsState(
        targetValue = if (logoVisible.value) 1f else 0.2f,
        animationSpec = tween(durationMillis = 1200, easing = EaseInOutCubic),
        label = "logoScale"
    )

    val logoAlpha = animateFloatAsState(
        targetValue = if (logoVisible.value) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = EaseInOutCubic),
        label = "logoAlpha"
    )

    val logoRotation = animateFloatAsState(
        targetValue = if (logoVisible.value) 720f else 0f,
        animationSpec = tween(durationMillis = 3000, easing = EaseInOutCubic),
        label = "logoRotation"
    )

    val particleAlpha = animateFloatAsState(
        targetValue = if (particlesVisible.value) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "particleAlpha"
    )

    val textScale = animateFloatAsState(
        targetValue = if (textVisible.value) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1000, easing = EaseInOutCubic),
        label = "textScale"
    )

    LaunchedEffect(Unit) {
        logoVisible.value = true
        delay(200)
        particlesVisible.value = true
        delay(200)
        textVisible.value = true
        delay(200)
        pillsAnimating.value = true
        delay(300)
        loadingVisible.value = true
        delay(2300)

        if (isLoggedIn) {
            // Navigate to Home Screen if user is logged in
            navController.navigate(MainUiRoute.HomeScreen) {
                popUpTo(NavigationRoute.AuthScreens) { inclusive = true }
            }
        } else {
            // Navigate to Welcome Screen if user is not logged in
            navController.navigate(AuthenticationRoute.WelcomeScreen) {
                popUpTo(AuthenticationRoute.SplashScreen) { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30)
            pulseState.value = (pulseState.value + 2) % 360
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(20)
            ringState.value = (ringState.value + 3) % 360
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(25)
            waveState.value = (waveState.value + 4) % 360
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        AnimatedBackgroundGradient(pulseState.value, ringState.value, waveState.value)
        HeavyParticleSystem1(alpha = particleAlpha.value)
        HeavyParticleSystem2(alpha = particleAlpha.value)
        HeavyParticleSystem3(alpha = particleAlpha.value)
        ExpandingWaveRings()
        ShockwaveEffect(pulseState.value)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .scale(textScale.value)
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .rotate(logoRotation.value),
                contentAlignment = Alignment.Center
            ) {
                repeat(3) { layerIndex ->
                    val layerAlpha = animateFloatAsState(
                        targetValue = ((sin((pulseState.value + layerIndex * 90) * 0.05f) + 1) / 2).coerceIn(0f, 1f),
                        animationSpec = tween(100),
                        label = "glowAlpha$layerIndex"
                    )

                    Box(
                        modifier = Modifier
                            .size((140 + layerIndex * 20).dp)
                            .clip(CircleShape)
                            .alpha(layerAlpha.value * 0.3f)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        SecondaryContainerColor.copy(alpha = 0.4f),
                                        SecondaryContainerColor.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(145.dp)
                        .drawBehind {
                            drawCircle(
                                color = Color(0xFFFF6B6B).copy(alpha = 0.4f),
                                radius = 72.dp.toPx(),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                )

                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4ECDC4).copy(alpha = 0.2f),
                                    Color(0xFFFFE66D).copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                        .blur(8.dp)
                )

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .blur(6.dp)
                        .alpha(
                            ((sin((pulseState.value) * 0.03f) + 1) / 2)
                                .coerceIn(0.1f, 0.5f)
                        )
                        .background(
                            Color.Black.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                )

                Text(
                    text = "💊",
                    fontSize = 110.sp,
                    modifier = Modifier
                        .size(130.dp)
                        .align(Alignment.Center)
                )

                if (pillsAnimating.value) {
                    UltraFastOrbittingPills()
                    TripleOrbittingPills()
                    FastRotatingRing()
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            AnimatedVisibility(
                visible = textVisible.value,
                enter = fadeIn(animationSpec = tween(900)) + slideInVertically(
                    initialOffsetY = { 80 },
                    animationSpec = tween(900)
                ),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        repeat(3) { shadowIndex ->
                            Text(
                                text = "Kiri Reminder",
                                fontFamily = rubikFamily,
                                fontSize = 52.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier
                                    .blur((shadowIndex + 1).dp)
                                    .offset(y = (shadowIndex * 2).dp),
                                color = fontColor.copy(alpha = 0.15f - shadowIndex * 0.05f)
                            )
                        }
                        Text(
                            text = "Kiri Reminder",
                            fontFamily = rubikFamily,
                            fontSize = 52.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = fontColor
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your Medication Companion",
                        fontFamily = rubikFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryContainerColor,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { index ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (index) {
                                            0 -> Color(0xFFFF6B6B)
                                            1 -> Color(0xFF4ECDC4)
                                            else -> Color(0xFFFFE66D)
                                        }
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Smart reminders • Health tracking • AI guidance",
                        fontFamily = rubikFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = fontColor.copy(alpha = 0.7f),
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(70.dp))

            AnimatedVisibility(
                visible = loadingVisible.value,
                enter = fadeIn(animationSpec = tween(600)),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UltraHeavyLoadingDots()
                    Spacer(modifier = Modifier.height(20.dp))
                    PulsingLoadingText()
                }
            }
        }

        TopCornerDecorations(pulseState.value)
        HeavyDecorativeBottomBar(alpha = logoAlpha.value, pulseState = pulseState.value)
    }
}

@Composable
private fun AnimatedBackgroundGradient(pulse: Float, ring: Float, wave: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SecondaryContainerColor.copy(alpha = 0.12f + sin(pulse * 0.01f) * 0.05f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.06f + sin(ring * 0.01f) * 0.04f),
                        Color.Transparent,
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f + sin(wave * 0.01f) * 0.03f)
                    )
                )
            )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        SecondaryContainerColor.copy(alpha = 0.04f + sin(pulse * 0.02f) * 0.03f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun HeavyParticleSystem1(alpha: Float) {
    repeat(15) { index ->
        val offsetX = remember { mutableStateOf(0f) }
        val offsetY = remember { mutableStateOf(0f) }
        val rotation = remember { mutableStateOf(0f) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(20)
                offsetY.value -= 2
                offsetX.value += sin(offsetY.value * 0.05f)
                rotation.value = (rotation.value + 5) % 360
                if (offsetY.value < -400) offsetY.value = 400f
            }
        }

        Box(
            modifier = Modifier
                .offset(
                    x = (Random.nextDouble(-200.0, 200.0).toFloat()).dp + offsetX.value.dp,
                    y = (Random.nextDouble(-100.0, 700.0).toFloat()).dp + offsetY.value.dp
                )
                .size((1 + index % 4).dp)
                .clip(CircleShape)
                .rotate(rotation.value)
                .background(
                    Color(0xFFFF6B6B).copy(
                        alpha = alpha * (0.4f - index * 0.008f)
                    )
                )
        )
    }
}

@Composable
private fun HeavyParticleSystem2(alpha: Float) {
    repeat(12) { index ->
        val offsetX = remember { mutableStateOf(0f) }
        val offsetY = remember { mutableStateOf(0f) }
        val scale = remember { mutableStateOf(1f) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(30)
                offsetY.value -= 1.5f
                offsetX.value += cos(offsetY.value * 0.03f)
                scale.value = 0.5f + sin(offsetY.value * 0.02f) * 0.5f
                if (offsetY.value < -400) offsetY.value = 400f
            }
        }

        Box(
            modifier = Modifier
                .offset(
                    x = (Random.nextDouble(-200.0, 200.0).toFloat()).dp + offsetX.value.dp,
                    y = (Random.nextDouble(-100.0, 700.0).toFloat()).dp + offsetY.value.dp
                )
                .size((2 + index % 3).dp)
                .scale(scale.value)
                .clip(CircleShape)
                .background(
                    Color(0xFF4ECDC4).copy(
                        alpha = alpha * (0.35f - index * 0.007f)
                    )
                )
        )
    }
}

@Composable
private fun HeavyParticleSystem3(alpha: Float) {
    repeat(10) { index ->
        val offsetX = remember { mutableStateOf(0f) }
        val offsetY = remember { mutableStateOf(0f) }
        val rotation = remember { mutableStateOf(0f) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(30)
                offsetY.value -= 1f
                offsetX.value += sin(offsetY.value * 0.04f) * 1.5f
                rotation.value = (rotation.value + 3) % 360
                if (offsetY.value < -400) offsetY.value = 400f
            }
        }

        Box(
            modifier = Modifier
                .offset(
                    x = (Random.nextDouble(-200.0, 200.0).toFloat()).dp + offsetX.value.dp,
                    y = (Random.nextDouble(-100.0, 700.0).toFloat()).dp + offsetY.value.dp
                )
                .size((1 + index % 3).dp)
                .rotate(rotation.value)
                .clip(CircleShape)
                .background(
                    Color(0xFFFFE66D).copy(
                        alpha = alpha * (0.3f - index * 0.006f)
                    )
                )
        )
    }
}

@Composable
private fun ExpandingWaveRings() {
    repeat(5) { ringIndex ->
        val radiusMultiplier = 50 + ringIndex * 30
        val alpha = 0.8f - ringIndex * 0.1f

        Box(
            modifier = Modifier
                .size((radiusMultiplier * 2).dp)
                .drawBehind {
                    drawCircle(
                        color = when (ringIndex % 3) {
                            0 -> Color(0xFFFF6B6B).copy(alpha = alpha * 0.3f)
                            1 -> Color(0xFF4ECDC4).copy(alpha = alpha * 0.3f)
                            else -> Color(0xFFFFE66D).copy(alpha = alpha * 0.3f)
                        },
                        radius = (radiusMultiplier / 2).dp.toPx(),
                        style = Stroke(width = 1.5f.dp.toPx())
                    )
                }
        )
    }
}

@Composable
private fun ShockwaveEffect(pulse: Float) {
    val shockwaveRadius = (100 + sin(pulse * 0.02f) * 150).coerceIn(100f, 250f)

    Box(
        modifier = Modifier
            .size((shockwaveRadius * 2).dp)
            .drawBehind {
                drawCircle(
                    color = Color(0xFF4ECDC4).copy(alpha = 0.15f),
                    radius = shockwaveRadius.dp.toPx(),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
    )
}

@Composable
private fun UltraFastOrbittingPills() {
    val rotation = remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(10)
            rotation.value = (rotation.value + 4) % 360
        }
    }

    repeat(6) { index ->
        val angle = (rotation.value + index * 60) * (Math.PI / 180)
        val radius = 100.dp
        val offsetX = (radius.value * cos(angle)).dp
        val offsetY = (radius.value * sin(angle)).dp

        Box(
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .size(25.dp)
                .clip(CircleShape)
                .background(
                    when (index % 3) {
                        0 -> Color(0xFFFF6B6B).copy(alpha = 0.7f)
                        1 -> Color(0xFF4ECDC4).copy(alpha = 0.7f)
                        else -> Color(0xFFFFE66D).copy(alpha = 0.7f)
                    }
                )
                .blur(1.dp)
        )
    }
}

@Composable
private fun TripleOrbittingPills() {
    val rotation = remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(15)
            rotation.value = (rotation.value - 3) % 360
        }
    }

    repeat(4) { index ->
        val angle = (rotation.value + index * 90) * (Math.PI / 180)
        val radius = 65.dp
        val offsetX = (radius.value * cos(angle)).dp
        val offsetY = (radius.value * sin(angle)).dp

        Box(
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .size(18.dp)
                .clip(CircleShape)
                .background(
                    Color(0xFFFF6B6B).copy(alpha = 0.5f)
                )
        )
    }
}

@Composable
private fun FastRotatingRing() {
    val rotation = remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(12)
            rotation.value = (rotation.value + 6) % 360
        }
    }

    Box(
        modifier = Modifier
            .size(155.dp)
            .rotate(rotation.value)
            .drawBehind {
                drawCircle(
                    color = Color(0xFF4ECDC4).copy(alpha = 0.4f),
                    radius = 77.5.dp.toPx(),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
    )
}

@Composable
private fun UltraHeavyLoadingDots() {
    val animationDuration = 600
    val states = remember {
        listOf(
            mutableStateOf(0),
            mutableStateOf(0),
            mutableStateOf(0),
            mutableStateOf(0),
            mutableStateOf(0)
        )
    }

    val scales = states.map { state ->
        animateFloatAsState(
            targetValue = if (state.value % 2 == 0) 0.4f else 1.4f,
            animationSpec = tween(animationDuration),
            label = "scale${state.value}"
        )
    }

    val alphas = states.map { state ->
        animateFloatAsState(
            targetValue = if (state.value % 2 == 0) 0.3f else 1f,
            animationSpec = tween(animationDuration),
            label = "alpha${state.value}"
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(animationDuration.toLong())
            states[0].value++
            delay(100)
            states[1].value++
            delay(100)
            states[2].value++
            delay(100)
            states[3].value++
            delay(100)
            states[4].value++
        }
    }

    Row(
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .scale(scales[index].value)
                    .alpha(alphas[index].value)
                    .blur(1.dp)
                    .clip(CircleShape)
                    .background(
                        when (index % 3) {
                            0 -> Color(0xFFFF6B6B)
                            1 -> Color(0xFF4ECDC4)
                            else -> Color(0xFFFFE66D)
                        }
                    )
            )
        }
    }
}

@Composable
private fun PulsingLoadingText() {
    val pulseAlpha = remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(40)
            pulseAlpha.value = (sin(pulseAlpha.value * 0.1f) + 1) / 2
        }
    }

    Text(
        text = "Loading your health companion...",
        fontFamily = rubikFamily,
        fontSize = 13.sp,
        color = fontColor.copy(alpha = 0.5f + pulseAlpha.value * 0.4f),
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.6.sp
    )
}

@Composable
private fun TopCornerDecorations(pulse: Float) {
    repeat(4) { index ->
        val offsetX = (index % 2) * 350
        val offsetY = (index / 2) * 150

        Box(
            modifier = Modifier
                .offset(x = offsetX.dp, y = offsetY.dp)
                .size((20 + index * 3).dp)
                .alpha(
                    (sin((pulse + index * 60) * 0.02f) + 1) / 2 * 0.3f
                )
                .clip(CircleShape)
                .background(
                    when (index % 3) {
                        0 -> Color(0xFFFF6B6B)
                        1 -> Color(0xFF4ECDC4)
                        else -> Color(0xFFFFE66D)
                    }
                )
                .blur(2.dp)
        )
    }
}

@Composable
private fun HeavyDecorativeBottomBar(alpha: Float, pulseState: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(240.dp)
                .height(80.dp)
                .alpha(alpha)
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFFFF6B6B).copy(alpha = 0.5f + sin(pulseState * 0.01f) * 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box {
                Text(
                    text = "✨ Powered by AI Health Assistant",
                    fontFamily = rubikFamily,
                    fontSize = 11.sp,
                    color = fontColor.copy(alpha = 0.2f),
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.blur(1.5.dp)
                )
                Text(
                    text = "✨ Powered by AI Health Assistant",
                    fontFamily = rubikFamily,
                    fontSize = 11.sp,
                    color = fontColor.copy(
                        alpha = 0.4f + sin(pulseState * 0.015f) * 0.3f
                    ),
                    fontWeight = FontWeight.Light
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(
                                when (index) {
                                    0 -> Color(0xFFFF6B6B)
                                    1 -> Color(0xFF4ECDC4)
                                    else -> Color(0xFFFFE66D)
                                }.copy(
                                    alpha = 0.4f + sin((pulseState + index * 120) * 0.02f) * 0.4f
                                )
                            )
                    )
                }
            }
        }
    }
}
