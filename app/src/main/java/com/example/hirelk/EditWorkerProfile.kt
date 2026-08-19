package com.example.hirelk

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// Color Palette
// ==========================================
private val EditBgColor = Color(0xFFF8FAFC)
private val CardWhite = Color(0xFFFFFFFF)
private val PrimaryGreen = Color(0xFF1B5E20)
private val GreenText = Color(0xFF2E7D32)
private val ActiveChipBg = Color(0xFFE8F5E9)
private val ChipBorderGreen = Color(0xFF4CAF50)
private val InactiveChipBg = Color(0xFFF1F5F9)
private val TextDark = Color(0xFF1E293B)
private val LabelGray = Color(0xFF64748B)
private val InputBorder = Color(0xFFE2E8F0)
private val AvatarBlueBg = Color(0xFFDBEAFE)
private val AvatarBlueText = Color(0xFF1D4ED8)
private val DeleteRedBg = Color(0xFFFEE2E2)
private val DeleteRedText = Color(0xFFDC2626)

// ==========================================
// Vector Icons
// ==========================================
private val IconChevronLeft: ImageVector
    get() = ImageVector.Builder(
        name = "ChevronLeft", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(stroke = SolidColor(TextDark), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(15f, 18f); lineTo(9f, 12f); lineTo(15f, 6f)
        }
    }.build()

private val IconChevronDown: ImageVector
    get() = ImageVector.Builder(
        name = "ChevronDown", defaultWidth = 20.dp, defaultHeight = 20.dp, viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(stroke = SolidColor(LabelGray), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(6f, 9f); lineTo(12f, 15f); lineTo(18f, 9f)
        }
    }.build()

private val IconClock: ImageVector
    get() = ImageVector.Builder(
        name = "Clock", defaultWidth = 20.dp, defaultHeight = 20.dp, viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(stroke = SolidColor(LabelGray), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 22f); curveTo(17.5228f, 22f, 22f, 17.5228f, 22f, 12f); curveTo(22f, 6.47715f, 17.5228f, 2f, 12f, 2f); curveTo(6.47715f, 2f, 2f, 6.47715f, 2f, 12f); curveTo(2f, 17.5228f, 6.47715f, 22f, 12f, 22f); close()
            moveTo(12f, 6f); verticalLineTo(12f); lineTo(16f, 14f)
        }
    }.build()

private val IconTrash: ImageVector
    get() = ImageVector.Builder(
        name = "Trash", defaultWidth = 20.dp, defaultHeight = 20.dp, viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(stroke = SolidColor(DeleteRedText), strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(3f, 6f); horizontalLineTo(21f)
            moveTo(19f, 6f); verticalLineTo(20f); curveTo(19f, 21.1f, 18.1f, 22f, 17f, 22f); horizontalLineTo(7f); curveTo(5.9f, 22f, 5f, 21.1f, 5f, 20f); verticalLineTo(6f)
            moveTo(8f, 6f); verticalLineTo(4f); curveTo(8f, 2.9f, 8.9f, 2f, 10f, 2f); horizontalLineTo(14f); curveTo(15.1f, 2f, 16f, 2.9f, 16f, 4f); verticalLineTo(6f)
        }
    }.build()

// ==========================================
// Main Edit Profile Screen (with onBack)
// ==========================================
@Composable
fun EditProfileScreen(
    onBack: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf(TextFieldValue("Sunil Fernando")) }
    var mobileNumber by remember { mutableStateOf(TextFieldValue("+94 71 456 7890")) }
    var nicNumber by remember { mutableStateOf(TextFieldValue("912345678V")) }
    var bio by remember { mutableStateOf(TextFieldValue("10+ years in domestic electrical work, grid management, short circuit diagnosis.")) }
    var serviceCategory by remember { mutableStateOf("Electrician") }
    var workingLocation by remember { mutableStateOf(TextFieldValue("Maharagama, Nugegoda")) }
    var district by remember { mutableStateOf("Colombo") }
    var workingHoursFrom by remember { mutableStateOf(TextFieldValue("08:00 AM")) }
    var workingHoursTo by remember { mutableStateOf(TextFieldValue("06:00 PM")) }
    var rate by remember { mutableStateOf(TextFieldValue("1800")) }
    var rateType by remember { mutableStateOf("Per Hour") }
    var transport by remember { mutableStateOf("Transport included") }

    val selectedDays = remember { mutableStateListOf("Mon", "Tue", "Wed", "Thu", "Fri") }
    val daysList = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EditBgColor)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardWhite)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onBack() }, // FIXED: Calls onBack
                shape = CircleShape,
                color = EditBgColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = IconChevronLeft, contentDescription = "Back", modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Edit Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }

        Divider(color = InputBorder, thickness = 0.8.dp)

        // Main Content Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar & Change Photo
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = AvatarBlueBg
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "SF", fontWeight = FontWeight.Bold, color = AvatarBlueText, fontSize = 22.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Change Photo",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenText,
                    modifier = Modifier.clickable { }
                )
            }

            // Input Fields
            FormInputField(label = "Full Name", value = fullName, onValueChange = { fullName = it })
            FormInputField(label = "Mobile Number", value = mobileNumber, onValueChange = { mobileNumber = it })
            FormInputField(label = "NIC Number", value = nicNumber, onValueChange = { nicNumber = it })
            FormInputField(label = "Bio", value = bio, onValueChange = { bio = it }, singleLine = false, minLines = 3)

            FormDropdownField(label = "Service Category", value = serviceCategory)
            FormInputField(label = "Working Location", value = workingLocation, onValueChange = { workingLocation = it })
            FormDropdownField(label = "District", value = district)

            // Working Days Chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Working Days", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = LabelGray)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    daysList.forEach { day ->
                        val isSelected = selectedDays.contains(day)
                        DayChip(
                            day = day,
                            isSelected = isSelected,
                            onToggle = {
                                if (isSelected) selectedDays.remove(day) else selectedDays.add(day)
                            }
                        )
                    }
                }
            }

            // Working Hours
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Working Hours", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = LabelGray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimeInputField(value = workingHoursFrom, onValueChange = { workingHoursFrom = it }, modifier = Modifier.weight(1f))
                    Text(text = "to", fontSize = 13.sp, color = LabelGray)
                    TimeInputField(value = workingHoursTo, onValueChange = { workingHoursTo = it }, modifier = Modifier.weight(1f))
                }
            }

            // Rate Field
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Rate", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = LabelGray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FormInputField(
                        value = rate,
                        onValueChange = { rate = it },
                        modifier = Modifier.weight(1.3f)
                    )
                    FormDropdownField(
                        value = rateType,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Transport Field
            FormDropdownField(label = "Transport", value = transport)

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Button(
                onClick = { /* Save logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text(text = "Save Changes", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Button(
                onClick = { /* Delete logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeleteRedBg)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = IconTrash, contentDescription = null, tint = DeleteRedText, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Delete Account", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DeleteRedText)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==========================================
// Custom UI Component Helpers
// ==========================================
@Composable
private fun FormInputField(
    label: String? = null,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (label != null) {
            Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = LabelGray)
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = CardWhite,
            border = BorderStroke(1.dp, InputBorder)
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                minLines = minLines,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CardWhite,
                    unfocusedContainerColor = CardWhite,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Composable
private fun FormDropdownField(
    label: String? = null,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (label != null) {
            Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = LabelGray)
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { },
            shape = RoundedCornerShape(12.dp),
            color = CardWhite,
            border = BorderStroke(1.dp, InputBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                Icon(imageVector = IconChevronDown, contentDescription = null, tint = LabelGray)
            }
        }
    }
}

@Composable
private fun TimeInputField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, InputBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CardWhite,
                    unfocusedContainerColor = CardWhite,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium)
            )
            Icon(imageVector = IconClock, contentDescription = null, tint = LabelGray, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
        }
    }
}

@Composable
private fun DayChip(
    day: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onToggle() }
            .then(
                if (isSelected) {
                    Modifier.border(1.dp, ChipBorderGreen, RoundedCornerShape(20.dp))
                } else {
                    Modifier.border(1.dp, InputBorder, RoundedCornerShape(20.dp))
                }
            ),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) ActiveChipBg else InactiveChipBg
    ) {
        Text(
            text = day,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) GreenText else LabelGray,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}