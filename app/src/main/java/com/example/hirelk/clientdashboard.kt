package com.example.hirelk

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// ==================== REAL SCREEN IMPORTS ====================
import com.example.hirelk.ui.theme.WorkerDetailScreen
import com.example.hirelk.ui.theme.ChatScreen
import com.example.hirelk.ui.theme.BookServiceScreen
import com.example.hirelk.ui.theme.AboutHireLKScreen
import com.example.hirelk.ui.theme.HelpAndSupportScreen
import com.example.hirelk.ui.theme.SettingsScreen
import com.example.hirelk.ui.theme.ClientEditProfileScreen
import com.example.hirelk.ui.theme.ClientBookingHistoryScreen
import com.example.hirelk.ui.theme.ClientBookingDetailsScreen

// ==================== SEALED CLASS ====================
sealed class ClientScreen {
    object Home : ClientScreen()
    object AllCategories : ClientScreen()
    object Search : ClientScreen()
    object Alerts : ClientScreen()
    object Profile : ClientScreen()
    data class WorkerDetail(val workerId: String) : ClientScreen()
    data class Chat(val workerId: String, val workerName: String) : ClientScreen()
    data class BookService(
        val workerId: String,
        val workerName: String,
        val workerCategory: String,
        val workerRate: String
    ) : ClientScreen()

    // Profile sub-screens
    object EditProfile : ClientScreen()
    object Settings : ClientScreen()
    object About : ClientScreen()
    object Help : ClientScreen()
    object BookingHistory : ClientScreen()
    data class BookingDetails(val bookingId: String) : ClientScreen()
}

// ==================== DATA CLASSES ====================
data class WorkerCategory(
    val name: String,
    val icon: ImageVector
)

data class WorkerModel(
    val id: String = "",
    val fullName: String = "",
    val serviceCategory: String = "",
    val serviceDistrict: String = "",
    val rateAmount: String = "1,000",
    val rateType: String = "Per Hour",
    val approvalStatus: String = "approved",
    val shortBio: String = "",
    val profileImageUrl: String = ""
)

// ==================== MAIN DASHBOARD ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboardScreen(
    onLogout: () -> Unit
) {
    var currentScreen by remember { mutableStateOf<ClientScreen>(ClientScreen.Home) }
    var preselectedCategory by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    var userName by remember { mutableStateOf("Loading...") }
    var userPhone by remember { mutableStateOf("") }
    var userDistrict by remember { mutableStateOf("") }
    var userProfileImage by remember { mutableStateOf("") }

    val primaryGreen = Color(0xFF1E6030)

    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userName = document.getString("fullName") ?: "User"
                        userPhone = document.getString("mobileNumber") ?: ""
                        userDistrict = document.getString("serviceDistrict") ?: "Colombo"
                        userProfileImage = document.getString("profileImageUrl") ?: ""
                    }
                }
        }
    }

    val categoriesList = listOf(
        WorkerCategory("Electrician", Icons.Default.FlashOn),
        WorkerCategory("Plumber", Icons.Default.WaterDrop),
        WorkerCategory("AC Repair", Icons.Default.AcUnit),
        WorkerCategory("Painter", Icons.Default.FormatPaint),
        WorkerCategory("Carpenter", Icons.Default.Chair),
        WorkerCategory("Welder", Icons.Default.Build),
        WorkerCategory("Tiler", Icons.Default.GridOn),
        WorkerCategory("Mason", Icons.Default.Home)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentScreen == ClientScreen.Home,
                    onClick = { currentScreen = ClientScreen.Home },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryGreen,
                        selectedTextColor = primaryGreen,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFFE8F5E9)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    selected = currentScreen == ClientScreen.Search,
                    onClick = { currentScreen = ClientScreen.Search },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryGreen,
                        selectedTextColor = primaryGreen,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFFE8F5E9)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Alerts") },
                    label = { Text("Alerts") },
                    selected = currentScreen == ClientScreen.Alerts,
                    onClick = { currentScreen = ClientScreen.Alerts },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryGreen,
                        selectedTextColor = primaryGreen,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFFE8F5E9)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = currentScreen == ClientScreen.Profile,
                    onClick = { currentScreen = ClientScreen.Profile },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryGreen,
                        selectedTextColor = primaryGreen,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFFE8F5E9)
                    )
                )
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            val screen = currentScreen

            when (screen) {
                ClientScreen.Home -> {
                    ClientHomeScreen(
                        userName = userName,
                        categories = categoriesList,
                        primaryGreen = primaryGreen,
                        onMoreClick = { currentScreen = ClientScreen.AllCategories },
                        onCategoryClick = { categoryName ->
                            preselectedCategory = categoryName
                            currentScreen = ClientScreen.Search
                        },
                        onSearchBarClick = { currentScreen = ClientScreen.Search },
                        onNotificationClick = { currentScreen = ClientScreen.Alerts }
                    )
                }
                ClientScreen.AllCategories -> {
                    AllCategoriesScreen(
                        categories = categoriesList,
                        primaryGreen = primaryGreen,
                        onBackClick = { currentScreen = ClientScreen.Home },
                        onCategoryClick = { categoryName ->
                            preselectedCategory = categoryName
                            currentScreen = ClientScreen.Search
                        }
                    )
                }
                ClientScreen.Search -> {
                    ClientSearchTab(
                        primaryGreen = primaryGreen,
                        onBackClick = {
                            preselectedCategory = null
                            currentScreen = ClientScreen.Home
                        },
                        categoriesList = categoriesList,
                        preselectedCategory = preselectedCategory,
                        onWorkerClick = { worker ->
                            currentScreen = ClientScreen.WorkerDetail(worker.id)
                        }
                    )
                }
                ClientScreen.Alerts -> {
                    ClientAlertsTab(
                        primaryGreen = primaryGreen,
                        onBack = { currentScreen = ClientScreen.Home }
                    )
                }
                ClientScreen.Profile -> {
                    ClientProfileTab(
                        userName = userName,
                        userPhone = userPhone,
                        userDistrict = userDistrict,
                        onLogout = onLogout,
                        primaryGreen = primaryGreen,
                        onEditProfileClick = { currentScreen = ClientScreen.EditProfile },
                        onSettingsClick = { currentScreen = ClientScreen.Settings },
                        onAboutClick = { currentScreen = ClientScreen.About },
                        onHelpClick = { currentScreen = ClientScreen.Help },
                        onBookingHistoryClick = { currentScreen = ClientScreen.BookingHistory }
                    )
                }
                is ClientScreen.WorkerDetail -> {
                    WorkerDetailScreen(
                        workerId = screen.workerId,
                        onBack = { currentScreen = ClientScreen.Search },
                        onChatClick = { workerId, workerName ->
                            currentScreen = ClientScreen.Chat(workerId, workerName)
                        },
                        onHireClick = { workerId, workerName, workerCategory, workerRate ->
                            currentScreen = ClientScreen.BookService(workerId, workerName, workerCategory, workerRate)
                        }
                    )
                }
                is ClientScreen.Chat -> {
                    ChatScreen(
                        workerId = screen.workerId,
                        workerName = screen.workerName,
                        onBack = { currentScreen = ClientScreen.Search }
                    )
                }
                is ClientScreen.BookService -> {
                    BookServiceScreen(
                        workerId = screen.workerId,
                        workerName = screen.workerName,
                        workerCategory = screen.workerCategory,
                        workerRate = screen.workerRate,
                        onBack = { currentScreen = ClientScreen.WorkerDetail(screen.workerId) },
                        onBookingSuccess = { currentScreen = ClientScreen.Home }
                    )
                }
                ClientScreen.About -> {
                    AboutHireLKScreen(
                        onBack = { currentScreen = ClientScreen.Profile }
                    )
                }
                ClientScreen.Help -> {
                    HelpAndSupportScreen(
                        onBack = { currentScreen = ClientScreen.Profile }
                    )
                }
                ClientScreen.EditProfile -> {
                    ClientEditProfileScreen(
                        onBack = { currentScreen = ClientScreen.Profile },
                        onSaveSuccess = { currentScreen = ClientScreen.Profile }
                    )
                }
                ClientScreen.Settings -> {
                    SettingsScreen(
                        onBack = { currentScreen = ClientScreen.Profile }
                    )
                }
                ClientScreen.BookingHistory -> {
                    ClientBookingHistoryScreen(
                        onBack = { currentScreen = ClientScreen.Profile },
                        onBookingClick = { bookingId ->
                            currentScreen = ClientScreen.BookingDetails(bookingId)
                        }
                    )
                }
                is ClientScreen.BookingDetails -> {
                    ClientBookingDetailsScreen(
                        bookingId = screen.bookingId,
                        onBack = { currentScreen = ClientScreen.BookingHistory },
                        onChatClick = { workerId, workerName ->
                            currentScreen = ClientScreen.Chat(workerId, workerName)
                        }
                    )
                }
            }
        }
    }
}

// ==================== PLACEHOLDER SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(
    title: String,
    onBack: () -> Unit,
    primaryGreen: Color
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = Color.Black) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Construction,
                    contentDescription = null,
                    tint = primaryGreen,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$title\n(Coming Soon)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This screen will be implemented soon.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// ==================== HOME SCREEN ====================
@Composable
fun ClientHomeScreen(
    userName: String,
    categories: List<WorkerCategory>,
    primaryGreen: Color,
    onMoreClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onSearchBarClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Good morning 👋", fontSize = 13.sp, color = Color.Gray)
                Text(text = userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { onNotificationClick() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notification", tint = Color.Black, modifier = Modifier.size(20.dp))
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "OK", color = primaryGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search electrician, plumber...", color = Color.Gray, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSearchBarClick() },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = primaryGreen,
                unfocusedBorderColor = Color(0xFFE0E0E0)
            ),
            singleLine = true,
            enabled = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = primaryGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "FEATURED", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Monsoon Season Check", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Certified roofers & drainage specialists — 10% off today", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onSearchBarClick() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = "Book Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Worker Categories", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))

        val homeCategories = categories.take(5)

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                homeCategories.getOrNull(0)?.let {
                    CategoryItemCard(
                        category = it,
                        primaryGreen = primaryGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { onCategoryClick(it.name) }
                    )
                }
                homeCategories.getOrNull(1)?.let {
                    CategoryItemCard(
                        category = it,
                        primaryGreen = primaryGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { onCategoryClick(it.name) }
                    )
                }
                homeCategories.getOrNull(2)?.let {
                    CategoryItemCard(
                        category = it,
                        primaryGreen = primaryGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { onCategoryClick(it.name) }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                homeCategories.getOrNull(3)?.let {
                    CategoryItemCard(
                        category = it,
                        primaryGreen = primaryGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { onCategoryClick(it.name) }
                    )
                }
                homeCategories.getOrNull(4)?.let {
                    CategoryItemCard(
                        category = it,
                        primaryGreen = primaryGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { onCategoryClick(it.name) }
                    )
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier
                        .weight(1f)
                        .height(95.dp)
                        .clickable { onMoreClick() }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = "More", tint = primaryGreen, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "More", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun CategoryItemCard(category: WorkerCategory, primaryGreen: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        modifier = modifier
            .height(95.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(category.icon, contentDescription = category.name, tint = primaryGreen, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = category.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

// ==================== SEARCH TAB ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientSearchTab(
    primaryGreen: Color,
    onBackClick: () -> Unit,
    categoriesList: List<WorkerCategory>,
    preselectedCategory: String? = null,
    onWorkerClick: (WorkerModel) -> Unit
) {
    var allWorkers by remember { mutableStateOf(listOf<WorkerModel>()) }
    var filteredWorkers by remember { mutableStateOf(listOf<WorkerModel>()) }
    var isLoading by remember { mutableStateOf(true) }

    var selectedCategory by remember { mutableStateOf("All Categories") }
    var selectedDistrict by remember { mutableStateOf("Select District") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }

    val categories = listOf("All Categories") + categoriesList.map { it.name }
    val districts = listOf("Select District", "Colombo", "Gampaha", "Kalutara", "Kandy", "Galle", "Matara", "Kurunegala", "Ratnapura")

    val db = FirebaseFirestore.getInstance()
    var hasAppliedPreselected by remember { mutableStateOf(false) }

    fun applyFilters() {
        filteredWorkers = allWorkers.filter { worker ->
            val matchesCategory = selectedCategory == "All Categories" || worker.serviceCategory.equals(selectedCategory, ignoreCase = true)
            val matchesDistrict = selectedDistrict == "Select District" || worker.serviceDistrict.equals(selectedDistrict, ignoreCase = true)
            matchesCategory && matchesDistrict
        }
    }

    LaunchedEffect(Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<WorkerModel>()
                for (doc in result) {
                    val userType = doc.getString("userType") ?: ""
                    val approvalStatus = doc.getString("approvalStatus") ?: ""

                    if (userType.equals("worker", ignoreCase = true)) {
                        val worker = WorkerModel(
                            id = doc.id,
                            fullName = doc.getString("fullName") ?: "Worker",
                            serviceCategory = doc.getString("serviceCategory") ?: "Electrician",
                            serviceDistrict = doc.getString("serviceDistrict") ?: "Colombo",
                            rateAmount = doc.getString("rateAmount") ?: "1,000",
                            rateType = doc.getString("rateType") ?: "Per Hour",
                            approvalStatus = approvalStatus,
                            shortBio = doc.getString("shortBio") ?: "",
                            profileImageUrl = doc.getString("profileImageUrl") ?: ""
                        )
                        list.add(worker)
                    }
                }
                allWorkers = list
                filteredWorkers = list
                isLoading = false

                if (preselectedCategory != null && !hasAppliedPreselected) {
                    selectedCategory = preselectedCategory!!
                    applyFilters()
                    hasAppliedPreselected = true
                }
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    LaunchedEffect(preselectedCategory) {
        if (preselectedCategory != null && !isLoading && !hasAppliedPreselected) {
            selectedCategory = preselectedCategory!!
            applyFilters()
            hasAppliedPreselected = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Find Workers", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Service Type", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryExpanded = false
                                    hasAppliedPreselected = true
                                    applyFilters()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Location", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = districtExpanded,
                    onExpandedChange = { districtExpanded = !districtExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedDistrict,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = districtExpanded,
                        onDismissRequest = { districtExpanded = false }
                    ) {
                        districts.forEach { dist ->
                            DropdownMenuItem(
                                text = { Text(dist) },
                                onClick = {
                                    selectedDistrict = dist
                                    districtExpanded = false
                                    applyFilters()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { applyFilters() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Search Workers", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Active workers near you", fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryGreen)
            }
        } else if (filteredWorkers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No workers found for selected filters.", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredWorkers) { worker ->
                    WorkerCardItem(
                        worker = worker,
                        onClick = { onWorkerClick(worker) }
                    )
                }
            }
        }
    }
}

// ==================== WORKER CARD ====================
@Composable
fun WorkerCardItem(
    worker: WorkerModel,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                            .size(45.dp)
                            .background(Color(0xFFE3F2FD), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = if (worker.fullName.isNotEmpty()) worker.fullName.take(2).uppercase() else "W"
                        Text(text = initials, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0), fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = worker.fullName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "${worker.serviceCategory} • ${worker.serviceDistrict}", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                val isApproved = worker.approvalStatus.equals("approved", ignoreCase = true)
                val badgeBg = if (isApproved) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                val badgeTextCol = if (isApproved) Color(0xFF2E7D32) else Color(0xFFE65100)
                val displayStatus = if (isApproved) "Active" else worker.approvalStatus

                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = displayStatus, color = badgeTextCol, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LKR ${worker.rateAmount} / ${worker.rateType}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E6030)
                )
                if (worker.shortBio.isNotEmpty()) {
                    Text(
                        text = worker.shortBio,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ==================== ALL CATEGORIES ====================
@Composable
fun AllCategoriesScreen(
    categories: List<WorkerCategory>,
    primaryGreen: Color,
    onBackClick: () -> Unit,
    onCategoryClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "All Categories", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(categories) { category ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier
                        .height(110.dp)
                        .fillMaxWidth()
                        .clickable { onCategoryClick(category.name) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(category.icon, contentDescription = category.name, tint = primaryGreen, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = category.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}

// ==================== ALERTS TAB ====================
@Composable
fun ClientAlertsTab(
    primaryGreen: Color,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Notifications", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = primaryGreen,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Notifications Yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "We'll notify you when something important arrives.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// ==================== PROFILE TAB ====================
@Composable
fun ClientProfileTab(
    userName: String,
    userPhone: String,
    userDistrict: String,
    onLogout: () -> Unit,
    primaryGreen: Color,
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onHelpClick: () -> Unit,
    onBookingHistoryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFE8F5E9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = if (userName.isNotEmpty() && userName != "Loading...") {
                        val parts = userName.split(" ")
                        if (parts.size > 1) "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
                        else userName.take(2).uppercase()
                    } else "OK"
                    Text(
                        text = initials,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryGreen
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = userName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "$userPhone • $userDistrict",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Account",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.Default.PersonOutline,
                    title = "Edit Profile",
                    onClick = onEditProfileClick
                )
                Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                ProfileMenuItem(
                    icon = Icons.Default.Bookmark,
                    title = "My Bookings",
                    onClick = onBookingHistoryClick
                )
                Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                ProfileMenuItem(
                    icon = Icons.Default.NotificationsNone,
                    title = "Notifications",
                    onClick = { /* Already in bottom nav */ }
                )
                Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Settings",
                    onClick = onSettingsClick
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Support",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.Default.Info,
                    title = "About HireLK",
                    onClick = onAboutClick
                )
                Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                ProfileMenuItem(
                    icon = Icons.Default.ChatBubbleOutline,
                    title = "Help & Support",
                    onClick = onHelpClick
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Log Out",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F)
            )
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF1E6030),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Forward",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}