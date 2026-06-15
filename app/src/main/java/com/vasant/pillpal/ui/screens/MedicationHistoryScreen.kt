package com.vasant.pillpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.vasant.pillpal.ui.theme.SecondaryContainerColor
import com.vasant.pillpal.ui.theme.jetbrainFamily
import java.text.SimpleDateFormat
import java.util.*

data class MedicationHistory(
    val id: Int,
    val medicineName: String,
    val dosage: String,
    val scheduledTime: String,
    val takenTime: String?,
    val status: HistoryStatus,
    val date: String,
    val notes: String = ""
)

enum class HistoryStatus {
    TAKEN, MISSED, SKIPPED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationHistoryScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Taken", "Missed", "Skipped")

    // Sample history data
    val allHistory = remember {
        listOf(
            MedicationHistory(
                1,
                "Aspirin",
                "100mg",
                "8:00 AM",
                "8:05 AM",
                HistoryStatus.TAKEN,
                "Today",
                "Taken with breakfast"
            ),
            MedicationHistory(
                2,
                "Vitamin D",
                "1000 IU",
                "9:00 AM",
                "9:15 AM",
                HistoryStatus.TAKEN,
                "Today"
            ),
            MedicationHistory(
                3,
                "Metformin",
                "500mg",
                "2:00 PM",
                null,
                HistoryStatus.MISSED,
                "Today"
            ),
            MedicationHistory(
                4,
                "Lisinopril",
                "10mg",
                "8:00 PM",
                null,
                HistoryStatus.SKIPPED,
                "Today",
                "Doctor's advice"
            ),
            MedicationHistory(
                5,
                "Aspirin",
                "100mg",
                "8:00 AM",
                "8:10 AM",
                HistoryStatus.TAKEN,
                "Yesterday"
            ),
            MedicationHistory(
                6,
                "Vitamin D",
                "1000 IU",
                "9:00 AM",
                null,
                HistoryStatus.MISSED,
                "Yesterday"
            ),
            MedicationHistory(
                7,
                "Metformin",
                "500mg",
                "2:00 PM",
                "2:20 PM",
                HistoryStatus.TAKEN,
                "Yesterday"
            )
        )
    }

    val filteredHistory = when (tabs[selectedTab]) {
        "All" -> allHistory
        "Taken" -> allHistory.filter { it.status == HistoryStatus.TAKEN }
        "Missed" -> allHistory.filter { it.status == HistoryStatus.MISSED }
        "Skipped" -> allHistory.filter { it.status == HistoryStatus.SKIPPED }
        else -> allHistory
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Medication History",
                        fontFamily = jetbrainFamily,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            // Statistics Summary
            HistoryStatsSection(allHistory)

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                containerColor = Color.Transparent,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(

                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = SecondaryContainerColor
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontFamily = jetbrainFamily,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = SecondaryContainerColor,
                        unselectedContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // History List
            if (filteredHistory.isEmpty()) {
                EmptyHistoryState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Group by date
                    val groupedHistory = filteredHistory.groupBy { it.date }
                    groupedHistory.forEach { (date, histories) ->
                        item {
                            Text(
                                text = date,
                                fontFamily = jetbrainFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }

                        items(histories) { history ->
                            HistoryCard(history = history)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryStatsSection(history: List<MedicationHistory>) {
    val takenCount = history.count { it.status == HistoryStatus.TAKEN }
    val missedCount = history.count { it.status == HistoryStatus.MISSED }
    val skippedCount = history.count { it.status == HistoryStatus.SKIPPED }
    val adherenceRate = if (history.isNotEmpty()) {
        (takenCount.toFloat() / history.size * 100).toInt()
    } else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SecondaryContainerColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = "$adherenceRate%",
                label = "Adherence",
                color = Color(0xFF4CAF50)
            )
            VerticalDivider(
                modifier = Modifier.height(50.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            StatItem(
                value = "$takenCount",
                label = "Taken",
                color = Color(0xFF4CAF50)
            )
            VerticalDivider(
                modifier = Modifier.height(50.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            StatItem(
                value = "$missedCount",
                label = "Missed",
                color = Color(0xFFFF9800)
            )
            VerticalDivider(
                modifier = Modifier.height(50.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            StatItem(
                value = "$skippedCount",
                label = "Skipped",
                color = Color(0xFF9E9E9E)
            )
        }
    }
}

@Composable
fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = jetbrainFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontFamily = jetbrainFamily,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun HistoryCard(history: MedicationHistory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon
            Surface(
                shape = CircleShape,
                color = getStatusColor(history.status).copy(alpha = 0.15f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = getStatusIcon(history.status),
                        contentDescription = null,
                        tint = getStatusColor(history.status),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = history.medicineName,
                    fontFamily = jetbrainFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = history.dosage,
                        fontFamily = jetbrainFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = " • ",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Scheduled: ${history.scheduledTime}",
                        fontFamily = jetbrainFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                if (history.takenTime != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Taken at: ${history.takenTime}",
                        fontFamily = jetbrainFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF4CAF50)
                    )
                }

                if (history.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Note: ${history.notes}",
                        fontFamily = jetbrainFamily,
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = getStatusColor(history.status).copy(alpha = 0.15f)
            ) {
                Text(
                    text = history.status.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontFamily = jetbrainFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = getStatusColor(history.status),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyHistoryState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = SecondaryContainerColor.copy(alpha = 0.15f),
                modifier = Modifier.size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        tint = SecondaryContainerColor,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No History Found",
                fontFamily = jetbrainFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your medication history will appear here",
                fontFamily = jetbrainFamily,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

fun getStatusIcon(status: HistoryStatus): androidx.compose.ui.graphics.vector.ImageVector {
    return when (status) {
        HistoryStatus.TAKEN -> Icons.Filled.CheckCircle
        HistoryStatus.MISSED -> Icons.Outlined.Cancel
        HistoryStatus.SKIPPED -> Icons.Outlined.NotInterested
    }
}

fun getStatusColor(status: HistoryStatus): Color {
    return when (status) {
        HistoryStatus.TAKEN -> Color(0xFF4CAF50)
        HistoryStatus.MISSED -> Color(0xFFFF9800)
        HistoryStatus.SKIPPED -> Color(0xFF9E9E9E)
    }
}

