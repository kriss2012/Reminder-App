package com.vasant.pillpal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.vasant.pillpal.R
import com.vasant.pillpal.ui.navigation.MainUiRoute
import com.vasant.pillpal.ui.navigation.NavigationRoute
import com.vasant.pillpal.ui.screens.HomeScreen
import com.vasant.pillpal.ui.theme.SecondaryContainerColor
import com.vasant.pillpal.ui.theme.jetbrainFamily
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun TopBarHomeScreen(navHostController: NavHostController, onNavigationIconClick: () -> Job) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .shadow(
                elevation = 2.dp,
                spotColor = Color.Black.copy(alpha = 0.05f)
            ),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Section - App Logo/Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // App Icon
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SecondaryContainerColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.medicine),
                            contentDescription = "App Logo",
                            tint = SecondaryContainerColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // App Name
                Column {
                    Text(
                        text = "Kiri Reminder",
                        fontFamily = jetbrainFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Your Health Companion",
                        fontFamily = jetbrainFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // Right Section - Action Icons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Notifications Icon Button
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(
                        onClick = {navHostController.navigate(MainUiRoute.NotificationScreen)},
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Settings Icon Button
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(
                        onClick = {
                           navHostController.navigate(MainUiRoute.SettingScreen)
                            //onNavigationIconClick()
                                  },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // User Profile Icon Button
                Surface(
                    shape = CircleShape,
                    color = SecondaryContainerColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.user),
                            contentDescription = "Profile",
                            tint = SecondaryContainerColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

    }
}