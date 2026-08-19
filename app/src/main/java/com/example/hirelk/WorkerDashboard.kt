package com.example.hirelk

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.*

// Import the actual EditProfileScreen and ChatScreen
import com.example.hirelk.ui.theme.ChatScreen

// ==========================================
// Data Classes
// ==========================================
data class WorkerBooking(
    val id: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val clientPhone: String = "",
    val address: String = "",
    val date: String = "",
    val time: String = "",
    val problem: String = "",
    val status: String = "pending",
    val createdAt: com.google.firebase.Timestamp? = null
)

data class WorkerStats(
    val totalBookings: Int = 0,
    val pendingBookings: Int = 0,
    val confirmedBookings: Int = 0,
    val completedBookings: Int = 0,
    val totalEarnings: Double = 0.0
)

data class WorkerChat(
    val chatId: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val lastMessage: String = "",
    val lastMessageTime: com.google.firebase.Timestamp? = null,
    val unreadCount: Int = 0
)

data class ChatMessage(
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: com.google.firebase.Timestamp? = null,
    val isRead: Boolean = false
)

// ==========================================
// Navigation Enums
// ==========================================
enum class WorkerNavTab {
    HOME, BOOKINGS, CHATS, PROFILE
}

enum class WorkerHomeSubScreen {
    HOME_MAIN, BOOKING_DETAILS
}

enum class WorkerProfileSubScreen {
    VIEW_PROFILE, EDIT_PROFILE
}

// ==================== NEW: Chat Sub Navigation ====================
enum class WorkerChatSubScreen {
    CHAT_LIST, CHAT_DETAIL
}

// ==========================================
// Main Worker App
// ==========================================
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainProviderApp() {
    var currentTab by remember { mutableStateOf(WorkerNavTab.HOME) }
    var homeSubScreen by remember { mutableStateOf(WorkerHomeSubScreen.HOME_MAIN) }
    var selectedBookingId by remember { mutableStateOf<String?>(null) }
    var profileSubScreen by remember { mutableStateOf(WorkerProfileSubScreen.VIEW_PROFILE) }

    // ==================== NEW: Chat Navigation States ====================
    var chatSubScreen by remember { mutableStateOf(WorkerChatSubScreen.CHAT_LIST) }
    var selectedChatClientId by remember { mutableStateOf("") }
    var selectedChatClientName by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val currentUser = auth.currentUser
    val userId = currentUser?.uid

    // Worker Data
    var workerName by remember { mutableStateOf("Loading...") }
    var workerCategory by remember { mutableStateOf("") }
    var workerProfileImage by remember { mutableStateOf("") }
    var workerId by remember { mutableStateOf("") }

    // Load Worker Data
    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        workerName = doc.getString("fullName") ?: "Worker"
                        workerCategory = doc.getString("serviceCategory") ?: "Service Provider"
                        workerProfileImage = doc.getString("profileImageUrl") ?: ""
                        workerId = userId
                    }
                }
        }
    }

    // Logout function
    fun logout() {
        auth.signOut()
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() with fadeOut() },
                    label = "TabTransition"
                ) { targetTab ->
                    when (targetTab) {
                        WorkerNavTab.HOME -> {
                            Crossfade(targetState = homeSubScreen, label = "HomeSubNavigation") { subScreen ->
                                when (subScreen) {
                                    WorkerHomeSubScreen.HOME_MAIN -> {
                                        WorkerHomeScreen(
                                            workerName = workerName,
                                            workerCategory = workerCategory,
                                            workerProfileImage = workerProfileImage,
                                            onViewBookingDetails = { bookingId ->
                                                selectedBookingId = bookingId
                                                homeSubScreen = WorkerHomeSubScreen.BOOKING_DETAILS
                                            },
                                            onProfileClick = {
                                                currentTab = WorkerNavTab.PROFILE
                                                profileSubScreen = WorkerProfileSubScreen.VIEW_PROFILE
                                            },
                                            onNotificationClick = {
                                                Toast.makeText(context, "Notifications clicked", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                    WorkerHomeSubScreen.BOOKING_DETAILS -> {
                                        selectedBookingId?.let { bookingId ->
                                            WorkerBookingDetailsScreen(
                                                bookingId = bookingId,
                                                onBackClick = {
                                                    homeSubScreen = WorkerHomeSubScreen.HOME_MAIN
                                                },
                                                onStatusUpdate = {
                                                    homeSubScreen = WorkerHomeSubScreen.HOME_MAIN
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        WorkerNavTab.BOOKINGS -> {
                            WorkerAllBookingsScreen()
                        }
                        // ==================== FIXED: CHATS TAB ====================
                        WorkerNavTab.CHATS -> {
                            Crossfade(targetState = chatSubScreen, label = "ChatSubNavigation") { subState ->
                                when (subState) {
                                    WorkerChatSubScreen.CHAT_LIST -> {
                                        WorkerChatsScreen(
                                            onChatClick = { clientId, clientName ->
                                                selectedChatClientId = clientId
                                                selectedChatClientName = clientName
                                                chatSubScreen = WorkerChatSubScreen.CHAT_DETAIL
                                            }
                                        )
                                    }
                                    WorkerChatSubScreen.CHAT_DETAIL -> {
                                        ChatScreen(
                                            workerId = selectedChatClientId, // The other person is the client
                                            workerName = selectedChatClientName,
                                            onBack = {
                                                chatSubScreen = WorkerChatSubScreen.CHAT_LIST
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        WorkerNavTab.PROFILE -> {
                            Crossfade(targetState = profileSubScreen, label = "ProfileSubNavigation") { subState ->
                                when (subState) {
                                    WorkerProfileSubScreen.VIEW_PROFILE -> {
                                        WorkerProfileScreen(
                                            workerName = workerName,
                                            workerCategory = workerCategory,
                                            onEditClick = { profileSubScreen = WorkerProfileSubScreen.EDIT_PROFILE },
                                            onLogout = { logout() }
                                        )
                                    }
                                    WorkerProfileSubScreen.EDIT_PROFILE -> {
                                        // ==================== FIXED: Use actual EditProfileScreen ====================
                                        EditProfileScreen(
                                            onBack = { profileSubScreen = WorkerProfileSubScreen.VIEW_PROFILE }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Bar
            val showBottomBar = !(currentTab == WorkerNavTab.PROFILE && profileSubScreen == WorkerProfileSubScreen.EDIT_PROFILE) &&
                    !(currentTab == WorkerNavTab.HOME && homeSubScreen == WorkerHomeSubScreen.BOOKING_DETAILS) &&
                    !(currentTab == WorkerNavTab.CHATS && chatSubScreen == WorkerChatSubScreen.CHAT_DETAIL)

            if (showBottomBar) {
                WorkerBottomNavigation(
                    currentTab = currentTab,
                    onTabSelected = {
                        currentTab = it
                        if (it == WorkerNavTab.HOME) {
                            homeSubScreen = WorkerHomeSubScreen.HOME_MAIN
                        }
                        if (it == WorkerNavTab.PROFILE) {
                            profileSubScreen = WorkerProfileSubScreen.VIEW_PROFILE
                        }
                        if (it == WorkerNavTab.CHATS) {
                            chatSubScreen = WorkerChatSubScreen.CHAT_LIST
                        }
                    }
                )
            }
        }
    }
}

// ==========================================
// Bottom Navigation
// ==========================================
@Composable
fun WorkerBottomNavigation(
    currentTab: WorkerNavTab,
    onTabSelected: (WorkerNavTab) -> Unit
) {
    val primaryGreen = Color(0xFF1E6030)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column {
            Divider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WorkerNavItem(
                    label = "Home",
                    tab = WorkerNavTab.HOME,
                    currentTab = currentTab,
                    icon = Icons.Default.Home,
                    onSelect = onTabSelected
                )
                WorkerNavItem(
                    label = "Bookings",
                    tab = WorkerNavTab.BOOKINGS,
                    currentTab = currentTab,
                    icon = Icons.Default.Bookmark,
                    onSelect = onTabSelected
                )
                WorkerNavItem(
                    label = "Chats",
                    tab = WorkerNavTab.CHATS,
                    currentTab = currentTab,
                    icon = Icons.Default.Chat,
                    onSelect = onTabSelected
                )
                WorkerNavItem(
                    label = "Profile",
                    tab = WorkerNavTab.PROFILE,
                    currentTab = currentTab,
                    icon = Icons.Default.Person,
                    onSelect = onTabSelected
                )
            }
        }
    }
}

@Composable
fun WorkerNavItem(
    label: String,
    tab: WorkerNavTab,
    currentTab: WorkerNavTab,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    hasBadge: Boolean = false,
    onSelect: (WorkerNavTab) -> Unit
) {
    val isSelected = currentTab == tab
    val primaryGreen = Color(0xFF1E6030)
    val textGray = Color(0xFF71717A)
    val textColor = if (isSelected) primaryGreen else textGray
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onSelect(tab) }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Box {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            if (hasBadge && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFEF4444), CircleShape)
                        .align(Alignment.TopEnd)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = fontWeight,
            color = textColor
        )
    }
}

// ==========================================
// 1. WORKER HOME SCREEN
// ==========================================
@Composable
fun WorkerHomeScreen(
    workerName: String,
    workerCategory: String,
    workerProfileImage: String,
    onViewBookingDetails: (String) -> Unit,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    var bookings by remember { mutableStateOf(listOf<WorkerBooking>()) }
    var stats by remember { mutableStateOf(WorkerStats()) }
    var isLoading by remember { mutableStateOf(true) }
    var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }

    LaunchedEffect(userId) {
        if (userId != null) {
            val query = db.collection("bookings")
                .whereEqualTo("workerId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)

            listenerRegistration = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    WorkerBooking(
                        id = doc.id,
                        clientId = doc.getString("clientId") ?: "",
                        clientName = doc.getString("clientName") ?: "Client",
                        clientPhone = doc.getString("clientPhone") ?: "",
                        address = doc.getString("address") ?: "",
                        date = doc.getString("date") ?: "",
                        time = doc.getString("time") ?: "",
                        problem = doc.getString("problem") ?: "",
                        status = doc.getString("status") ?: "pending",
                        createdAt = doc.getTimestamp("createdAt")
                    )
                } ?: emptyList()
                bookings = list

                val pending = list.filter { it.status == "pending" }.size
                val confirmed = list.filter { it.status == "confirmed" }.size
                val completed = list.filter { it.status == "completed" }.size
                val total = list.size

                stats = WorkerStats(
                    totalBookings = total,
                    pendingBookings = pending,
                    confirmedBookings = confirmed,
                    completedBookings = completed,
                    totalEarnings = confirmed * 1800.0
                )
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Provider Dashboard", fontSize = 13.sp, color = Color.Gray)
                Text(
                    text = workerName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onProfileClick() },
                    shape = CircleShape,
                    color = Color(0xFFDBEAFE)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = workerName.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1E6030))
            }
            return
        }

        // Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WorkerStatCard(
                value = stats.pendingBookings.toString(),
                label = "Pending",
                modifier = Modifier.weight(1f),
                color = Color(0xFFFFA726)
            )
            WorkerStatCard(
                value = stats.confirmedBookings.toString(),
                label = "Confirmed",
                modifier = Modifier.weight(1f),
                color = Color(0xFF4CAF50)
            )
            WorkerStatCard(
                value = stats.completedBookings.toString(),
                label = "Completed",
                modifier = Modifier.weight(1f),
                color = Color(0xFF2196F3)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Earnings Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Earnings",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "LKR ${stats.totalEarnings.toInt()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E6030)
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1E6030).copy(alpha = 0.1f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = "Earnings",
                            tint = Color(0xFF1E6030)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Recent Bookings",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (bookings.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.BookmarkBorder,
                        contentDescription = "No bookings",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No bookings yet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Text(
                        text = "Bookings will appear here when clients request your service",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            bookings.take(3).forEach { booking ->
                WorkerBookingCard(
                    booking = booking,
                    onViewClick = { onViewBookingDetails(booking.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ==========================================
// Worker Stat Card
// ==========================================
@Composable
fun WorkerStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    color: Color
) {
    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

// ==========================================
// Worker Booking Card
// ==========================================
@Composable
fun WorkerBookingCard(
    booking: WorkerBooking,
    onViewClick: () -> Unit
) {
    val context = LocalContext.current
    val statusColor = when (booking.status) {
        "pending" -> Color(0xFFFFA726)
        "confirmed" -> Color(0xFF4CAF50)
        "completed" -> Color(0xFF2196F3)
        "rejected" -> Color(0xFFD32F2F)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = Color(0xFFE8F5E9)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = booking.clientName.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E6030),
                                fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = booking.clientName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "${booking.date} • ${booking.time}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = booking.status.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewClick,
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF1E6030))
                ) {
                    Text("View", color = Color(0xFF1E6030), fontSize = 12.sp)
                }
                if (booking.status == "pending") {
                    Button(
                        onClick = {
                            val db = FirebaseFirestore.getInstance()
                            db.collection("bookings").document(booking.id)
                                .update("status", "confirmed")
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Booking accepted!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        },
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E6030))
                    ) {
                        Text("Accept", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. WORKER BOOKING DETAILS
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerBookingDetailsScreen(
    bookingId: String,
    onBackClick: () -> Unit,
    onStatusUpdate: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    var booking by remember { mutableStateOf<WorkerBooking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }

    LaunchedEffect(bookingId) {
        listenerRegistration = db.collection("bookings").document(bookingId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    booking = WorkerBooking(
                        id = snapshot.id,
                        clientId = snapshot.getString("clientId") ?: "",
                        clientName = snapshot.getString("clientName") ?: "Client",
                        clientPhone = snapshot.getString("clientPhone") ?: "",
                        address = snapshot.getString("address") ?: "",
                        date = snapshot.getString("date") ?: "",
                        time = snapshot.getString("time") ?: "",
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details", color = Color.Black, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1E6030))
            }
            return@Scaffold
        }

        val b = booking ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Booking not found")
            }
            return@Scaffold
        }

        val statusColor = when (b.status) {
            "pending" -> Color(0xFFFFA726)
            "confirmed" -> Color(0xFF4CAF50)
            "completed" -> Color(0xFF2196F3)
            "rejected" -> Color(0xFFD32F2F)
            else -> Color.Gray
        }

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
                        Text(
                            b.status.uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = statusColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = statusColor
                            )
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
                    Text("CLIENT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = Color(0xFFE8F5E9)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    b.clientName.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E6030),
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                b.clientName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                b.clientPhone,
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("LOCATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        b.address,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SCHEDULE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Date", fontSize = 12.sp, color = Color.Gray)
                            Text(b.date, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Column {
                            Text("Time", fontSize = 12.sp, color = Color.Gray)
                            Text(b.time, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PROBLEM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        b.problem,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (b.status == "pending") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            db.collection("bookings").document(b.id)
                                .update("status", "rejected")
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Booking rejected", Toast.LENGTH_SHORT).show()
                                    onStatusUpdate()
                                }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFD32F2F)
                        )
                    ) {
                        Text("Reject", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            db.collection("bookings").document(b.id)
                                .update("status", "confirmed")
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Booking accepted!", Toast.LENGTH_SHORT).show()
                                    onStatusUpdate()
                                }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E6030))
                    ) {
                        Text("Accept", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (b.status == "confirmed") {
                Button(
                    onClick = {
                        db.collection("bookings").document(b.id)
                            .update("status", "completed")
                            .addOnSuccessListener {
                                Toast.makeText(context, "Booking marked as completed!", Toast.LENGTH_SHORT).show()
                                onStatusUpdate()
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E6030))
                ) {
                    Text("Complete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// 3. WORKER ALL BOOKINGS SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerAllBookingsScreen() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    var bookings by remember { mutableStateOf(listOf<WorkerBooking>()) }
    var isLoading by remember { mutableStateOf(true) }
    var filter by remember { mutableStateOf("All") }

    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("bookings")
                .whereEqualTo("workerId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    bookings = result.documents.mapNotNull { doc ->
                        WorkerBooking(
                            id = doc.id,
                            clientId = doc.getString("clientId") ?: "",
                            clientName = doc.getString("clientName") ?: "Client",
                            clientPhone = doc.getString("clientPhone") ?: "",
                            address = doc.getString("address") ?: "",
                            date = doc.getString("date") ?: "",
                            time = doc.getString("time") ?: "",
                            problem = doc.getString("problem") ?: "",
                            status = doc.getString("status") ?: "pending",
                            createdAt = doc.getTimestamp("createdAt")
                        )
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    val filteredBookings = when (filter) {
        "Pending" -> bookings.filter { it.status == "pending" }
        "Confirmed" -> bookings.filter { it.status == "confirmed" }
        "Completed" -> bookings.filter { it.status == "completed" }
        else -> bookings
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Bookings", color = Color.Black, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Pending", "Confirmed", "Completed").forEach { tab ->
                    FilterChip(
                        selected = filter == tab,
                        onClick = { filter = tab },
                        label = { Text(tab, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1E6030),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color.Gray
                        )
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1E6030))
                }
            } else if (filteredBookings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No ${filter.lowercase()} bookings",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredBookings) { booking ->
                        WorkerBookingSmallCard(booking = booking)
                    }
                }
            }
        }
    }
}

@Composable
fun WorkerBookingSmallCard(booking: WorkerBooking) {
    val statusColor = when (booking.status) {
        "pending" -> Color(0xFFFFA726)
        "confirmed" -> Color(0xFF4CAF50)
        "completed" -> Color(0xFF2196F3)
        "rejected" -> Color(0xFFD32F2F)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    booking.clientName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    "${booking.date} • ${booking.time}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    booking.address.take(30) + if (booking.address.length > 30) "..." else "",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    booking.status.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }
    }
}

// ==========================================
// 4. WORKER CHATS SCREEN (UPDATED with callback)
// ==========================================
@Composable
fun WorkerChatsScreen(
    onChatClick: (String, String) -> Unit = { _, _ -> }
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    val context = LocalContext.current

    var chats by remember { mutableStateOf(listOf<WorkerChat>()) }
    var isLoading by remember { mutableStateOf(true) }
    var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val userId = currentUser.uid

            val query = db.collection("messages")
                .whereEqualTo("receiverId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)

            listenerRegistration = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }

                val messagesList = snapshot?.documents?.mapNotNull { doc ->
                    ChatMessage(
                        chatId = doc.getString("chatId") ?: "",
                        senderId = doc.getString("senderId") ?: "",
                        receiverId = doc.getString("receiverId") ?: "",
                        text = doc.getString("text") ?: "",
                        timestamp = doc.getTimestamp("timestamp"),
                        isRead = doc.getBoolean("isRead") ?: false
                    )
                } ?: emptyList()

                val chatMap = mutableMapOf<String, MutableList<ChatMessage>>()
                messagesList.forEach { msg ->
                    if (!chatMap.containsKey(msg.chatId)) {
                        chatMap[msg.chatId] = mutableListOf()
                    }
                    chatMap[msg.chatId]?.add(msg)
                }

                val chatList = mutableListOf<WorkerChat>()
                chatMap.forEach { (chatId, msgs) ->
                    val latestMsg = msgs.maxByOrNull { it.timestamp?.toDate() ?: Date() }
                    val clientId = msgs.firstOrNull()?.senderId ?: ""

                    if (latestMsg != null && clientId.isNotEmpty()) {
                        db.collection("users").document(clientId).get()
                            .addOnSuccessListener { doc ->
                                val clientName = doc.getString("fullName") ?: "Client"
                                val chat = WorkerChat(
                                    chatId = chatId,
                                    clientId = clientId,
                                    clientName = clientName,
                                    lastMessage = latestMsg.text,
                                    lastMessageTime = latestMsg.timestamp,
                                    unreadCount = msgs.count { it.receiverId == userId && !it.isRead }
                                )
                                val existingIndex = chatList.indexOfFirst { it.chatId == chatId }
                                if (existingIndex >= 0) {
                                    chatList[existingIndex] = chat
                                } else {
                                    chatList.add(chat)
                                }
                                chatList.sortByDescending { it.lastMessageTime?.toDate() }
                                chats = chatList.toList()
                                isLoading = false
                            }
                            .addOnFailureListener {
                                val chat = WorkerChat(
                                    chatId = chatId,
                                    clientId = clientId,
                                    clientName = "Client",
                                    lastMessage = latestMsg.text,
                                    lastMessageTime = latestMsg.timestamp,
                                    unreadCount = msgs.count { it.receiverId == userId && !it.isRead }
                                )
                                chatList.add(chat)
                                chatList.sortByDescending { it.lastMessageTime?.toDate() }
                                chats = chatList.toList()
                                isLoading = false
                            }
                    }
                }

                if (chatList.isEmpty()) {
                    isLoading = false
                }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Chats",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1E6030))
            }
        } else if (chats.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Chat,
                        contentDescription = null,
                        tint = Color(0xFF1E6030),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No conversations yet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Text(
                        "Your chats with clients will appear here",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chats) { chat ->
                    WorkerChatItem(
                        chat = chat,
                        onClick = {
                            // ==================== FIXED: Call onChatClick ====================
                            onChatClick(chat.clientId, chat.clientName)
                        }
                    )
                }
            }
        }
    }
}

// ==========================================
// Worker Chat Item
// ==========================================
@Composable
fun WorkerChatItem(
    chat: WorkerChat,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color(0xFFE8F5E9)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = chat.clientName.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E6030),
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.clientName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    if (chat.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(Color(0xFFEF4444), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chat.unreadCount.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Text(
                    text = chat.lastMessage,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = chat.lastMessageTime?.let {
                        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        format.format(it.toDate())
                    } ?: "",
                    fontSize = 11.sp,
                    color = Color.Gray.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ==========================================
// 5. WORKER PROFILE SCREEN
// ==========================================
@Composable
fun WorkerProfileScreen(
    workerName: String,
    workerCategory: String,
    onEditClick: () -> Unit,
    onLogout: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(4.9) }
    var totalReviews by remember { mutableStateOf(42) }

    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        email = doc.getString("email") ?: ""
                        phone = doc.getString("mobileNumber") ?: ""
                    }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = Color(0xFFDBEAFE)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = workerName.take(2).uppercase(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    workerName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    workerCategory,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐ $rating", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEAB308))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("($totalReviews reviews)", fontSize = 13.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF1E6030))
                ) {
                    Text("Edit Profile", color = Color(0xFF1E6030), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Contact Information", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF1E6030), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(phone.ifEmpty { "+94 77 123 4567" }, fontSize = 14.sp, color = Color.Black)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF1E6030), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(email.ifEmpty { "worker@hirelk.com" }, fontSize = 14.sp, color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Account", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                ProfileMenuItemWorker(icon = Icons.Default.Edit, title = "Edit Profile", onClick = onEditClick)
                Divider(color = Color(0xFFF0F0F0))
                ProfileMenuItemWorker(icon = Icons.Default.Info, title = "About HireLK", onClick = { /* Navigate to About */ })
                Divider(color = Color(0xFFF0F0F0))
                ProfileMenuItemWorker(icon = Icons.Default.Help, title = "Help & Support", onClick = { /* Navigate to Help */ })
                Divider(color = Color(0xFFF0F0F0))
                ProfileMenuItemWorker(icon = Icons.Default.Settings, title = "Settings", onClick = { /* Navigate to Settings */ })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Text("Log Out", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ProfileMenuItemWorker(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF1E6030),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}