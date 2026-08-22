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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.hirelk.WorkerModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


// ============================================================
// COLORS
// ============================================================

private val PrimaryGreen = Color(0xFF1E6030)
private val LightGreen = Color(0xFFE8F5E9)
private val BackgroundColor = Color(0xFFF7F9F8)
private val TextDark = Color(0xFF111827)
private val TextGray = Color(0xFF6B7280)
private val BorderGray = Color(0xFFE5E7EB)


// ============================================================
// WORKER DETAIL SCREEN
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDetailScreen(
    workerId: String,
    onBack: () -> Unit,
    onChatClick: (workerId: String, workerName: String) -> Unit,
    onHireClick: (
        workerId: String,
        workerName: String,
        workerCategory: String,
        workerRate: String
    ) -> Unit
) {

    var worker by remember {
        mutableStateOf<WorkerModel?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(workerId) {

        isLoading = true

        db.collection("users")
            .document(workerId)
            .get()

            .addOnSuccessListener { doc ->

                if (doc.exists()) {

                    worker = WorkerModel(
                        id = doc.id,

                        fullName =
                            doc.getString("fullName")
                                ?: "Worker",

                        serviceCategory =
                            doc.getString("serviceCategory")
                                ?: "Electrician",

                        serviceDistrict =
                            doc.getString("serviceDistrict")
                                ?: "Colombo",

                        rateAmount =
                            doc.getString("rateAmount")
                                ?: "1,800",

                        rateType =
                            doc.getString("rateType")
                                ?: "Per Hour",

                        approvalStatus =
                            doc.getString("approvalStatus")
                                ?: "approved",

                        shortBio =
                            doc.getString("shortBio")
                                ?: "",

                        profileImageUrl =
                            doc.getString("profileImageUrl")
                                ?: ""
                    )
                }

                isLoading = false
            }

            .addOnFailureListener { error ->

                Log.e(
                    "WorkerDetailScreen",
                    "Failed to load worker",
                    error
                )

                isLoading = false
            }
    }

    // ========================================================
    // LOADING
    // ========================================================

    if (isLoading) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            CircularProgressIndicator(
                color = PrimaryGreen,
                strokeWidth = 3.dp
            )
        }

        return
    }

    // ========================================================
    // NOT FOUND
    // ========================================================

    val w = worker ?: run {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Icon(
                    Icons.Default.ChatBubbleOutline,
                    contentDescription = null,
                    tint = BorderGray,
                    modifier = Modifier.size(52.dp)
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Worker not found",
                    color = TextGray,
                    fontSize = 15.sp
                )
            }
        }

        return
    }

    val isApproved =
        w.approvalStatus.equals(
            "approved",
            ignoreCase = true
        )

    Scaffold(

        topBar = {

            Surface(
                color = Color.White,
                shadowElevation = 1.dp
            ) {

                TopAppBar(

                    title = {

                        Text(
                            text = "Worker Profile",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    },

                    navigationIcon = {

                        IconButton(
                            onClick = onBack
                        ) {

                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextDark
                            )
                        }
                    },

                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White
                        )
                )
            }
        },

        containerColor = BackgroundColor

    ) { paddingValues ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(16.dp)
        ) {

            // ====================================================
            // PROFILE HEADER CARD
            // ====================================================

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(22.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor = Color.White
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        // Avatar

                        Box(

                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    LightGreen,
                                    CircleShape
                                ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            val initials =
                                if (w.fullName.isNotEmpty()) {

                                    w.fullName
                                        .trim()
                                        .split(" ")
                                        .take(2)
                                        .mapNotNull {
                                            it.firstOrNull()
                                        }
                                        .joinToString("")
                                        .uppercase()

                                } else {
                                    "W"
                                }

                            Text(
                                text = initials,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(14.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = w.fullName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    "${w.serviceCategory} • ${w.serviceDistrict}",
                                fontSize = 13.sp,
                                color = TextGray
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    // Status

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (isApproved) {
                                        Color(0xFF22C55E)
                                    } else {
                                        Color(0xFFF59E0B)
                                    },
                                    CircleShape
                                )
                        )

                        Spacer(
                            modifier = Modifier.width(7.dp)
                        )

                        Text(
                            text =
                                if (isApproved) {
                                    "Available now"
                                } else {
                                    w.approvalStatus
                                },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color =
                                if (isApproved) {
                                    Color(0xFF15803D)
                                } else {
                                    Color(0xFFD97706)
                                }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // ====================================================
            // ABOUT
            // ====================================================

            SectionTitle(
                title = "About"
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(18.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor = Color.White
                    ),

                border =
                    BorderStroke(
                        1.dp,
                        BorderGray
                    )
            ) {

                Text(
                    text =
                        if (w.shortBio.isNotEmpty()) {

                            w.shortBio

                        } else {

                            "Experienced professional ready to help with your service requirements."
                        },

                    fontSize = 14.sp,

                    lineHeight = 21.sp,

                    color = Color(0xFF374151),

                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // ====================================================
            // PRICING
            // ====================================================

            SectionTitle(
                title = "Pricing & Details"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(18.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor = Color.White
                    ),

                border =
                    BorderStroke(
                        1.dp,
                        BorderGray
                    )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        DetailItem(
                            title = "Rate",
                            value =
                                "LKR ${w.rateAmount}",
                            valueColor = PrimaryGreen
                        )

                        DetailItem(
                            title = "Rate type",
                            value = w.rateType
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        DetailItem(
                            title = "Response",
                            value = "~30 mins"
                        )

                        DetailItem(
                            title = "Working days",
                            value = "Mon – Sat"
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    DetailItem(
                        title = "Working hours",
                        value = "8 AM – 6 PM"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // ====================================================
            // RATINGS
            // ====================================================

            SectionTitle(
                title = "Ratings & Reviews"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(18.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor = Color.White
                    ),

                border =
                    BorderStroke(
                        1.dp,
                        BorderGray
                    )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text = "4.9",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(
                            modifier =
                                Modifier.width(10.dp)
                        )

                        Column {

                            Text(
                                text = "★★★★★",
                                fontSize = 18.sp,
                                color = Color(0xFFF59E0B)
                            )

                            Text(
                                text = "42 reviews",
                                fontSize = 11.sp,
                                color = TextGray
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    RatingBarRow(
                        label = "5",
                        percentage = 0.85f,
                        color = PrimaryGreen
                    )

                    RatingBarRow(
                        label = "4",
                        percentage = 0.10f,
                        color = PrimaryGreen
                    )

                    RatingBarRow(
                        label = "3",
                        percentage = 0.05f,
                        color = PrimaryGreen
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // ====================================================
            // ACTION BUTTONS
            // ====================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                OutlinedButton(

                    onClick = {
                        onChatClick(
                            w.id,
                            w.fullName
                        )
                    },

                    modifier = Modifier.weight(1f),

                    shape =
                        RoundedCornerShape(14.dp),

                    border =
                        BorderStroke(
                            1.2.dp,
                            PrimaryGreen
                        ),

                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White
                        )
                ) {

                    Icon(
                        Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(19.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "Chat",
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(

                    onClick = {
                        onHireClick(
                            w.id,
                            w.fullName,
                            w.serviceCategory,
                            w.rateAmount
                        )
                    },

                    modifier = Modifier.weight(1f),

                    shape =
                        RoundedCornerShape(14.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen
                        )
                ) {

                    Text(
                        text = "Hire Now",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
    }
}


// ============================================================
// SECTION TITLE
// ============================================================

@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark
    )
}


// ============================================================
// DETAIL ITEM
// ============================================================

@Composable
private fun DetailItem(
    title: String,
    value: String,
    valueColor: Color = TextDark
) {

    Column {

        Text(
            text = title,
            fontSize = 11.sp,
            color = TextGray
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}


// ============================================================
// RATING BAR
// ============================================================

@Composable
fun RatingBarRow(
    label: String,
    percentage: Float,
    color: Color
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
    ) {

        Text(
            text = label,
            fontSize = 11.sp,
            color = TextGray,
            modifier = Modifier.width(18.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(
                    RoundedCornerShape(10.dp)
                )
                .background(
                    Color(0xFFE5E7EB)
                )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage)
                    .fillMaxHeight()
                    .background(
                        color,
                        RoundedCornerShape(10.dp)
                    )
            )
        }

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = "${(percentage * 100).toInt()}%",
            fontSize = 10.sp,
            color = TextGray,
            modifier = Modifier.width(34.dp)
        )
    }
}


// ============================================================
// CHAT MESSAGE MODEL
// ============================================================

data class ChatMessage(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: Timestamp? = null
)


// ============================================================
// CHAT SCREEN
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    workerId: String,
    workerName: String,
    onBack: () -> Unit
) {

    val currentUser =
        FirebaseAuth
            .getInstance()
            .currentUser

    val db =
        FirebaseFirestore
            .getInstance()

    val lifecycleOwner =
        LocalLifecycleOwner.current

    // ========================================================
    // CHAT ID
    // ========================================================

    val chatId = remember(
        currentUser?.uid,
        workerId
    ) {

        if (currentUser != null) {

            val ids = listOf(
                currentUser.uid,
                workerId
            ).sorted()

            "${ids[0]}_${ids[1]}"

        } else {
            ""
        }
    }

    // ========================================================
    // STATE
    // ========================================================

    var messages by remember {
        mutableStateOf(
            emptyList<ChatMessage>()
        )
    }

    var inputText by remember {
        mutableStateOf("")
    }

    var isSending by remember {
        mutableStateOf(false)
    }

    val listState =
        rememberLazyListState()

    // ========================================================
    // LOAD MESSAGES
    // ========================================================

    fun attachMessageListener():
            ListenerRegistration? {

        if (chatId.isEmpty()) {
            return null
        }

        Log.d(
            "ChatScreen",
            "Attaching listener for chat: $chatId"
        )

        return db.collection("messages")
            .whereEqualTo(
                "chatId",
                chatId
            )
            .addSnapshotListener { snapshot, error ->

                if (error != null) {

                    Log.e(
                        "ChatScreen",
                        "Firestore listener error",
                        error
                    )

                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    return@addSnapshotListener
                }

                val loadedMessages =
                    snapshot.documents
                        .mapNotNull { document ->

                            try {

                                document
                                    .toObject(
                                        ChatMessage::class.java
                                    )
                                    ?.copy(
                                        id = document.id
                                    )

                            } catch (e: Exception) {

                                Log.e(
                                    "ChatScreen",
                                    "Message parsing error",
                                    e
                                )

                                null
                            }
                        }
                        .sortedBy { message ->

                            message.timestamp
                                ?.toDate()
                                ?.time
                                ?: Long.MIN_VALUE
                        }

                messages = loadedMessages

                Log.d(
                    "ChatScreen",
                    "Messages received: ${loadedMessages.size}"
                )
            }
    }

    // ========================================================
    // REAL-TIME LISTENER
    // ========================================================

    DisposableEffect(chatId) {

        var listener:
                ListenerRegistration? = null

        if (chatId.isNotEmpty()) {
            listener =
                attachMessageListener()
        }

        onDispose {

            Log.d(
                "ChatScreen",
                "Removing listener for chat: $chatId"
            )

            listener?.remove()
            listener = null
        }
    }

    // ========================================================
    // REFRESH WHEN RESUMED
    // ========================================================

    DisposableEffect(
        lifecycleOwner,
        chatId
    ) {

        val observer =
            LifecycleEventObserver { _, event ->

                if (
                    event ==
                    Lifecycle.Event.ON_RESUME
                ) {

                    Log.d(
                        "ChatScreen",
                        "Screen resumed: $chatId"
                    )

                    if (chatId.isNotEmpty()) {

                        db.collection("messages")
                            .whereEqualTo(
                                "chatId",
                                chatId
                            )
                            .get()

                            .addOnSuccessListener {
                                    snapshot ->

                                val refreshedMessages =
                                    snapshot.documents
                                        .mapNotNull { document ->

                                            try {

                                                document
                                                    .toObject(
                                                        ChatMessage::class.java
                                                    )
                                                    ?.copy(
                                                        id =
                                                            document.id
                                                    )

                                            } catch (
                                                e: Exception
                                            ) {

                                                null
                                            }
                                        }
                                        .sortedBy {

                                            it.timestamp
                                                ?.toDate()
                                                ?.time
                                                ?: Long.MIN_VALUE
                                        }

                                messages =
                                    refreshedMessages

                                Log.d(
                                    "ChatScreen",
                                    "Resume refresh: ${refreshedMessages.size} messages"
                                )
                            }

                            .addOnFailureListener { error ->

                                Log.e(
                                    "ChatScreen",
                                    "Resume refresh failed",
                                    error
                                )
                            }
                    }
                }
            }

        lifecycleOwner.lifecycle.addObserver(
            observer
        )

        onDispose {

            lifecycleOwner.lifecycle.removeObserver(
                observer
            )
        }
    }

    // ========================================================
    // AUTO SCROLL
    // ========================================================

    LaunchedEffect(messages.size) {

        if (messages.isNotEmpty()) {

            listState.animateScrollToItem(
                messages.lastIndex
            )
        }
    }

    // ========================================================
    // CHAT UI
    // ========================================================

    Scaffold(

        topBar = {

            Surface(
                color = Color.White,
                shadowElevation = 1.dp
            ) {

                TopAppBar(

                    title = {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            // Avatar

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        LightGreen,
                                        CircleShape
                                    ),
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text =
                                        workerName
                                            .take(2)
                                            .uppercase(),

                                    fontSize = 13.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        PrimaryGreen
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.width(10.dp)
                            )

                            Column {

                                Text(
                                    text = workerName,

                                    fontSize = 16.sp,

                                    fontWeight =
                                        FontWeight.SemiBold,

                                    color = TextDark
                                )

                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Box(
                                        modifier =
                                            Modifier
                                                .size(7.dp)
                                                .background(
                                                    Color(
                                                        0xFF22C55E
                                                    ),
                                                    CircleShape
                                                )
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(5.dp)
                                    )

                                    Text(
                                        text =
                                            "Available to chat",

                                        fontSize = 11.sp,

                                        color = TextGray
                                    )
                                }
                            }
                        }
                    },

                    navigationIcon = {

                        IconButton(
                            onClick = onBack
                        ) {

                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription =
                                    "Back",
                                tint = TextDark
                            )
                        }
                    },

                    colors =
                        TopAppBarDefaults
                            .topAppBarColors(
                                containerColor =
                                    Color.White
                            )
                )
            }
        },

        containerColor =
            BackgroundColor

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // ==================================================
            // MESSAGE LIST
            // ==================================================

            if (messages.isEmpty()) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(20.dp),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        // Chat icon

                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .background(
                                    LightGreen,
                                    CircleShape
                                ),
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier =
                                    Modifier.size(34.dp)
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        Text(
                            text =
                                "Start a conversation",

                            fontSize = 18.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color = TextDark
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Ask $workerName about availability,\npricing or your job.",

                            fontSize = 13.sp,

                            color = TextGray,

                            lineHeight = 19.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(20.dp)
                        )

                        // Quick message buttons

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            AssistChip(

                                onClick = {

                                    inputText =
                                        "Hi, are you available?"
                                },

                                label = {

                                    Text(
                                        "Availability",
                                        fontSize = 11.sp
                                    )
                                },

                                shape =
                                    RoundedCornerShape(20.dp),

                                colors =
                                    AssistChipDefaults
                                        .assistChipColors(
                                            containerColor =
                                                Color.White
                                        )
                            )

                            AssistChip(

                                onClick = {

                                    inputText =
                                        "What is your rate?"
                                },

                                label = {

                                    Text(
                                        "Ask rate",
                                        fontSize = 11.sp
                                    )
                                },

                                shape =
                                    RoundedCornerShape(20.dp),

                                colors =
                                    AssistChipDefaults
                                        .assistChipColors(
                                            containerColor =
                                                Color.White
                                        )
                            )
                        }
                    }
                }

            } else {

                LazyColumn(

                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(
                            horizontal = 10.dp,
                            vertical = 8.dp
                        ),

                    state = listState,

                    reverseLayout = false
                ) {

                    items(

                        items = messages,

                        key = { message ->
                            message.id
                        }

                    ) { message ->

                        val isOwn =
                            message.senderId ==
                                    currentUser?.uid

                        ModernMessageBubble(
                            text = message.text,
                            isOwn = isOwn,
                            timestamp =
                                message.timestamp
                        )
                    }
                }
            }

            // ==================================================
            // INPUT AREA
            // ==================================================

            Surface(

                modifier =
                    Modifier.fillMaxWidth(),

                color = Color.White,

                shadowElevation = 6.dp,

                shape =
                    RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    )

            ) {

                Row(

                    modifier = Modifier
                        .padding(
                            horizontal = 12.dp,
                            vertical = 9.dp
                        )
                        .fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    // ==================================================
                    // TEXT INPUT
                    // ==================================================

                    Box(

                        modifier = Modifier
                            .weight(1f)
                            .clip(
                                RoundedCornerShape(24.dp)
                            )
                            .background(
                                Color(0xFFF4F6F5)
                            )
                            .padding(
                                horizontal = 14.dp,
                                vertical = 3.dp
                            )
                    ) {

                        TextField(

                            value = inputText,

                            onValueChange = {
                                inputText = it
                            },

                            placeholder = {

                                Text(
                                    text =
                                        "Type a message...",
                                    color =
                                        Color(0xFF9CA3AF),
                                    fontSize = 14.sp
                                )
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            colors =
                                TextFieldDefaults.colors(

                                    focusedContainerColor =
                                        Color.Transparent,

                                    unfocusedContainerColor =
                                        Color.Transparent,

                                    disabledContainerColor =
                                        Color.Transparent,

                                    focusedIndicatorColor =
                                        Color.Transparent,

                                    unfocusedIndicatorColor =
                                        Color.Transparent,

                                    disabledIndicatorColor =
                                        Color.Transparent,

                                    focusedTextColor =
                                        TextDark,

                                    unfocusedTextColor =
                                        TextDark
                                ),

                            textStyle =
                                LocalTextStyle.current.copy(
                                    fontSize = 14.sp
                                ),

                            singleLine = true,

                            enabled = !isSending
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    // ==================================================
                    // SEND BUTTON
                    // ==================================================

                    Box(

                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)

                            .background(

                                if (
                                    inputText.isNotBlank() &&
                                    !isSending
                                ) {

                                    PrimaryGreen

                                } else {

                                    Color(0xFFE5E7EB)
                                }
                            )

                            .clickable(

                                enabled =
                                    inputText.isNotBlank() &&
                                            !isSending

                            ) {

                                if (
                                    currentUser == null ||
                                    chatId.isEmpty()
                                ) {

                                    Log.e(
                                        "ChatScreen",
                                        "User not logged in or chatId empty"
                                    )

                                    return@clickable
                                }

                                val textToSend =
                                    inputText.trim()

                                if (
                                    textToSend.isEmpty()
                                ) {

                                    return@clickable
                                }

                                // ==================================================
                                // PREVENT DOUBLE SEND
                                // ==================================================

                                isSending = true

                                // ==================================================
                                // CREATE MESSAGE
                                // ==================================================

                                val message =
                                    ChatMessage(

                                        chatId =
                                            chatId,

                                        senderId =
                                            currentUser.uid,

                                        receiverId =
                                            workerId,

                                        text =
                                            textToSend,

                                        timestamp =
                                            Timestamp.now()
                                    )

                                // ==================================================
                                // SEND TO FIRESTORE
                                // ==================================================

                                db.collection("messages")
                                    .add(message)

                                    .addOnSuccessListener {

                                        Log.d(
                                            "ChatScreen",
                                            "Message sent successfully"
                                        )

                                        inputText = ""

                                        isSending = false
                                    }

                                    .addOnFailureListener { error ->

                                        Log.e(
                                            "ChatScreen",
                                            "Failed to send message",
                                            error
                                        )

                                        isSending = false
                                    }
                            },

                        contentAlignment =
                            Alignment.Center
                    ) {

                        if (isSending) {

                            CircularProgressIndicator(

                                modifier =
                                    Modifier.size(19.dp),

                                strokeWidth = 2.dp,

                                color = Color.White
                            )

                        } else {

                            Icon(

                                Icons.Default.Send,

                                contentDescription =
                                    "Send",

                                tint =
                                    if (
                                        inputText.isNotBlank()
                                    ) {

                                        Color.White

                                    } else {

                                        Color(0xFF9CA3AF)
                                    },

                                modifier =
                                    Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


// ============================================================
// MODERN MESSAGE BUBBLE
// ============================================================

@Composable
fun ModernMessageBubble(
    text: String,
    isOwn: Boolean,
    timestamp: Timestamp?
) {

    val dateFormat =
        remember {

            SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
            )
        }

    val timeStr =
        timestamp?.let {

            dateFormat.format(
                it.toDate()
            )

        } ?: ""

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 3.dp,
                horizontal = 4.dp
            ),

        horizontalArrangement =
            if (isOwn) {

                Arrangement.End

            } else {

                Arrangement.Start
            }
    ) {

        Column(

            horizontalAlignment =
                if (isOwn) {

                    Alignment.End

                } else {

                    Alignment.Start
                },

            modifier =
                Modifier.widthIn(
                    max = 290.dp
                )
        ) {

            // ==================================================
            // MESSAGE
            // ==================================================

            Box(

                modifier = Modifier
                    .background(

                        color =
                            if (isOwn) {

                                PrimaryGreen

                            } else {

                                Color.White
                            },

                        shape =
                            if (isOwn) {

                                RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 18.dp,
                                    bottomStart = 18.dp,
                                    bottomEnd = 5.dp
                                )

                            } else {

                                RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 18.dp,
                                    bottomStart = 5.dp,
                                    bottomEnd = 18.dp
                                )
                            }
                    )

                    .then(

                        if (!isOwn) {

                            Modifier

                        } else {

                            Modifier
                        }
                    )

                    .padding(
                        horizontal = 15.dp,
                        vertical = 11.dp
                    )
            ) {

                Text(

                    text = text,

                    color =
                        if (isOwn) {

                            Color.White

                        } else {

                            Color(0xFF1F2937)
                        },

                    fontSize = 14.sp,

                    lineHeight = 20.sp
                )
            }

            // ==================================================
            // TIME
            // ==================================================

            Text(

                text = timeStr,

                fontSize = 9.sp,

                color =
                    Color(0xFF9CA3AF),

                modifier =
                    Modifier.padding(
                        top = 3.dp,
                        start = 5.dp,
                        end = 5.dp
                    )
            )
        }
    }
}