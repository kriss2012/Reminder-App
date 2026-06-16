package com.vasant.pillpal.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.vasant.pillpal.R
import com.vasant.pillpal.ui.theme.rubikFamily
import androidx.hilt.navigation.compose.hiltViewModel
import com.vasant.pillpal.ui.viewmodel.SettingsViewModel
import com.vasant.pillpal.ui.navigation.AuthenticationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val loggedOut by viewModel.loggedOut.collectAsState()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showSoundDialog by remember { mutableStateOf(false) }

    // Navigate to Login when logout finishes
    LaunchedEffect(loggedOut) {
        if (loggedOut) {
            navController.navigate(AuthenticationRoute.LoginScreen) {
                popUpTo(0)
                launchSingleTop = true
            }
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isTablet = screenWidth > 600.dp
    val maxWidth = if (isTablet) 800.dp else screenWidth

    // Advanced Ringtone Picker (RingtoneManager fallback)
    val soundPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val picked: Uri? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
        }
        if (picked != null) {
            viewModel.setSoundUri(picked)
        }
    }

    fun launchSystemRingtonePicker(currentUri: String?) {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select notification sound")
            if (currentUri != null) {
                putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(currentUri))
            }
        }
        soundPickerLauncher.launch(intent)
    }

    val systemSounds by produceState<List<PresetSound>>(
        initialValue = listOf(
            PresetSound("Default Notification", Settings.System.DEFAULT_NOTIFICATION_URI.toString()),
            PresetSound("Gentle Alarm", Settings.System.DEFAULT_ALARM_ALERT_URI.toString()),
            PresetSound("Default Ringtone", Settings.System.DEFAULT_RINGTONE_URI.toString()),
            PresetSound("Custom Chime (App Sound)", "android.resource://${context.packageName}/${R.raw.alarm}")
        )
    ) {
        val list = mutableListOf<PresetSound>()
        list.add(PresetSound("Custom Chime (App Sound)", "android.resource://${context.packageName}/${R.raw.alarm}"))
        list.add(PresetSound("Default Notification", Settings.System.DEFAULT_NOTIFICATION_URI.toString()))
        list.add(PresetSound("Gentle Alarm", Settings.System.DEFAULT_ALARM_ALERT_URI.toString()))
        list.add(PresetSound("Default Ringtone", Settings.System.DEFAULT_RINGTONE_URI.toString()))

        try {
            val ringtoneManager = RingtoneManager(context).apply {
                setType(RingtoneManager.TYPE_NOTIFICATION or RingtoneManager.TYPE_ALARM)
            }
            val cursor = ringtoneManager.cursor
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                    val uri = ringtoneManager.getRingtoneUri(cursor.position)
                    if (uri != null) {
                        val uriStr = uri.toString()
                        if (list.none { it.uri == uriStr }) {
                            list.add(PresetSound(title, uriStr))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        value = list
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontFamily = rubikFamily,
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .widthIn(max = maxWidth),
                contentPadding = PaddingValues(
                    vertical = if (isTablet) 20.dp else 16.dp,
                    horizontal = if (isTablet) 24.dp else 20.dp
                )
            ) {
                // Notifications Section
                item { SectionHeader("Notifications", isTablet) }

                item {
                    SettingSwitchCard(
                        icon = Icons.Outlined.Notifications,
                        title = "Push Notifications",
                        subtitle = "Receive medication reminders",
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                        isTablet = isTablet
                    )
                }

                item {
                    SettingSwitchCard(
                        icon = Icons.Outlined.MusicNote,
                        title = "Reminder Sound",
                        subtitle = "Play sound for reminders",
                        checked = uiState.soundEnabled,
                        onCheckedChange = { viewModel.setSoundEnabled(it) },
                        isTablet = isTablet
                    )
                }

                item {
                    SettingSwitchCard(
                        icon = Icons.Outlined.Vibration,
                        title = "Vibration",
                        subtitle = "Vibrate on reminders",
                        checked = uiState.vibration,
                        onCheckedChange = { viewModel.setVibration(it) },
                        isTablet = isTablet
                    )
                }

                item {
                    SettingClickCard(
                        icon = Icons.Outlined.VolumeUp,
                        title = "Reminder Ringtone",
                        subtitle = uiState.soundTitle,
                        onClick = { showSoundDialog = true },
                        isTablet = isTablet
                    )
                }

                // Appearance Section
                item {
                    Spacer(modifier = Modifier.height(if (isTablet) 28.dp else 24.dp))
                    SectionHeader("Appearance", isTablet)
                }

                item {
                    SettingClickCard(
                        icon = Icons.Outlined.Palette,
                        title = "App Theme",
                        subtitle = when (uiState.currentTheme.lowercase()) {
                            "minty" -> "Minty Fresh"
                            "lavender" -> "Lavender Fields"
                            "dark" -> "Charcoal Dark"
                            else -> "Whisker Peach"
                        },
                        onClick = { showThemeDialog = true },
                        isTablet = isTablet
                    )
                }

                // Account Section
                item {
                    Spacer(modifier = Modifier.height(if (isTablet) 28.dp else 24.dp))
                    SectionHeader("Account", isTablet)
                }

                item {
                    SettingClickCard(
                        icon = Icons.Outlined.Person,
                        title = "Edit Profile",
                        subtitle = "Update your personal information",
                        onClick = {},
                        isTablet = isTablet
                    )
                }

                item {
                    SettingClickCard(
                        icon = Icons.Outlined.Lock,
                        title = "Change Password",
                        subtitle = "Update your password",
                        onClick = {},
                        isTablet = isTablet
                    )
                }

                // Support Section
                item {
                    Spacer(modifier = Modifier.height(if (isTablet) 28.dp else 24.dp))
                    SectionHeader("Support", isTablet)
                }

                item {
                    SettingClickCard(
                        icon = Icons.AutoMirrored.Outlined.Help,
                        title = "Help & FAQs",
                        subtitle = "Get help using Kiri Reminder",
                        onClick = {},
                        isTablet = isTablet
                    )
                }

                item {
                    SettingClickCard(
                        icon = Icons.Outlined.Info,
                        title = "About",
                        subtitle = "Version 1.1.0",
                        onClick = {},
                        isTablet = isTablet
                    )
                }

                // Logout Section
                item { Spacer(modifier = Modifier.height(24.dp)) }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(if (isTablet) 20.dp else 16.dp))
                            .clickable { viewModel.logout() },
                        shape = RoundedCornerShape(if (isTablet) 20.dp else 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(if (isTablet) 20.dp else 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(if (isTablet) 14.dp else 12.dp),
                                color = Color(0xFFEF5350).copy(alpha = 0.2f),
                                modifier = Modifier.size(if (isTablet) 56.dp else 48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                                        contentDescription = null,
                                        tint = Color(0xFFEF5350),
                                        modifier = Modifier.size(if (isTablet) 28.dp else 24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(if (isTablet) 18.dp else 16.dp))

                            Text(
                                text = "Logout",
                                fontFamily = rubikFamily,
                                fontSize = if (isTablet) 18.sp else 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFEF5350)
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }

    // Theme Picker Dialog
    if (showThemeDialog) {
        Dialog(onDismissRequest = { showThemeDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Choose Theme",
                        fontFamily = rubikFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    ThemeOptionItem(
                        themeId = "whisker",
                        themeName = "Whisker Peach",
                        colorIndicator = Color(0xFFF28B6A),
                        isSelected = uiState.currentTheme == "whisker",
                        onClick = {
                            viewModel.setTheme("whisker")
                            showThemeDialog = false
                        }
                    )

                    ThemeOptionItem(
                        themeId = "minty",
                        themeName = "Minty Fresh",
                        colorIndicator = Color(0xFF4DB6AC),
                        isSelected = uiState.currentTheme == "minty",
                        onClick = {
                            viewModel.setTheme("minty")
                            showThemeDialog = false
                        }
                    )

                    ThemeOptionItem(
                        themeId = "lavender",
                        themeName = "Lavender Fields",
                        colorIndicator = Color(0xFF9E8BF2),
                        isSelected = uiState.currentTheme == "lavender",
                        onClick = {
                            viewModel.setTheme("lavender")
                            showThemeDialog = false
                        }
                    )

                    ThemeOptionItem(
                        themeId = "dark",
                        themeName = "Charcoal Dark",
                        colorIndicator = Color(0xFF2C2520),
                        isSelected = uiState.currentTheme == "dark",
                        onClick = {
                            viewModel.setTheme("dark")
                            showThemeDialog = false
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("Cancel", fontFamily = rubikFamily)
                    }
                }
            }
        }
    }

    // Sound Picker Dialog
    if (showSoundDialog) {
        Dialog(onDismissRequest = { showSoundDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Reminder Sounds",
                        fontFamily = rubikFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Scrollable container for ringtones
                    Box(modifier = Modifier.weight(1f, fill = false).heightIn(max = 280.dp)) {
                        val scrollState = rememberScrollState()
                        Column(modifier = Modifier.verticalScroll(scrollState)) {
                            systemSounds.forEach { sound ->
                                val isSelected = uiState.soundUri == sound.uri
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            viewModel.setSoundUri(Uri.parse(sound.uri))
                                        }
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { viewModel.setSoundUri(Uri.parse(sound.uri)) }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = sound.name,
                                            fontFamily = rubikFamily,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    IconButton(onClick = { viewModel.playSoundPreview(sound.uri) }) {
                                        Icon(
                                            imageVector = Icons.Outlined.PlayArrow,
                                            contentDescription = "Preview Sound",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    // System File Picker fallback button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                showSoundDialog = false
                                launchSystemRingtonePicker(uiState.soundUri)
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "More Ringtones...",
                            fontFamily = rubikFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showSoundDialog = false }) {
                            Text("Done", fontFamily = rubikFamily, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeOptionItem(
    themeId: String,
    themeName: String,
    colorIndicator: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(colorIndicator)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = themeName,
                fontFamily = rubikFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

data class PresetSound(
    val name: String,
    val uri: String
)

@Composable
fun SectionHeader(title: String, isTablet: Boolean) {
    Text(
        text = title,
        fontFamily = rubikFamily,
        fontSize = if (isTablet) 16.sp else 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
    )
}

@Composable
fun SettingSwitchCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isTablet: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (isTablet) 20.dp else 16.dp)),
        shape = RoundedCornerShape(if (isTablet) 20.dp else 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isTablet) 20.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(if (isTablet) 14.dp else 12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(if (isTablet) 56.dp else 48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(if (isTablet) 28.dp else 24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(if (isTablet) 18.dp else 16.dp))
                Column {
                    Text(
                        text = title,
                        fontFamily = rubikFamily,
                        fontSize = if (isTablet) 18.sp else 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        fontFamily = rubikFamily,
                        fontSize = if (isTablet) 14.sp else 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Composable
fun SettingClickCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isTablet: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (isTablet) 20.dp else 16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(if (isTablet) 20.dp else 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isTablet) 20.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(if (isTablet) 14.dp else 12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(if (isTablet) 56.dp else 48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(if (isTablet) 28.dp else 24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(if (isTablet) 18.dp else 16.dp))
                Column {
                    Text(
                        text = title,
                        fontFamily = rubikFamily,
                        fontSize = if (isTablet) 18.sp else 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        fontFamily = rubikFamily,
                        fontSize = if (isTablet) 14.sp else 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}
