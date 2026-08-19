package com.example.hirelk

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerRegisterScreen(
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: () -> Unit,
    onSwitchToRoleSelect: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    // Approval status tracking from Firestore: "none", "pending", "approved", "rejected"
    var approvalStatus by remember { mutableStateOf<String?>(null) }
    var isCheckingStatus by remember { mutableStateOf(true) }

    // Real-time listener to check approval status so it auto-refreshes (WhatsApp style)
    DisposableEffect(userId) {
        if (userId != null) {
            val docRef = db.collection("users").document(userId)
            val listener = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    isCheckingStatus = false
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val status = snapshot.getString("approvalStatus") ?: "none"
                    approvalStatus = status
                } else {
                    approvalStatus = "none"
                }
                isCheckingStatus = false
            }
            onDispose {
                listener.remove()
            }
        } else {
            isCheckingStatus = false
            // FIX: DisposableEffect requires returning a DisposableEffectResult (which onDispose provides)
            // Using a dummy onDispose block when userId is null
            onDispose {}
        }
    }

    // Form Field States
    var fullName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var nicNumber by remember { mutableStateOf("") }

    // Profile Image States
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // NIC Front & Back Image States
    var nicFrontUri by remember { mutableStateOf<Uri?>(null) }
    var nicFrontBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var nicBackUri by remember { mutableStateOf<Uri?>(null) }
    var nicBackBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var activeImageTarget by remember { mutableStateOf("profile") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = try {
                if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
            } catch (e: Exception) {
                null
            }

            when (activeImageTarget) {
                "profile" -> { profileImageUri = it; profileBitmap = bitmap }
                "nic_front" -> { nicFrontUri = it; nicFrontBitmap = bitmap }
                "nic_back" -> { nicBackUri = it; nicBackBitmap = bitmap }
            }
        }
    }

    var serviceCategory by remember { mutableStateOf("Electrician") }
    var expandedCategory by remember { mutableStateOf(false) }
    val categories = listOf(
        "Electrician", "Plumber", "Carpenter", "Painter", "AC Technician",
        "Mason", "CCTV Installer", "Welder", "Catering & Cooking",
        "Gardener", "Cleaner", "Computer Repair", "Appliance Technician"
    )

    var shortBio by remember { mutableStateOf("") }
    var serviceDistrict by remember { mutableStateOf("Colombo") }
    var expandedDistrict by remember { mutableStateOf(false) }
    val districts = listOf(
        "Colombo", "Gampaha", "Kalutara", "Kandy", "Matale", "Nuwara Eliya",
        "Galle", "Matara", "Hambantota", "Jaffna", "Kilinochchi", "Mannar",
        "Vavuniya", "Mullaitivu", "Batticaloa", "Ampara", "Trincomalee",
        "Kurunegala", "Puttalam", "Anuradhapura", "Polonnaruwa", "Badulla",
        "Monaragala", "Ratnapura", "Kegalle"
    )

    var workingLocation by remember { mutableStateOf("") }
    val workingDays = remember { mutableStateMapOf(
        "Mon" to true, "Tue" to true, "Wed" to true, "Thu" to true,
        "Fri" to true, "Sat" to false, "Sun" to false
    ) }

    var startTime by remember { mutableStateOf("08:00 AM") }
    var endTime by remember { mutableStateOf("06:00 PM") }

    val calendar = Calendar.getInstance()
    val startPickerDialog = android.app.TimePickerDialog(
        context, { _, h, m ->
            val amPm = if (h < 12) "AM" else "PM"
            val h12 = if (h == 0) 12 else if (h > 12) h - 12 else h
            startTime = String.format("%02d:%02d %s", h12, m, amPm)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
    )

    val endPickerDialog = android.app.TimePickerDialog(
        context, { _, h, m ->
            val amPm = if (h < 12) "AM" else "PM"
            val h12 = if (h == 0) 12 else if (h > 12) h - 12 else h
            endTime = String.format("%02d:%02d %s", h12, m, amPm)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
    )

    var rateAmount by remember { mutableStateOf("") }
    var rateType by remember { mutableStateOf("Per Hour") }
    var expandedRateType by remember { mutableStateOf(false) }
    val rateTypes = listOf("Per Hour", "Per Day", "Per Project")

    var transportStatus by remember { mutableStateOf("Transport included") }
    var expandedTransport by remember { mutableStateOf(false) }
    val transportOptions = listOf("Transport included", "Transport charges apply", "Negotiable")

    var isLoading by remember { mutableStateOf(false) }

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.DarkGray,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = Color.White,
        focusedContainerColor = Color(0xFF121212),
        unfocusedContainerColor = Color(0xFF121212)
    )

    BackHandler {
        onNavigateBack()
    }

    // Loading initial status check view
    if (isCheckingStatus) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    // 1. IF PENDING APPROVAL SCREEN
    if (approvalStatus == "pending") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = "Pending",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Application Under Review",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Thank you for registering as a professional on HireLK. Our admin team is verifying your NIC and details. This usually takes under 24 hours.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "• Real-time updates active", color = Color.LightGray, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "• This screen will automatically update once approved.", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
        return
    }

    // 2. IF APPROVED SCREEN
    if (approvalStatus == "approved") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Approved",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(90.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Registration Approved!",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Congratulations! Your account has been successfully verified by our administration team. You can now access your provider dashboard.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(36.dp))
                Button(
                    onClick = { onRegistrationSuccess() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Open Homepage",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        return
    }

    // 3. IF REJECTED SCREEN
    if (approvalStatus == "rejected") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Rejected",
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(90.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Registration Rejected",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Unfortunately, your application was rejected by the admin due to unclear documents or invalid information. Please re-apply with correct details.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(36.dp))
                Button(
                    onClick = { onSwitchToRoleSelect() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Register Again",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        return
    }

    // 4. NORMAL REGISTRATION FORM SCREEN (If approvalStatus == "none" or empty)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Provider Registration",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Photo
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .border(1.dp, Color.White, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF1C1C1E))
                    .clickable {
                        activeImageTarget = "profile"
                        imagePickerLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (profileBitmap != null) {
                    Image(
                        bitmap = profileBitmap!!.asImageBitmap(),
                        contentDescription = "Profile Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Add photo",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (profileImageUri != null) "Change profile photo" else "Add profile photo",
                color = Color.Gray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name", color = Color.Gray) },
                placeholder = { Text("e.g. Sunil Fernando", color = Color.DarkGray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = customTextFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = it },
                label = { Text("Mobile Number", color = Color.Gray) },
                placeholder = { Text("07X XXX XXXX", color = Color.DarkGray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = customTextFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nicNumber,
                onValueChange = { nicNumber = it },
                label = { Text("NIC Number", color = Color.Gray) },
                placeholder = { Text("for identity verification", color = Color.DarkGray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = customTextFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            // NIC Verification Upload Section (Front & Back)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "NIC Verification Photos",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Please upload clear photos of your NIC (Front and Back)",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1C1C1E))
                                .clickable {
                                    activeImageTarget = "nic_front"
                                    imagePickerLauncher.launch("image/*")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (nicFrontBitmap != null) {
                                Image(
                                    bitmap = nicFrontBitmap!!.asImageBitmap(),
                                    contentDescription = "NIC Front",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.UploadFile,
                                        contentDescription = "Upload Front",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Tap to upload", color = Color.Gray, fontSize = 11.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "NIC Front", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1C1C1E))
                                .clickable {
                                    activeImageTarget = "nic_back"
                                    imagePickerLauncher.launch("image/*")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (nicBackBitmap != null) {
                                Image(
                                    bitmap = nicBackBitmap!!.asImageBitmap(),
                                    contentDescription = "NIC Back",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.UploadFile,
                                        contentDescription = "Upload Back",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Tap to upload", color = Color.Gray, fontSize = 11.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "NIC Back", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = serviceCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Service Category", color = Color.Gray) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = customTextFieldColors
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false },
                    modifier = Modifier.background(Color(0xFF1C1C1E))
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category, color = Color.White) },
                            onClick = {
                                serviceCategory = category
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = shortBio,
                onValueChange = { shortBio = it },
                label = { Text("Short Bio", color = Color.Gray) },
                placeholder = { Text("Years of experience, certifications...", color = Color.DarkGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = customTextFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expandedDistrict,
                onExpandedChange = { expandedDistrict = !expandedDistrict },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = serviceDistrict,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Service District", color = Color.Gray) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDistrict) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = customTextFieldColors
                )
                ExposedDropdownMenu(
                    expanded = expandedDistrict,
                    onDismissRequest = { expandedDistrict = false },
                    modifier = Modifier
                        .background(Color(0xFF1C1C1E))
                        .heightIn(max = 250.dp)
                ) {
                    districts.forEach { district ->
                        DropdownMenuItem(
                            text = { Text(district, color = Color.White) },
                            onClick = {
                                serviceDistrict = district
                                expandedDistrict = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = workingLocation,
                onValueChange = { workingLocation = it },
                label = { Text("Working Location (Town/Area)", color = Color.Gray) },
                placeholder = { Text("e.g. Maharagama, Nugegoda", color = Color.DarkGray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = customTextFieldColors
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Working Days",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri").forEach { day ->
                        val isSelected = workingDays[day] == true
                        FilterChip(
                            selected = isSelected,
                            onClick = { workingDays[day] = !isSelected },
                            label = { Text(day, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xFF1C1C1E),
                                labelColor = Color.Gray,
                                selectedContainerColor = Color.White,
                                selectedLabelColor = Color.Black
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color.DarkGray,
                                selectedBorderColor = Color.White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    listOf("Sat", "Sun").forEach { day ->
                        val isSelected = workingDays[day] == true
                        FilterChip(
                            selected = isSelected,
                            onClick = { workingDays[day] = !isSelected },
                            label = { Text(day, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xFF1C1C1E),
                                labelColor = Color.Gray,
                                selectedContainerColor = Color.White,
                                selectedLabelColor = Color.Black
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color.DarkGray,
                                selectedBorderColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Working Hours",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { startPickerDialog.show() },
                            enabled = false,
                            shape = RoundedCornerShape(12.dp),
                            colors = customTextFieldColors
                        )
                    }
                    Text(
                        text = " to ",
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { endPickerDialog.show() },
                            enabled = false,
                            shape = RoundedCornerShape(12.dp),
                            colors = customTextFieldColors
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = rateAmount,
                    onValueChange = { rateAmount = it },
                    label = { Text("Rate Amount", color = Color.Gray) },
                    placeholder = { Text("LKR amount", color = Color.DarkGray) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = customTextFieldColors
                )
                Spacer(modifier = Modifier.width(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedRateType,
                    onExpandedChange = { expandedRateType = !expandedRateType },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = rateType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit", color = Color.Gray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRateType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = customTextFieldColors
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRateType,
                        onDismissRequest = { expandedRateType = false },
                        modifier = Modifier.background(Color(0xFF1C1C1E))
                    ) {
                        rateTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type, color = Color.White) },
                                onClick = {
                                    rateType = type
                                    expandedRateType = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expandedTransport,
                onExpandedChange = { expandedTransport = !expandedTransport },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = transportStatus,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Transport", color = Color.Gray) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTransport) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = customTextFieldColors
                )
                ExposedDropdownMenu(
                    expanded = expandedTransport,
                    onDismissRequest = { expandedTransport = false },
                    modifier = Modifier.background(Color(0xFF1C1C1E))
                ) {
                    transportOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = Color.White) },
                            onClick = {
                                transportStatus = option
                                expandedTransport = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (fullName.isBlank() || mobileNumber.isBlank() || nicNumber.isBlank() || workingLocation.isBlank() || rateAmount.isBlank()) {
                        Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (userId == null) {
                        Toast.makeText(context, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    coroutineScope.launch {
                        val profileUrl = if (profileImageUri != null) uploadWorkerImageToCloudinary(context, profileImageUri!!) ?: "" else ""
                        val nicFrontUrl = if (nicFrontUri != null) uploadWorkerImageToCloudinary(context, nicFrontUri!!) ?: "" else ""
                        val nicBackUrl = if (nicBackUri != null) uploadWorkerImageToCloudinary(context, nicBackUri!!) ?: "" else ""

                        val workerData = hashMapOf(
                            "userType" to "worker",
                            "fullName" to fullName,
                            "mobileNumber" to mobileNumber,
                            "nicNumber" to nicNumber,
                            "profileImageUrl" to profileUrl,
                            "nicFrontUrl" to nicFrontUrl,
                            "nicBackUrl" to nicBackUrl,
                            "serviceCategory" to serviceCategory,
                            "shortBio" to shortBio,
                            "serviceDistrict" to serviceDistrict,
                            "workingLocation" to workingLocation,
                            "workingDays" to workingDays.filterValues { it }.keys.toList(),
                            "workingHours" to "$startTime to $endTime",
                            "rateAmount" to rateAmount,
                            "rateType" to rateType,
                            "transportStatus" to transportStatus,
                            "approvalStatus" to "pending",
                            "isProfileCompleted" to true
                        )

                        db.collection("users").document(userId)
                            .set(workerData, com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "Application submitted successfully!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                } else {
                    Text(
                        text = "Submit Application",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Profile reviewed & verified within 24 hours",
                color = Color.Gray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Cloudinary Direct Unsigned Upload Helper Function
suspend fun uploadWorkerImageToCloudinary(context: Context, uri: Uri): String? {
    return withContext(Dispatchers.IO) {
        try {
            val cloudName = "tjyge2k8"
            val uploadPreset = "hirelk_preset"
            val urlString = "https://api.cloudinary.com/v1_1/$cloudName/image/upload"

            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val boundary = "Boundary-" + System.currentTimeMillis()
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "POST"
                doOutput = true
                doInput = true
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            }

            val outputStream = connection.outputStream
            val writer = java.io.PrintWriter(java.io.OutputStreamWriter(outputStream, "UTF-8"), true)

            writer.append("--$boundary\r\n")
            writer.append("Content-Disposition: form-data; name=\"upload_preset\"\r\n\r\n")
            writer.append("$uploadPreset\r\n")

            writer.append("--$boundary\r\n")
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"worker_upload.jpg\"\r\n")
            writer.append("Content-Type: image/jpeg\r\n\r\n")
            writer.flush()

            outputStream.write(bytes)
            outputStream.flush()

            writer.append("\r\n--$boundary--\r\n")
            writer.flush()
            writer.close()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseStream = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(responseStream)
                return@withContext jsonObject.getString("secure_url")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        null
    }
}