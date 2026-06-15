package com.vasant.pillpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vasant.pillpal.R
import com.vasant.pillpal.ui.notifications.NotificationItem
import com.vasant.pillpal.ui.notifications.NotificationType
import com.vasant.pillpal.ui.theme.SecondaryContainerColor
import com.vasant.pillpal.ui.theme.jetbrainFamily
import com.vasant.pillpal.ui.viewmodel.NotificationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val notifications by viewModel.items.collectAsState(initial = emptyList())
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Adaptive sizing
    val isTablet = screenWidth > 600.dp
    val maxWidth = if (isTablet) 800.dp else screenWidth

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notifications",
                        fontFamily = jetbrainFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isTablet) 24.sp else 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    if (notifications.any { !it.isRead }) {
                        TextButton(
                            onClick = { viewModel.markAllRead() },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                "Mark all read",
                                fontFamily = jetbrainFamily,
                                fontSize = if (isTablet) 15.sp else 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = SecondaryContainerColor
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (notifications.isEmpty()) {
                EmptyNotificationsState(
                    modifier = Modifier.widthIn(max = maxWidth),
                    isTablet = isTablet
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .widthIn(max = maxWidth),
                    contentPadding = PaddingValues(
                        horizontal = if (isTablet) 24.dp else 16.dp,
                        vertical = if (isTablet) 16.dp else 12.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(if (isTablet) 16.dp else 12.dp)
                ) {
                    items(notifications, key = { it.key }) { notification ->
                        NotificationCard(
                            notification = notification,
                            onClick = { viewModel.markRead(notification.key) },
                            isTablet = isTablet
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit,
    isTablet: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (isTablet) 20.dp else 16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(if (isTablet) 20.dp else 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isRead) 1.dp else 3.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isTablet) 20.dp else 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Notification Icon with gradient background
            Box(
                modifier = Modifier
                    .size(if (isTablet) 64.dp else 56.dp)
                    .clip(CircleShape)
                    .background(getNotificationColor(notification.type).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = getNotificationIcon(notification.type),
                    contentDescription = null,
                    tint = getNotificationColor(notification.type),
                    modifier = Modifier.size(if (isTablet) 32.dp else 28.dp)
                )
            }

            Spacer(modifier = Modifier.width(if (isTablet) 18.dp else 14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontFamily = jetbrainFamily,
                        fontSize = if (isTablet) 18.sp else 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(if (isTablet) 12.dp else 10.dp)
                                .clip(CircleShape)
                                .background(SecondaryContainerColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (isTablet) 8.dp else 6.dp))

                Text(
                    text = notification.message,
                    fontFamily = jetbrainFamily,
                    fontSize = if (isTablet) 16.sp else 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    lineHeight = if (isTablet) 24.sp else 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(if (isTablet) 10.dp else 8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (isTablet) 10.dp else 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(if (isTablet) 8.dp else 6.dp),
                        color = getNotificationColor(notification.type).copy(alpha = 0.1f),
                        modifier = Modifier.height(if (isTablet) 24.dp else 20.dp)
                    ) {
                        Text(
                            text = notification.type.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontFamily = jetbrainFamily,
                            fontSize = if (isTablet) 13.sp else 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = getNotificationColor(notification.type),
                            modifier = Modifier.padding(
                                horizontal = if (isTablet) 10.dp else 8.dp,
                                vertical = if (isTablet) 4.dp else 2.dp
                            )
                        )
                    }

                    Text(
                        text = "•",
                        fontFamily = jetbrainFamily,
                        fontSize = if (isTablet) 12.sp else 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )

                    Text(
                        text = notification.timeText,
                        fontFamily = jetbrainFamily,
                        fontSize = if (isTablet) 14.sp else 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyNotificationsState(modifier: Modifier = Modifier, isTablet: Boolean = false) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(if (isTablet) 64.dp else 48.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (isTablet) 140.dp else 120.dp)
                    .clip(CircleShape)
                    .background(SecondaryContainerColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.notificationbing),
                    contentDescription = null,
                    tint = SecondaryContainerColor,
                    modifier = Modifier.size(if (isTablet) 64.dp else 56.dp)
                )
            }

            Spacer(modifier = Modifier.height(if (isTablet) 32.dp else 28.dp))

            Text(
                text = "No Notifications",
                fontFamily = jetbrainFamily,
                fontSize = if (isTablet) 28.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(if (isTablet) 12.dp else 10.dp))

            Text(
                text = "You're all caught up! Check back later for updates on your medications.",
                fontFamily = jetbrainFamily,
                fontSize = if (isTablet) 17.sp else 15.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                lineHeight = if (isTablet) 26.sp else 22.sp
            )
        }
    }
}

@Composable
fun getNotificationIcon(type: NotificationType) = when (type) {
    NotificationType.REMINDER -> painterResource(R.drawable.alarm)
    NotificationType.MISSED -> painterResource(R.drawable.notificationbing)
    NotificationType.COMPLETED -> painterResource(R.drawable.medicine)
    NotificationType.SYSTEM -> painterResource(R.drawable.settingicon)
}

fun getNotificationColor(type: NotificationType): Color = when (type) {
    NotificationType.REMINDER -> Color(0xFF2196F3) // Blue
    NotificationType.MISSED -> Color(0xFFFF9800) // Orange
    NotificationType.COMPLETED -> Color(0xFF4CAF50) // Green
    NotificationType.SYSTEM -> Color(0xFF9C27B0) // Purple
}
