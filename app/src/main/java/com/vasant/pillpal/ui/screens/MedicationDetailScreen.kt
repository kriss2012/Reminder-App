package com.vasant.pillpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.vasant.pillpal.R
import com.vasant.pillpal.ui.theme.SecondaryContainerColor
import com.vasant.pillpal.ui.theme.jetbrainFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDetailScreen(navController: NavHostController) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Medication Details",
                        fontFamily = jetbrainFamily,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Edit, "Edit", tint = SecondaryContainerColor)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFEF5350))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header Card
            item {
                MedicationHeaderCard()
            }

            // Quick Actions
            item {
                Spacer(modifier = Modifier.height(16.dp))
                QuickActionsSection()
            }

            // Details Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Medication Information",
                    fontFamily = jetbrainFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                MedicationInfoSection()
            }

            // Schedule Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Schedule",
                    fontFamily = jetbrainFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                ScheduleSection()
            }

            // Usage Instructions
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Usage Instructions",
                    fontFamily = jetbrainFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                UsageInstructionsSection()
            }

            // Recent History
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Recent History",
                    fontFamily = jetbrainFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                RecentHistorySection()
            }
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text(
                        "Delete Medication?",
                        fontFamily = jetbrainFamily,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to delete this medication? This action cannot be undone.",
                        fontFamily = jetbrainFamily
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            navController.navigateUp()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF5350)
                        )
                    ) {
                        Text("Delete", fontFamily = jetbrainFamily)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel", fontFamily = jetbrainFamily)
                    }
                }
            )
        }
    }
}

@Composable
fun MedicationHeaderCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SecondaryContainerColor,
                            SecondaryContainerColor.copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Medicine Icon
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.medicine),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Aspirin",
                        fontFamily = jetbrainFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = "100mg • Tablet",
                            fontFamily = jetbrainFamily,
                            fontSize = 14.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Active",
                            fontFamily = jetbrainFamily,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionButton(
            icon = Icons.Filled.CheckCircle,
            label = "Mark Taken",
            color = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Outlined.NotificationsOff,
            label = "Skip",
            color = Color(0xFFFF9800),
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Outlined.Alarm,
            label = "Snooze",
            color = Color(0xFF2196F3),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        onClick = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontFamily = jetbrainFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
fun MedicationInfoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoCard(
            icon = Icons.Outlined.Medication,
            label = "Medicine Name",
            value = "Aspirin"
        )
        InfoCard(
            icon = Icons.Outlined.Numbers,
            label = "Dosage",
            value = "100mg"
        )
        InfoCard(
            icon = Icons.Outlined.Category,
            label = "Type",
            value = "Tablet"
        )
        InfoCard(
            icon = Icons.Outlined.Science,
            label = "Active Ingredient",
            value = "Acetylsalicylic acid"
        )
        InfoCard(
            icon = Icons.Outlined.LocalHospital,
            label = "Purpose",
            value = "Pain relief & blood thinner"
        )
    }
}

@Composable
fun InfoCard(icon: ImageVector, label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SecondaryContainerColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    fontFamily = jetbrainFamily,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = value,
                    fontFamily = jetbrainFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ScheduleSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ScheduleItem("Morning", "8:00 AM", true)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            ScheduleItem("Afternoon", "2:00 PM", true)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            ScheduleItem("Evening", "8:00 PM", false)
        }
    }
}

@Composable
fun ScheduleItem(label: String, time: String, isActive: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = if (isActive) SecondaryContainerColor.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Alarm,
                        contentDescription = null,
                        tint = if (isActive) SecondaryContainerColor else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    fontFamily = jetbrainFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = time,
                    fontFamily = jetbrainFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Switch(
            checked = isActive,
            onCheckedChange = {},
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SecondaryContainerColor
            )
        )
    }
}

@Composable
fun UsageInstructionsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            InstructionItem(
                icon = Icons.Outlined.Restaurant,
                text = "Take with food or milk to prevent stomach upset"
            )
            Spacer(modifier = Modifier.height(12.dp))
            InstructionItem(
                icon = Icons.Outlined.WaterDrop,
                text = "Drink a full glass of water with each dose"
            )
            Spacer(modifier = Modifier.height(12.dp))
            InstructionItem(
                icon = Icons.Outlined.Warning,
                text = "Do not take if allergic to NSAIDs"
            )
            Spacer(modifier = Modifier.height(12.dp))
            InstructionItem(
                icon = Icons.Outlined.LocalPharmacy,
                text = "Store at room temperature away from moisture"
            )
        }
    }
}

@Composable
fun InstructionItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SecondaryContainerColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontFamily = jetbrainFamily,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun RecentHistorySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HistoryItem("Today, 8:05 AM", "Taken", Color(0xFF4CAF50))
        HistoryItem("Yesterday, 2:15 PM", "Taken", Color(0xFF4CAF50))
        HistoryItem("Yesterday, 8:00 PM", "Missed", Color(0xFFFF9800))

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                "View Full History",
                fontFamily = jetbrainFamily,
                color = SecondaryContainerColor
            )
        }
    }
}

@Composable
fun HistoryItem(time: String, status: String, statusColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = time,
                fontFamily = jetbrainFamily,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = statusColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = status,
                    fontFamily = jetbrainFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

