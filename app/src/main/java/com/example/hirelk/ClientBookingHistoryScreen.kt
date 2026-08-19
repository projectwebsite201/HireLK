package com.example.hirelk.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

// ==================== DATA CLASS (Only Here) ====================
data class ClientBooking(
    val id: String = "",
    val workerId: String = "",
    val workerName: String = "",
    val workerCategory: String = "",
    val date: String = "",
    val time: String = "",
    val address: String = "",
    val problem: String = "",
    val status: String = "pending",
    val createdAt: com.google.firebase.Timestamp? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientBookingHistoryScreen(
    onBack: () -> Unit,
    onBookingClick: (String) -> Unit
) {
    val primaryGreen = Color(0xFF1E6030)
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    var bookings by remember { mutableStateOf(listOf<ClientBooking>()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) } // 0=All, 1=Active, 2=Completed
    var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }

    // ==================== REAL-TIME LISTENER ====================
    LaunchedEffect(userId) {
        if (userId != null) {
            val query = db.collection("bookings")
                .whereEqualTo("clientId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)

            listenerRegistration = query.addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val list = snapshot?.documents?.mapNotNull { doc ->
                    ClientBooking(
                        id = doc.id,
                        workerId = doc.getString("workerId") ?: "",
                        workerName = doc.getString("workerName") ?: "Worker",
                        workerCategory = doc.getString("workerCategory") ?: "",
                        date = doc.getString("date") ?: "",
                        time = doc.getString("time") ?: "",
                        address = doc.getString("address") ?: "",
                        problem = doc.getString("problem") ?: "",
                        status = doc.getString("status") ?: "pending",
                        createdAt = doc.getTimestamp("createdAt")
                    )
                } ?: emptyList()
                bookings = list
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            listenerRegistration?.remove()
        }
    }

    val filteredBookings = when (selectedTab) {
        0 -> bookings
        1 -> bookings.filter { it.status == "pending" || it.status == "confirmed" }
        else -> bookings.filter { it.status == "completed" || it.status == "rejected" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings", color = Color.Black, fontWeight = FontWeight.Bold) },
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
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = primaryGreen
            ) {
                listOf("All", "Active", "Completed").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 14.sp) },
                        selectedContentColor = primaryGreen,
                        unselectedContentColor = Color.Gray
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryGreen)
                }
            } else if (filteredBookings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No bookings found", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                        Text("Your bookings will appear here", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredBookings) { booking ->
                        ClientBookingCard(
                            booking = booking,
                            onClick = { onBookingClick(booking.id) }
                        )
                    }
                }
            }
        }
    }
}

// ==================== BOOKING CARD ====================
@Composable
fun ClientBookingCard(booking: ClientBooking, onClick: () -> Unit) {
    val statusColor = when (booking.status) {
        "pending" -> Color(0xFFFFA726)
        "confirmed" -> Color(0xFF4CAF50)
        "completed" -> Color(0xFF2196F3)
        "rejected" -> Color(0xFFD32F2F)
        else -> Color.Gray
    }

    val statusText = when (booking.status) {
        "pending" -> "Pending"
        "confirmed" -> "Confirmed"
        "completed" -> "Completed"
        "rejected" -> "Rejected"
        else -> booking.status
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE8F5E9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = booking.workerName.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E6030),
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(booking.workerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(booking.workerCategory, fontSize = 13.sp, color = Color.Gray)
                    }
                }
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(statusText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${booking.date} at ${booking.time}", fontSize = 13.sp, color = Color.Gray)
                }
            }
        }
    }
}