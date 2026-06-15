package com.vasant.pillpal.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.vasant.pillpal.R
import com.vasant.pillpal.ui.navigation.MainUiRoute
import com.vasant.pillpal.ui.theme.SecondaryContainerColor
import com.vasant.pillpal.ui.theme.fontColor

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val isHomeSelected = currentRoute?.contains("HomeScreen") == true
    val isSettingsSelected = currentRoute?.contains("SettingScreen") == true

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Glassmorphic Bottom Bar with gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
        ) {
            // Gradient background layer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        clip = false
                    )
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
                            )
                        )
                    )
            )

            // Animated indicator pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 16.dp)
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home section
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (isHomeSelected) {
                        AnimatedIndicatorPill()
                    }
                    BottomNavItem(
                        icon = R.drawable.home,
                        isSelected = isHomeSelected,
                        onClick = {
                            navController.navigate(MainUiRoute.HomeScreen) {
                                popUpTo(MainUiRoute.HomeScreen) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        contentDescription = "Home"
                    )
                }

                // FAB spacer
                Box(modifier = Modifier.size(72.dp))

                // Settings section
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSettingsSelected) {
                        AnimatedIndicatorPill()
                    }
                    BottomNavItem(
                        icon = R.drawable.guides,
                        isSelected = isSettingsSelected,
                        onClick = {
                            navController.navigate(MainUiRoute.ChatScreen) {
                                launchSingleTop = true
                            }
                        },
                        contentDescription = "Settings"
                    )
                }
            }
        }

        // Gradient Floating Action Button with theme colors
        FloatingActionButton(
            onClick = {
                navController.navigate(MainUiRoute.AddMedicineScreen) {
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .offset(y = (-32).dp)
                .size(72.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = SecondaryContainerColor,
                    spotColor = SecondaryContainerColor.copy(alpha = 0.3f)
                ),
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            ),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                SecondaryContainerColor.copy(alpha = 0.9f),
                                SecondaryContainerColor,
                                SecondaryContainerColor.copy(alpha = 1.1f, red = SecondaryContainerColor.red * 0.85f, green = SecondaryContainerColor.green * 0.95f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Rotating background accent
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .rotate(45f)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = "Add Medicine",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedIndicatorPill() {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pillScale"
    )

    Box(
        modifier = Modifier
            .size(width = 64.dp, height = 44.dp)
            .scale(scale)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        SecondaryContainerColor.copy(alpha = 0.15f),
                        SecondaryContainerColor.copy(alpha = 0.25f),
                        SecondaryContainerColor.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
    )
}

@Composable
private fun BottomNavItem(
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    contentDescription: String
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.25f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) SecondaryContainerColor else fontColor.copy(alpha = 0.4f),
        animationSpec = tween(300),
        label = "iconColor"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 0f else -5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "rotation"
    )

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .rotate(rotation)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
    }
}