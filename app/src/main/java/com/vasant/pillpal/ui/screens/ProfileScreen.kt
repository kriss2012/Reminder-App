package com.vasant.pillpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.vasant.pillpal.utils.Prefs
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(Prefs.getName(context) ?: "John Doe") }
    val email = remember { Prefs.getEmail(context) ?: "john.doe@email.com" }
    var phone by remember { mutableStateOf(Prefs.getPhone(context) ?: "+1 234 567 8900") }
    var dob by remember { mutableStateOf(Prefs.getDob(context) ?: "January 15, 1990") }
    var gender by remember { mutableStateOf(Prefs.getGender(context) ?: "Male") }
    var location by remember { mutableStateOf(Prefs.getLocation(context) ?: "New York, USA") }

    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        var tempName by remember { mutableStateOf(name) }
        var tempPhone by remember { mutableStateOf(phone) }
        var tempDob by remember { mutableStateOf(dob) }
        var tempGender by remember { mutableStateOf(gender) }
        var tempLocation by remember { mutableStateOf(location) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "Edit Profile",
                    fontFamily = jetbrainFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Full Name", fontFamily = jetbrainFamily) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { tempPhone = it },
                        label = { Text("Phone Number", fontFamily = jetbrainFamily) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempDob,
                        onValueChange = { tempDob = it },
                        label = { Text("Date of Birth", fontFamily = jetbrainFamily) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempGender,
                        onValueChange = { tempGender = it },
                        label = { Text("Gender", fontFamily = jetbrainFamily) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempLocation,
                        onValueChange = { tempLocation = it },
                        label = { Text("Location", fontFamily = jetbrainFamily) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Prefs.setName(context, tempName)
                        Prefs.setPhone(context, tempPhone)
                        Prefs.setDob(context, tempDob)
                        Prefs.setGender(context, tempGender)
                        Prefs.setLocation(context, tempLocation)

                        name = tempName
                        phone = tempPhone
                        dob = tempDob
                        gender = tempGender
                        location = tempLocation

                        showEditDialog = false
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryContainerColor)
                ) {
                    Text("Save", fontFamily = jetbrainFamily, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", fontFamily = jetbrainFamily, color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Profile Header
            item {
                ProfileHeaderSection(name = name, email = email, onEditClick = { showEditDialog = true })
            }

            // Health Statistics
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Health Statistics",
                    fontFamily = jetbrainFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                HealthStatsSection()
            }

            // Personal Information
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Personal Information",
                    fontFamily = jetbrainFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                PersonalInfoSection(
                    name = name,
                    email = email,
                    phone = phone,
                    dob = dob,
                    gender = gender,
                    location = location
                )
            }
        }
    }
}

@Composable
fun ProfileHeaderSection(
    name: String,
    email: String,
    onEditClick: () -> Unit
) {
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture
                Box {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(100.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.user),
                                contentDescription = "Profile Picture",
                                tint = SecondaryContainerColor,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }

                    // Edit Button
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = SecondaryContainerColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = name,
                    fontFamily = jetbrainFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = email,
                    fontFamily = jetbrainFamily,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickStatItem("12", "Medications")
                    HorizontalDivider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp),
                        color = Color.White.copy(alpha = 0.3f)
                    )
                    QuickStatItem("85%", "Adherence")
                    HorizontalDivider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp),
                        color = Color.White.copy(alpha = 0.3f)
                    )
                    QuickStatItem("45", "Days Active")
                }
            }
        }
    }
}

@Composable
fun QuickStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = jetbrainFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontFamily = jetbrainFamily,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun HealthStatsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HealthStatCard(
            icon = Icons.Outlined.Favorite,
            title = "Blood Pressure",
            value = "120/80 mmHg",
            color = Color(0xFFE91E63)
        )
        HealthStatCard(
            icon = Icons.Outlined.FavoriteBorder,
            title = "Heart Rate",
            value = "72 bpm",
            color = Color(0xFFF44336)
        )
        HealthStatCard(
            icon = Icons.Outlined.LocalFireDepartment,
            title = "Temperature",
            value = "98.6°F",
            color = Color(0xFFFF9800)
        )
        HealthStatCard(
            icon = Icons.Outlined.MonitorWeight,
            title = "Weight",
            value = "70 kg",
            color = Color(0xFF4CAF50)
        )
    }
}

@Composable
fun HealthStatCard(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color
) {
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
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = jetbrainFamily,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = value,
                    fontFamily = jetbrainFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun PersonalInfoSection(
    name: String,
    email: String,
    phone: String,
    dob: String,
    gender: String,
    location: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PersonalInfoCard(
            icon = Icons.Outlined.Person,
            label = "Full Name",
            value = name
        )
        PersonalInfoCard(
            icon = Icons.Outlined.Email,
            label = "Email",
            value = email
        )
        PersonalInfoCard(
            icon = Icons.Outlined.Phone,
            label = "Phone",
            value = phone
        )
        PersonalInfoCard(
            icon = Icons.Outlined.CalendarToday,
            label = "Date of Birth",
            value = dob
        )
        PersonalInfoCard(
            icon = Icons.Outlined.Person,
            label = "Gender",
            value = gender
        )
        PersonalInfoCard(
            icon = Icons.Outlined.LocationOn,
            label = "Location",
            value = location
        )
    }
}

@Composable
fun PersonalInfoCard(
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontFamily = jetbrainFamily,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
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
