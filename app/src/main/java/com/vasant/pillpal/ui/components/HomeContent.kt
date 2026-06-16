package com.vasant.pillpal.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.vasant.pillpal.R
import com.vasant.pillpal.data.db.Medicine
import com.vasant.pillpal.data.db.MedicineEvent
import com.vasant.pillpal.ui.navigation.MainUiRoute
import com.vasant.pillpal.ui.presentation.MedicineType
import com.vasant.pillpal.ui.theme.rubikFamily
import com.vasant.pillpal.ui.theme.AccentMintGreen
import com.vasant.pillpal.ui.theme.AccentBlue
import com.vasant.pillpal.ui.theme.AccentPink
import com.vasant.pillpal.ui.theme.AccentYellow
import com.vasant.pillpal.ui.viewmodel.MedicineViewModel
import com.vasant.pillpal.utils.getFormattedTime
import com.vasant.pillpal.utils.Prefs
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.OptIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    navController: NavHostController,
    padding: PaddingValues,
    medicineViewModel: MedicineViewModel = hiltViewModel()
) {
    val medicine = medicineViewModel.meds.collectAsStateWithLifecycle()
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }

    val calendarDays = remember(selectedDate) {
        val list = mutableListOf<Long>()
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
        cal.add(Calendar.DAY_OF_YEAR, -3) // Start 3 days before selectedDate
        for (i in 0 until 10) {
            list.add(cal.timeInMillis)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    val filteredMedicines = remember(medicine.value, selectedDate) {
        medicine.value.filter { isSameDay(it.time, selectedDate) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header Section
            item {
                GreetingSection()
            }

            // Quick Stats Section (reflects the selected date stats)
            item {
                QuickStatsSection(filteredMedicines)
            }

            // Calendar Scheduling strip
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CalendarStrip(
                            days = calendarDays,
                            selectedDate = selectedDate,
                            medicines = medicine.value,
                            onDateSelected = { selectedDate = it }
                        )
                    }
                    var showDatePicker by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = { showDatePicker = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { selectedDate = it }
                                    showDatePicker = false
                                }) {
                                    Text("OK", fontFamily = rubikFamily, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Cancel", fontFamily = rubikFamily, color = Color.Gray)
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                }
            }

            // Medicine List Section Header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isSameDay(selectedDate, System.currentTimeMillis())) "Today's Medications" else "Schedule for ${getFormattedDateString(selectedDate)}",
                            fontFamily = rubikFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()).format(Date(selectedDate)),
                            fontFamily = rubikFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }

                    if (filteredMedicines.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${filteredMedicines.size} meds",
                                fontFamily = rubikFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Medicine List or Empty State
            if (filteredMedicines.isEmpty()) {
                item {
                    EmptyStateSection(navController)
                }
            } else {
                items(filteredMedicines, key = { meds -> meds.id }) { data ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            when (it) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    medicineViewModel.onEvent(MedicineEvent.DeleteMedicine(data))
                                    true
                                }
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    medicineViewModel.onEvent(MedicineEvent.PendingMedicine(data))
                                    false
                                }
                                else -> false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            SwipeDismissBackground(dismissState)
                        }
                    ) {
                        MedicinePillSection(
                            date = getFormattedTime(data.time),
                            medicineName = data.medName,
                            dosage = data.dosage,
                            isCompleted = data.isCompleted,
                            medType = data.medType,
                            notes = data.note ?: ""
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun GreetingSection() {
    val context = LocalContext.current
    val userName = remember { Prefs.getName(context) ?: Prefs.getEmail(context)?.substringBefore("@") ?: "User" }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${getGreeting()}, $userName",
                            fontFamily = rubikFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Stay Healthy! 💊",
                            fontFamily = rubikFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Track your medications easily",
                            fontFamily = rubikFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    // Notification Icon
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickStatsSection(medicines: List<Medicine>) {
    val completedCount = medicines.count { it.isCompleted }
    val pendingCount = medicines.size - completedCount
    val completionRate = if (medicines.isNotEmpty()) {
        (completedCount.toFloat() / medicines.size * 100).toInt()
    } else 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Completed Card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = AccentMintGreen.copy(alpha = 0.15f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF1D5A4E),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$completedCount",
                    fontFamily = rubikFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D5A4E)
                )
                Text(
                    text = "Completed",
                    fontFamily = rubikFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D5A4E).copy(alpha = 0.8f)
                )
            }
        }

        // Pending Card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.framemedicine),
                    contentDescription = null,
                    tint = Color(0xFF6E3623),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$pendingCount",
                    fontFamily = rubikFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6E3623)
                )
                Text(
                    text = "Pending",
                    fontFamily = rubikFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6E3623).copy(alpha = 0.8f)
                )
            }
        }

        // Completion Rate Card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = AccentBlue.copy(alpha = 0.15f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.framemedicine),
                    contentDescription = null,
                    tint = Color(0xFF1D3E7F),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$completionRate%",
                    fontFamily = rubikFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D3E7F)
                )
                Text(
                    text = "Progress",
                    fontFamily = rubikFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D3E7F).copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun CalendarStrip(
    days: List<Long>,
    selectedDate: Long,
    medicines: List<Medicine>,
    onDateSelected: (Long) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(days) { dayMillis ->
            val isSelected = isSameDay(dayMillis, selectedDate)
            val cal = Calendar.getInstance().apply { timeInMillis = dayMillis }
            val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(cal.time) // "Mon", "Tue"
            val dayNumber = cal.get(Calendar.DAY_OF_MONTH).toString() // "15"
            val hasMeds = medicines.any { isSameDay(it.time, dayMillis) }

            val bg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            val textCol = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            val subTextCol = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.2f)

            Card(
                modifier = Modifier
                    .width(62.dp)
                    .clickable { onDateSelected(dayMillis) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = bg),
                border = BorderStroke(1.dp, borderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = dayName,
                        fontFamily = rubikFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = subTextCol
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dayNumber,
                        fontFamily = rubikFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textCol
                    )
                    if (hasMeds) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateSection(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon Container
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier.size(90.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.medicine),
                            contentDescription = null,
                            modifier = Modifier.size(45.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "No Medications Yet",
                    fontFamily = rubikFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Start your health journey by adding your first medication. We'll help you stay on track!",
                    fontFamily = rubikFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = { navController.navigate(MainUiRoute.AddMedicineScreen) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Add Your First Medication",
                        fontFamily = rubikFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SwipeDismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.targetValue) {
        SwipeToDismissBoxValue.EndToStart -> Color(0xFFEF5350)
        SwipeToDismissBoxValue.StartToEnd -> AccentMintGreen
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color),
        contentAlignment = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
            Alignment.CenterEnd
        } else {
            Alignment.CenterStart
        }
    ) {
        Icon(
            imageVector = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                Icons.Default.Delete
            } else {
                Icons.Filled.CheckCircle
            },
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .size(32.dp)
        )
    }
}

@Composable
fun MedicinePillSection(
    date: String,
    medicineName: String,
    dosage: String,
    isCompleted: Boolean = false,
    medType: MedicineType? = null,
    notes: String = ""
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = if (isCompleted) 1.dp else 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = if (isCompleted) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                Color(0xFFF1F8F4)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Medicine Icon with Type-based design
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isCompleted) {
                    AccentMintGreen.copy(alpha = 0.15f)
                } else {
                    getMedicineTypeColor(medType).copy(alpha = 0.12f)
                },
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(getMedicineTypeIcon(medType)),
                        contentDescription = "Medicine Icon",
                        modifier = Modifier.size(30.dp),
                        tint = if (isCompleted) {
                            Color(0xFF1D5A4E)
                        } else {
                            getMedicineTypeColor(medType)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Medicine Details
            Column(modifier = Modifier.weight(1f)) {
                // Medicine Name
                Text(
                    text = medicineName,
                    fontFamily = rubikFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = if (isCompleted) {
                        Color(0xFF2C2C2C).copy(alpha = 0.6f)
                    } else {
                        Color(0xFF2C2520)
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Time and Dosage Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Time
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCompleted) {
                            AccentMintGreen.copy(alpha = 0.12f)
                        } else {
                            Color(0xFFFDFDFD)
                        },
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.framemedicine),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isCompleted) Color(0xFF1D5A4E) else Color(0xFF888888)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = date,
                                fontFamily = rubikFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isCompleted) Color(0xFF1D5A4E) else Color(0xFF555555)
                            )
                        }
                    }

                    // Dosage
                    if (dosage.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = getMedicineTypeColor(medType).copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = dosage,
                                fontFamily = rubikFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = getMedicineTypeColor(medType),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Medicine Type and Notes
                if (medType != null || notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        medType?.let {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = getMedicineTypeColor(it).copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = it.displayName,
                                    fontFamily = rubikFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = getMedicineTypeColor(it),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        if (notes.isNotBlank()) {
                            Text(
                                text = "• $notes",
                                fontFamily = rubikFamily,
                                fontSize = 11.sp,
                                color = Color(0xFF8C827A),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Status Indicator
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isCompleted) {
                    AccentMintGreen
                } else {
                    MaterialTheme.colorScheme.primary
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isCompleted) "Done" else "Pending",
                        fontFamily = rubikFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Helper Functions
private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
}

private fun getFormattedDateString(time: Long): String {
    return SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(time))
}

private fun isSameDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@Composable
private fun getMedicineTypeColor(type: MedicineType?): Color {
    return when (type) {
        MedicineType.TABLET -> MaterialTheme.colorScheme.primary
        MedicineType.CAPSULE -> AccentPink
        MedicineType.SYRUP -> AccentBlue
        MedicineType.DROPS -> AccentMintGreen
        MedicineType.OTHERS -> AccentYellow
        null -> MaterialTheme.colorScheme.primary
    }
}

private fun getMedicineTypeIcon(type: MedicineType?): Int {
    return when (type) {
        MedicineType.TABLET, MedicineType.CAPSULE -> R.drawable.framemedicine
        MedicineType.SYRUP, MedicineType.DROPS -> R.drawable.medicine
        MedicineType.OTHERS -> R.drawable.framemedicine
        null -> R.drawable.framemedicine
    }
}