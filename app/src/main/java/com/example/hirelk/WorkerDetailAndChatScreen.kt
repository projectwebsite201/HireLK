package com.example.hirelk.ui.theme

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

import com.example.hirelk.WorkerModel

// ==================== WORKER DETAIL SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDetailScreen(
    workerId: String,
    onBack: () -> Unit,
    onChatClick: (workerId: String, workerName: String) -> Unit,  // Changed: workerId + workerName
    onHireClick: (workerId: String, workerName: String, workerCategory: String, workerRate: String) -> Unit
) {
    var worker by remember { mutableStateOf<WorkerModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(workerId) {
        db.collection("users").document(workerId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    worker = WorkerModel(
                        id = doc.id,
                        fullName = doc.getString("fullName") ?: "Worker",
                        serviceCategory = doc.getString("serviceCategory") ?: "Electrician",
                        serviceDistrict = doc.getString("serviceDistrict") ?: "Colombo",
                        rateAmount = doc.getString("rateAmount") ?: "1,800",
                        rateType = doc.getString("rateType") ?: "Per Hour",
                        approvalStatus = doc.getString("approvalStatus") ?: "approved",
                        shortBio = doc.getString("shortBio") ?: "",
                        profileImageUrl = doc.getString("profileImageUrl") ?: ""
                    )
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF1E6030))
        }
        return
    }

    val w = worker ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Worker not found")
        }
        return
    }

    val primaryGreen = Color(0xFF1E6030)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Worker Profile", color = Color.Black, fontWeight = FontWeight.Bold) },
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFE3F2FD), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = if (w.fullName.isNotEmpty()) w.fullName.take(2).uppercase() else "W"
                        Text(initials, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0), fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(w.fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("${w.serviceCategory} ● ${w.serviceDistrict}", fontSize = 13.sp, color = Color.Gray)
                    }
                }
                // Status badge
                val isApproved = w.approvalStatus.equals("approved", ignoreCase = true)
                val badgeBg = if (isApproved) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                val badgeTextCol = if (isApproved) Color(0xFF2E7D32) else Color(0xFFE65100)
                val displayStatus = if (isApproved) "Available Now" else w.approvalStatus
                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(displayStatus, color = badgeTextCol, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About
            Text("ABOUT", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                if (w.shortBio.isNotEmpty()) w.shortBio else "10+ years in domestic electrical work, grid management, short circuit diagnosis, and panel installation. LECO certified.",
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pricing & Details
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PRICING & DETAILS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Rate", fontSize = 12.sp, color = Color.Gray)
                            Text("LKR ${w.rateAmount} / ${w.rateType}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryGreen)
                        }
                        Column {
                            Text("Response time", fontSize = 12.sp, color = Color.Gray)
                            Text("~30 mins", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                        }
                        Column {
                            Text("Working days", fontSize = 12.sp, color = Color.Gray)
                            Text("Mon – Sat", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                        }
                        Column {
                            Text("Hours", fontSize = 12.sp, color = Color.Gray)
                            Text("8 AM – 6 PM", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ratings
            Text("RATINGS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⭐️⭐️⭐️⭐️⭐️", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("4.9 (42 reviews)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(8.dp))
            RatingBarRow(label = "5", percentage = 0.85f, color = primaryGreen)
            RatingBarRow(label = "4", percentage = 0.10f, color = primaryGreen)
            RatingBarRow(label = "3", percentage = 0.05f, color = primaryGreen)

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onChatClick(w.id, w.fullName) },  // Pass both workerId and workerName
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, primaryGreen),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Chat", tint = primaryGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chat First", color = primaryGreen, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onHireClick(w.id, w.fullName, w.serviceCategory, w.rateAmount) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Hire Now →", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun RatingBarRow(label: String, percentage: Float, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage)
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(4.dp))
            )
        }
        Text("${(percentage * 100).toInt()}%", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.width(40.dp))
    }
}

// ==================== CHAT SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    workerId: String,
    workerName: String,
    onBack: () -> Unit
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }

    val chatId = if (currentUser != null) {
        val ids = listOf(currentUser.uid, workerId).sorted()
        "${ids[0]}_${ids[1]}"
    } else ""

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatId) {
        if (chatId.isNotEmpty()) {
            val query = db.collection("messages")
                .whereEqualTo("chatId", chatId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)

            listenerRegistration = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChatScreen", "Listen failed.", error)
                    return@addSnapshotListener
                }
                val msgs = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChatMessage::class.java)
                } ?: emptyList()
                messages = msgs
                coroutineScope.launch {
                    if (messages.isNotEmpty()) {
                        listState.animateScrollToItem(messages.size - 1)
                    }
                }
            }
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
                title = { Text(workerName, color = Color.Black) },
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
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                state = listState,
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    val isOwn = msg.senderId == currentUser?.uid
                    MessageBubble(
                        text = msg.text,
                        isOwn = isOwn,
                        timestamp = msg.timestamp
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Type a message...", color = Color.Gray) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E6030),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank() && currentUser != null && chatId.isNotEmpty()) {
                                val message = ChatMessage(
                                    chatId = chatId,
                                    senderId = currentUser.uid,
                                    receiverId = workerId,
                                    text = inputText,
                                    timestamp = Timestamp.now()
                                )
                                db.collection("messages").add(message)
                                    .addOnSuccessListener {
                                        inputText = ""
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("ChatScreen", "Failed to send message", e)
                                    }
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF1E6030), CircleShape)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(text: String, isOwn: Boolean, timestamp: Timestamp?) {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val timeStr = timestamp?.let { dateFormat.format(it.toDate()) } ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .background(
                        if (isOwn) Color(0xFF1E6030) else Color.White,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isOwn) 16.dp else 4.dp,
                            bottomEnd = if (isOwn) 4.dp else 16.dp
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = text,
                    color = if (isOwn) Color.White else Color.Black,
                    fontSize = 14.sp
                )
            }
            Text(
                text = timeStr,
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
            )
        }
    }
}

// Data class for chat message
data class ChatMessage(
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: Timestamp? = null
)