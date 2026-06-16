package com.vasant.pillpal.ui.screens

import android.app.AlertDialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.vasant.pillpal.data.db.MedicineEvent
import com.vasant.pillpal.ui.components.AddMedTop
import com.vasant.pillpal.ui.presentation.MedicineState
import com.vasant.pillpal.ui.presentation.MedicineType
import com.vasant.pillpal.ui.theme.rubikFamily
import com.vasant.pillpal.ui.viewmodel.MedicineViewModel
import com.vasant.pillpal.utils.ALARM_PERMISSION
import com.vasant.pillpal.utils.NOTIFICATION_PERMISSION
import com.vasant.pillpal.utils.getFormattedTime
import com.vasant.pillpal.utils.getTimeInMillis
import com.vasant.pillpal.utils.hasPermission
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddMedsScreen(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass,
    medicineViewModel: MedicineViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val exactAlarmIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Intent("android.settings.REQUEST_SCHEDULE_EXACT_ALARM").apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    } else null
    val notificationIntent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    ).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }

    Scaffold(
        topBar = { AddMedTop(navController) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = {
                            val needsExactAlarmPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasPermission(context, ALARM_PERMISSION)
                            if (needsExactAlarmPermission) {
                                AlertDialog.Builder(context).setTitle("Permission Required")
                                    .setMessage(
                                        "To ensure you receive timely medication reminders Kiri Reminder needs permission to set precise alarms. Please tap 'Go to Settings' and enable 'Allow setting alarms and reminders' for our app."
                                    ).setPositiveButton("Go to Settings") { _, _ ->
                                        exactAlarmIntent?.let { context.startActivity(it) }
                                    }.setNegativeButton("Cancel", null)
                                    .show()
                            } else if (!hasPermission(context, NOTIFICATION_PERMISSION)) {
                                AlertDialog.Builder(context).setTitle("Permission Required")
                                    .setMessage(
                                        "To ensure you receive timely medication reminders Kiri Reminder needs permission to set precise alarms. Please tap 'Go to Settings' and enable 'Allow Notification' for our app."
                                    ).setPositiveButton("Go to Settings") { _, _ ->
                                        context.startActivity(notificationIntent)
                                    }.setNegativeButton("Cancel", null)
                                    .show()
                            } else {
                                medicineViewModel.onEvent(MedicineEvent.SaveMedicine(context))
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "Save Medication",
                            fontFamily = rubikFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            AddMedsScreenPill(
                currentValue = medicineViewModel.state,
                title = "Medicine Name",
                Event = medicineViewModel::onEvent,
            )

            // Structured dosage input
            DosageInputRow(state = medicineViewModel.state, onEvent = medicineViewModel::onEvent)

            // Medicine type selector (wrap chips)
            MedicineTypeSelector(currentState = medicineViewModel.state, onEvent = medicineViewModel::onEvent)

            // Notes input (multiline)
            AddMedsScreenPill(
                title = "Notes",
                Event = medicineViewModel::onEvent,
                currentValue = medicineViewModel.state,
            )

            // Combined Date & Time Calendar Scheduler Pill
            AddDateTimePill(medicineViewModel)

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MedicineTypeSelector(
    currentState: MutableStateFlow<MedicineState>,
    onEvent: (MedicineEvent) -> Unit
) {
    val value = currentState.collectAsStateWithLifecycle()
    val selected = value.value.med_type ?: MedicineType.TABLET

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Medicine Type",
                fontFamily = rubikFamily,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MedicineType.entries.forEach { type ->
                    val isSelected = type == selected
                    val bg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = bg,
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier
                            .clickable { onEvent(MedicineEvent.MedicineTypeChanged(type)) }
                    ) {
                        Text(
                            text = type.name,
                            fontFamily = rubikFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosageInputRow(
    state: MutableStateFlow<MedicineState>,
    onEvent: (MedicineEvent) -> Unit
) {
    val value = state.collectAsStateWithLifecycle()
    val medicineType = value.value.med_type ?: MedicineType.TABLET

    val defaultUnit = when (medicineType) {
        MedicineType.TABLET, MedicineType.CAPSULE -> "tablet"
        MedicineType.SYRUP -> "ml"
        MedicineType.DROPS -> "drops"
        MedicineType.OTHERS -> "mg"
    }

    val availableUnits = when (medicineType) {
        MedicineType.TABLET, MedicineType.CAPSULE -> listOf("tablet")
        MedicineType.SYRUP -> listOf("ml")
        MedicineType.DROPS -> listOf("drops")
        MedicineType.OTHERS -> listOf("mg", "ml", "g")
    }

    val dosageText = value.value.dosage
    val parts = remember(dosageText, defaultUnit) {
        val tokens = dosageText.trim().split(" ").filter { it.isNotBlank() }
        val amount = tokens.firstOrNull { it.any(Char::isDigit) } ?: ""
        val unit = tokens.drop(1).firstOrNull()?.lowercase() ?: defaultUnit
        amount to unit
    }

    var amount by remember(parts) { mutableStateOf(parts.first) }
    var expanded by remember { mutableStateOf(false) }
    var unit by remember(parts, defaultUnit) {
        mutableStateOf(if (availableUnits.contains(parts.second)) parts.second else defaultUnit)
    }

    LaunchedEffect(medicineType) {
        unit = defaultUnit
        val cleanAmount = amount.filter { it.isDigit() || it == '.' }
        val composed = if (cleanAmount.isNotEmpty()) "$cleanAmount $unit" else ""
        onEvent(MedicineEvent.AddDosageChange(composed))
    }

    fun pushDosage() {
        val cleanAmount = amount.filter { it.isDigit() || it == '.' }
        val composed = if (cleanAmount.isNotEmpty()) "$cleanAmount $unit" else ""
        onEvent(MedicineEvent.AddDosageChange(composed))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Dosage",
                fontFamily = rubikFamily,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it.filter { ch -> ch.isDigit() || ch == '.' }
                        pushDosage()
                    },
                    modifier = Modifier.weight(1.5f),
                    label = { Text("Amount", fontFamily = rubikFamily) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                if (availableUnits.size > 1) {
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = unit,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unit", fontFamily = rubikFamily) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .weight(1f)
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            availableUnits.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt, fontFamily = rubikFamily) },
                                    onClick = {
                                        unit = opt
                                        expanded = false
                                        pushDosage()
                                    }
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit", fontFamily = rubikFamily) },
                        enabled = false,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color.LightGray.copy(alpha = 0.3f),
                            disabledLabelColor = Color.Gray
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDateTimePill(medicineViewModel: MedicineViewModel) {
    val state by medicineViewModel.state.collectAsStateWithLifecycle()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val calendar = remember(state.date) {
        val cal = Calendar.getInstance()
        if (state.date > 0) {
            cal.timeInMillis = state.date
        }
        cal
    }

    val dateString = remember(state.date) {
        if (state.date > 0) {
            SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(state.date))
        } else {
            "Select Date"
        }
    }

    val timeString = remember(state.date) {
        if (state.date > 0) {
            getFormattedTime(state.date)
        } else {
            "Select Time"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Reminder Schedule",
                fontFamily = rubikFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date Selector Pill
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 14.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = dateString,
                            fontFamily = rubikFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = if (state.date > 0) MaterialTheme.colorScheme.primary else Color.Gray,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text("📅", fontSize = 14.sp)
                    }
                }

                // Time Selector Pill
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable { showTimePicker = true }
                        .padding(horizontal = 14.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = timeString,
                            fontFamily = rubikFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = if (state.date > 0) MaterialTheme.colorScheme.primary else Color.Gray,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text("⏰", fontSize = 14.sp)
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = if (state.date > 0) state.date else System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedMillis ->
                        val selectedCal = Calendar.getInstance().apply { timeInMillis = selectedMillis }
                        calendar.set(Calendar.YEAR, selectedCal.get(Calendar.YEAR))
                        calendar.set(Calendar.MONTH, selectedCal.get(Calendar.MONTH))
                        calendar.set(Calendar.DAY_OF_MONTH, selectedCal.get(Calendar.DAY_OF_MONTH))
                        medicineViewModel.onEvent(MedicineEvent.DateChanged(calendar.timeInMillis))
                    }
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

    if (showTimePicker) {
        AddTimePickerDialog(
            onConfirm = { hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                medicineViewModel.onEvent(MedicineEvent.DateChanged(calendar.timeInMillis))
                showTimePicker = false
            },
            onDisMiss = { showTimePicker = false }
        )
    }
}

@Composable
fun AddMedsScreenPill(
    title: String,
    Event: (MedicineEvent) -> Unit,
    currentValue: MutableStateFlow<MedicineState>,
) {
    val value = currentValue.collectAsStateWithLifecycle()
    val isNotes = title == "Notes"
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontFamily = rubikFamily,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            OutlinedTextField(
                value = when (title) {
                    "Medicine Name" -> value.value.medicineName
                    "Notes" -> value.value.note ?: ""
                    else -> ""
                },
                onValueChange = {
                    when (title) {
                        "Medicine Name" -> Event(MedicineEvent.MedicineNameChanged(medicineName = it))
                        "Notes" -> Event(MedicineEvent.NoteChanged(note = it))
                    }
                },
                singleLine = !isNotes,
                minLines = if (isNotes) 3 else 1,
                placeholder = {
                    Text(
                        text = "Enter $title",
                        fontFamily = rubikFamily,
                        color = Color.Gray.copy(alpha = 0.6f)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTimePickerDialog(
    onConfirm: (hour: Int, minute: Int) -> Unit = { _, _ -> },
    onDisMiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()
    val pickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = false
    )
    Dialog(onDismissRequest = { onDisMiss() }) {
        Surface(shape = RoundedCornerShape(24.dp), tonalElevation = 10.dp) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(state = pickerState)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDisMiss() }) {
                        Text("Cancel", fontFamily = rubikFamily, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        onConfirm(
                            pickerState.hour, pickerState.minute
                        )
                    }) {
                        Text("OK", fontFamily = rubikFamily, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}