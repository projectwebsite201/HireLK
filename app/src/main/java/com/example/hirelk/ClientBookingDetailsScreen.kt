package com.example.hirelk.ui.theme

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

// ==================== ClientBooking Data Class ====================
// මෙය ClientBookingHistoryScreen.kt එකේ තියෙනවා, ඒක Import කරන්න ඕනේ නැහැ
// එකම package එක නිසා auto detect වෙනවා

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientBookingDetailsScreen(
    bookingId: String,
    onBack: () -> Unit,
    onChatClick: (String, String) -> Unit
) {
    val primaryGreen = Color(0xFF1E6030)
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var booking by remember { mutableStateOf<ClientBooking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }

    LaunchedEffect(bookingId) {
        listenerRegistration = db.collection("bookings").document(bookingId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    booking = ClientBooking(
                        id = snapshot.id,
                        workerId = snapshot.getString("workerId") ?: "",
                        workerName = snapshot.getString("workerName") ?: "Worker",
                        workerCategory = snapshot.getString("workerCategory") ?: "",
                        date = snapshot.getString("date") ?: "",
                        time = snapshot.getString("time") ?: "",
                        address = snapshot.getString("address") ?: "",
                        problem = snapshot.getString("problem") ?: "",
                        status = snapshot.getString("status") ?: "pending",
                        createdAt = snapshot.getTimestamp("createdAt")
                    )
                }
                isLoading = false
            }
    }

    DisposableEffect(Unit) {
        onDispose {
            listenerRegistration?.remove()
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = primaryGreen)
        }
        return
    }

    val b = booking ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Booking not found", color = Color.Gray)
        }
        return
    }

    val statusColor = when (b.status) {
        "pending" -> Color(0xFFFFA726)
        "confirmed" -> Color(0xFF4CAF50)
        "completed" -> Color(0xFF2196F3)
        "rejected" -> Color(0xFFD32F2F)
        else -> Color.Gray
    }

    val statusText = when (b.status) {
        "pending" -> "Pending Approval"
        "confirmed" -> "Confirmed"
        "completed" -> "Completed"
        "rejected" -> "Rejected"
        else -> b.status
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details", color = Color.Black, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Booking Status", fontSize = 12.sp, color = Color.Gray)
                        Text(statusText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = statusColor)
                    }
                    Surface(
                        shape = CircleShape,
                        color = statusColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = statusColor)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SERVICE PROVIDER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = Color(0xFFE8F5E9)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    b.workerName.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E6030),
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(b.workerName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(b.workerCategory, fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailRow(Icons.Default.DateRange, "Date", b.date)
                    DetailRow(Icons.Default.Schedule, "Time", b.time)
                    DetailRow(Icons.Default.LocationOn, "Address", b.address)
                    DetailRow(Icons.Default.Description, "Problem", b.problem)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (b.status == "pending") {
                OutlinedButton(
                    onClick = {
                        db.collection("bookings").document(b.id)
                            .update("status", "rejected")
                            .addOnSuccessListener {
                                Toast.makeText(context, "Booking cancelled", Toast.LENGTH_SHORT).show()
                            }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Text("Cancel Booking", fontWeight = FontWeight.Bold)
                }
            } else if (b.status == "confirmed") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onChatClick(b.workerId, b.workerName) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, primaryGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryGreen)
                    ) {
                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = primaryGreen)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chat")
                    }
                    Button(
                        onClick = {
                            db.collection("bookings").document(b.id)
                                .update("status", "completed")
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Service marked as completed!", Toast.LENGTH_SHORT).show()
                                }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
                    ) {
                        Text("Complete", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (b.status == "completed" || b.status == "rejected") {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (b.status == "completed") "✓ Service Completed" else "✗ Booking Cancelled",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = statusColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1E6030), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(start = 28.dp))
    }
}