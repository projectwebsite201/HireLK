package com.example.hirelk

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
import com.example.hirelk.ui.theme.ProfileImage


// ============================================================
// COLORS
// ============================================================

val HireLKGreen = Color(0xFF1E6030)
val HireLKLightGreen = Color(0xFFEAF5EC)
val HireLKBackground = Color(0xFFF7F8F7)
val HireLKText = Color(0xFF151715)
val HireLKMuted = Color(0xFF737873)
val HireLKBorder = Color(0xFFE6E9E6)


// ============================================================
// SEALED CLASS
// ============================================================

sealed class ClientScreen {

    object Home : ClientScreen()

    object AllCategories : ClientScreen()

    object Search : ClientScreen()

    object Alerts : ClientScreen()

    object Profile : ClientScreen()

    data class WorkerDetail(
        val workerId: String
    ) : ClientScreen()

    data class Chat(
        val workerId: String,
        val workerName: String
    ) : ClientScreen()

    data class BookService(
        val workerId: String,
        val workerName: String,
        val workerCategory: String,
        val workerRate: String
    ) : ClientScreen()

    object EditProfile : ClientScreen()

    object Settings : ClientScreen()

    object About : ClientScreen()

    object Help : ClientScreen()

    object BookingHistory : ClientScreen()

    data class BookingDetails(
        val bookingId: String
    ) : ClientScreen()
}


// ============================================================
// DATA CLASSES
// ============================================================

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


// ============================================================
// MAIN DASHBOARD
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboardScreen(
    onLogout: () -> Unit
) {

    var currentScreen by remember {
        mutableStateOf<ClientScreen>(
            ClientScreen.Home
        )
    }

    var preselectedCategory by remember {
        mutableStateOf<String?>(null)
    }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    var userName by remember {
        mutableStateOf("Loading...")
    }

    var userPhone by remember {
        mutableStateOf("")
    }

    var userDistrict by remember {
        mutableStateOf("")
    }

    var userProfileImage by remember {
        mutableStateOf("")
    }

    LaunchedEffect(userId) {

        if (userId != null) {

            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->

                    if (document.exists()) {

                        userName =
                            document.getString("fullName")
                                ?: "User"

                        userPhone =
                            document.getString("mobileNumber")
                                ?: ""

                        userDistrict =
                            document.getString("serviceDistrict")
                                ?: "Colombo"

                        userProfileImage =
                            document.getString("profileImageUrl")
                                ?: ""
                    }
                }
        }
    }


    // ========================================================
    // CATEGORIES
    // ========================================================

    val categoriesList = listOf(

        WorkerCategory(
            "Electrician",
            Icons.Default.FlashOn
        ),

        WorkerCategory(
            "Plumber",
            Icons.Default.WaterDrop
        ),

        WorkerCategory(
            "AC Repair",
            Icons.Default.AcUnit
        ),

        WorkerCategory(
            "Painter",
            Icons.Default.FormatPaint
        ),

        WorkerCategory(
            "Carpenter",
            Icons.Default.Chair
        ),

        WorkerCategory(
            "Welder",
            Icons.Default.Build
        ),

        WorkerCategory(
            "Tiler",
            Icons.Default.GridOn
        ),

        WorkerCategory(
            "Mason",
            Icons.Default.Home
        )
    )


    // ========================================================
    // MAIN SCAFFOLD
    // ========================================================

    Scaffold(

        bottomBar = {

            NavigationBar(

                containerColor =
                    Color.White,

                tonalElevation = 0.dp,

                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color(0xFFF0F1F0)
                    )
            ) {

                NavigationBarItem(

                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },

                    label = {
                        Text(
                            "Home",
                            fontSize = 11.sp
                        )
                    },

                    selected =
                        currentScreen ==
                                ClientScreen.Home,

                    onClick = {
                        currentScreen =
                            ClientScreen.Home
                    },

                    colors =
                        NavigationBarItemDefaults.colors(

                            selectedIconColor =
                                HireLKGreen,

                            selectedTextColor =
                                HireLKGreen,

                            unselectedIconColor =
                                Color(0xFF9AA09B),

                            unselectedTextColor =
                                Color(0xFF9AA09B),

                            indicatorColor =
                                HireLKLightGreen
                        )
                )


                NavigationBarItem(

                    icon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },

                    label = {
                        Text(
                            "Search",
                            fontSize = 11.sp
                        )
                    },

                    selected =
                        currentScreen ==
                                ClientScreen.Search,

                    onClick = {
                        currentScreen =
                            ClientScreen.Search
                    },

                    colors =
                        NavigationBarItemDefaults.colors(

                            selectedIconColor =
                                HireLKGreen,

                            selectedTextColor =
                                HireLKGreen,

                            unselectedIconColor =
                                Color(0xFF9AA09B),

                            unselectedTextColor =
                                Color(0xFF9AA09B),

                            indicatorColor =
                                HireLKLightGreen
                        )
                )


                NavigationBarItem(

                    icon = {
                        Icon(
                            Icons.Default.NotificationsNone,
                            contentDescription =
                                "Alerts"
                        )
                    },

                    label = {
                        Text(
                            "Alerts",
                            fontSize = 11.sp
                        )
                    },

                    selected =
                        currentScreen ==
                                ClientScreen.Alerts,

                    onClick = {
                        currentScreen =
                            ClientScreen.Alerts
                    },

                    colors =
                        NavigationBarItemDefaults.colors(

                            selectedIconColor =
                                HireLKGreen,

                            selectedTextColor =
                                HireLKGreen,

                            unselectedIconColor =
                                Color(0xFF9AA09B),

                            unselectedTextColor =
                                Color(0xFF9AA09B),

                            indicatorColor =
                                HireLKLightGreen
                        )
                )


                NavigationBarItem(

                    icon = {
                        Icon(
                            Icons.Default.PersonOutline,
                            contentDescription =
                                "Profile"
                        )
                    },

                    label = {
                        Text(
                            "Profile",
                            fontSize = 11.sp
                        )
                    },

                    selected =
                        currentScreen ==
                                ClientScreen.Profile,

                    onClick = {
                        currentScreen =
                            ClientScreen.Profile
                    },

                    colors =
                        NavigationBarItemDefaults.colors(

                            selectedIconColor =
                                HireLKGreen,

                            selectedTextColor =
                                HireLKGreen,

                            unselectedIconColor =
                                Color(0xFF9AA09B),

                            unselectedTextColor =
                                Color(0xFF9AA09B),

                            indicatorColor =
                                HireLKLightGreen
                        )
                )
            }
        },

        containerColor =
            HireLKBackground

    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            when (val screen = currentScreen) {

                // ==================================================
                // HOME
                // ==================================================

                ClientScreen.Home -> {

                    ClientHomeScreen(

                        userName = userName,

                        profileImageUrl = userProfileImage,

                        categories =
                            categoriesList,

                        primaryGreen =
                            HireLKGreen,

                        onMoreClick = {

                            currentScreen =
                                ClientScreen.AllCategories
                        },

                        onCategoryClick = { categoryName ->

                            preselectedCategory =
                                categoryName

                            currentScreen =
                                ClientScreen.Search
                        },

                        onSearchBarClick = {

                            currentScreen =
                                ClientScreen.Search
                        },

                        onNotificationClick = {

                            currentScreen =
                                ClientScreen.Alerts
                        }
                    )
                }


                // ==================================================
                // ALL CATEGORIES
                // ==================================================

                ClientScreen.AllCategories -> {

                    AllCategoriesScreen(

                        categories =
                            categoriesList,

                        primaryGreen =
                            HireLKGreen,

                        onBackClick = {

                            currentScreen =
                                ClientScreen.Home
                        },

                        onCategoryClick = { categoryName ->

                            preselectedCategory =
                                categoryName

                            currentScreen =
                                ClientScreen.Search
                        }
                    )
                }


                // ==================================================
                // SEARCH
                // ==================================================

                ClientScreen.Search -> {

                    ClientSearchTab(

                        primaryGreen =
                            HireLKGreen,

                        onBackClick = {

                            preselectedCategory =
                                null

                            currentScreen =
                                ClientScreen.Home
                        },

                        categoriesList =
                            categoriesList,

                        preselectedCategory =
                            preselectedCategory,

                        onWorkerClick = { worker ->

                            currentScreen =
                                ClientScreen.WorkerDetail(
                                    worker.id
                                )
                        }
                    )
                }


                // ==================================================
                // ALERTS
                // ==================================================

                ClientScreen.Alerts -> {

                    ClientAlertsTab(

                        primaryGreen =
                            HireLKGreen,

                        onBack = {

                            currentScreen =
                                ClientScreen.Home
                        }
                    )
                }


                // ==================================================
                // PROFILE
                // ==================================================

                ClientScreen.Profile -> {

                    ClientProfileTab(

                        userName =
                            userName,

                        userPhone =
                            userPhone,

                        userDistrict =
                            userDistrict,

                        profileImageUrl =
                            userProfileImage,

                        onLogout =
                            onLogout,

                        primaryGreen =
                            HireLKGreen,

                        onEditProfileClick = {

                            currentScreen =
                                ClientScreen.EditProfile
                        },

                        onSettingsClick = {

                            currentScreen =
                                ClientScreen.Settings
                        },

                        onAboutClick = {

                            currentScreen =
                                ClientScreen.About
                        },

                        onHelpClick = {

                            currentScreen =
                                ClientScreen.Help
                        },

                        onBookingHistoryClick = {

                            currentScreen =
                                ClientScreen.BookingHistory
                        }
                    )
                }


                // ==================================================
                // WORKER DETAIL
                // ==================================================

                is ClientScreen.WorkerDetail -> {

                    WorkerDetailScreen(

                        workerId =
                            screen.workerId,

                        onBack = {

                            currentScreen =
                                ClientScreen.Search
                        },

                        onChatClick = {
                                workerId,
                                workerName ->

                            currentScreen =
                                ClientScreen.Chat(
                                    workerId,
                                    workerName
                                )
                        },

                        onHireClick = {
                                workerId,
                                workerName,
                                workerCategory,
                                workerRate ->

                            currentScreen =
                                ClientScreen.BookService(
                                    workerId,
                                    workerName,
                                    workerCategory,
                                    workerRate
                                )
                        }
                    )
                }


                // ==================================================
                // CHAT
                // ==================================================

                is ClientScreen.Chat -> {

                    ChatScreen(

                        workerId =
                            screen.workerId,

                        workerName =
                            screen.workerName,

                        onBack = {

                            currentScreen =
                                ClientScreen.Search
                        }
                    )
                }


                // ==================================================
                // BOOK SERVICE
                // ==================================================

                is ClientScreen.BookService -> {

                    BookServiceScreen(

                        workerId =
                            screen.workerId,

                        workerName =
                            screen.workerName,

                        workerCategory =
                            screen.workerCategory,

                        workerRate =
                            screen.workerRate,

                        onBack = {

                            currentScreen =
                                ClientScreen.WorkerDetail(
                                    screen.workerId
                                )
                        },

                        onBookingSuccess = {

                            currentScreen =
                                ClientScreen.Home
                        }
                    )
                }


                // ==================================================
                // ABOUT
                // ==================================================

                ClientScreen.About -> {

                    AboutHireLKScreen(

                        onBack = {

                            currentScreen =
                                ClientScreen.Profile
                        }
                    )
                }


                // ==================================================
                // HELP
                // ==================================================

                ClientScreen.Help -> {

                    HelpAndSupportScreen(

                        onBack = {

                            currentScreen =
                                ClientScreen.Profile
                        }
                    )
                }


                // ==================================================
                // EDIT PROFILE
                // ==================================================

                ClientScreen.EditProfile -> {

                    ClientEditProfileScreen(

                        onBack = {

                            currentScreen =
                                ClientScreen.Profile
                        },

                        onSaveSuccess = {

                            currentScreen =
                                ClientScreen.Profile
                        }
                    )
                }


                // ==================================================
                // SETTINGS
                // ==================================================

                ClientScreen.Settings -> {

                    SettingsScreen(

                        onBack = {

                            currentScreen =
                                ClientScreen.Profile
                        }
                    )
                }


                // ==================================================
                // BOOKING HISTORY
                // ==================================================

                ClientScreen.BookingHistory -> {

                    ClientBookingHistoryScreen(

                        onBack = {

                            currentScreen =
                                ClientScreen.Profile
                        },

                        onBookingClick = { bookingId ->

                            currentScreen =
                                ClientScreen.BookingDetails(
                                    bookingId
                                )
                        }
                    )
                }


                // ==================================================
                // BOOKING DETAILS
                // ==================================================

                is ClientScreen.BookingDetails -> {

                    ClientBookingDetailsScreen(

                        bookingId =
                            screen.bookingId,

                        onBack = {

                            currentScreen =
                                ClientScreen.BookingHistory
                        },

                        onChatClick = {
                                workerId,
                                workerName ->

                            currentScreen =
                                ClientScreen.Chat(
                                    workerId,
                                    workerName
                                )
                        }
                    )
                }
            }
        }
    }
}


// ============================================================
// PREMIUM HOME SCREEN
// ============================================================

@Composable
fun ClientHomeScreen(
    userName: String,
    profileImageUrl: String,
    categories: List<WorkerCategory>,
    primaryGreen: Color,
    onMoreClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onSearchBarClick: () -> Unit,
    onNotificationClick: () -> Unit
) {

    val bannerPages = listOf(

        HomeBanner(
            image = R.drawable.banner_plumber,
            category = "Plumber",
            label = "PLUMBING",
            title = "Fix leaks.\nSkip the stress.",
            subtitle = "Find trusted plumbers near you.",
            button = "Find a Plumber"
        ),

        HomeBanner(
            image = R.drawable.banner_electrician,
            category = "Electrician",
            label = "ELECTRICAL",
            title = "Power problems?\nWe've got you.",
            subtitle = "Connect with skilled electricians.",
            button = "Find an Electrician"
        ),

        HomeBanner(
            image = R.drawable.banner_painter,
            category = "Painter",
            label = "PAINTING",
            title = "Give your space\na fresh look.",
            subtitle = "Hire reliable painters around you.",
            button = "Find a Painter"
        ),

        HomeBanner(
            image = R.drawable.banner_mason,
            category = "Mason",
            label = "MASONRY",
            title = "Build it right.\nBuild it better.",
            subtitle = "Find experienced masons for your project.",
            button = "Find a Mason"
        )
    )


    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = {
                bannerPages.size
            }
        )


    val coroutineScope =
        rememberCoroutineScope()


    // ========================================================
    // AUTO SLIDE
    // ========================================================

    LaunchedEffect(Unit) {

        while (true) {

            delay(4500)

            val nextPage =
                (pagerState.currentPage + 1) %
                        bannerPages.size

            pagerState.animateScrollToPage(
                nextPage
            )
        }
    }


    Column(

        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 16.dp
            )
    ) {


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // ====================================================
        // HEADER
        // ====================================================

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "Good morning 👋",
                    fontSize = 13.sp,
                    color = HireLKMuted
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        if (
                            userName.isNotBlank() &&
                            userName != "Loading..."
                        ) {
                            "Hi, $userName"
                        } else {
                            "Welcome to HireLK"
                        },

                    fontSize = 22.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        HireLKText
                )
            }


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ================================================
                // PROFILE IMAGE
                // ================================================

                ProfileImage(
                    imageUrl = profileImageUrl,
                    initials = if (userName.isNotBlank() && userName != "Loading...") {
                        val parts = userName.split(" ")
                        if (parts.size > 1) {
                            "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
                        } else {
                            userName.take(2).uppercase()
                        }
                    } else {
                        "HK"
                    },
                    size = 44.dp,
                    modifier = Modifier.clickable { onNotificationClick() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                // ================================================
                // NOTIFICATION BUTTON
                // ================================================

                Box(

                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Color.White,
                            CircleShape
                        )
                        .border(
                            1.dp,
                            HireLKBorder,
                            CircleShape
                        )
                        .clickable {
                            onNotificationClick()
                        },

                    contentAlignment =
                        Alignment.Center

                ) {

                    Icon(
                        imageVector =
                            Icons.Default.NotificationsNone,

                        contentDescription =
                            "Notifications",

                        tint =
                            HireLKText,

                        modifier =
                            Modifier.size(21.dp)
                    )


                    // Notification dot

                    Box(

                        modifier = Modifier
                            .size(7.dp)
                            .background(
                                Color(0xFFE53935),
                                CircleShape
                            )
                            .align(
                                Alignment.TopEnd
                            )
                            .offset(
                                x = (-5).dp,
                                y = 5.dp
                            )
                    )
                }
            }
        }


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // ====================================================
        // SEARCH BAR
        // ====================================================

        Card(

            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onSearchBarClick()
                },

            shape =
                RoundedCornerShape(16.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            border =
                BorderStroke(
                    1.dp,
                    HireLKBorder
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
        ) {

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 15.dp,
                        vertical = 13.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Default.Search,
                    contentDescription =
                        "Search",

                    tint =
                        HireLKGreen,

                    modifier =
                        Modifier.size(22.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(11.dp)
                )

                Text(
                    text =
                        "What service do you need?",

                    fontSize =
                        14.sp,

                    color =
                        HireLKMuted,

                    modifier =
                        Modifier.weight(1f)
                )

                Box(

                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            HireLKLightGreen,
                            RoundedCornerShape(10.dp)
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        Icons.Default.Tune,
                        contentDescription =
                            "Filters",

                        tint =
                            HireLKGreen,

                        modifier =
                            Modifier.size(17.dp)
                    )
                }
            }
        }


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // ====================================================
        // SLIDESHOW BANNER
        // ====================================================

        HorizontalPager(

            state = pagerState,

            modifier = Modifier
                .fillMaxWidth()
                .height(205.dp),

            pageSpacing =
                0.dp

        ) { page ->

            val banner =
                bannerPages[page]

            PremiumHomeBanner(
                banner = banner,
                onClick = {

                    onCategoryClick(
                        banner.category
                    )
                }
            )
        }


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // ====================================================
        // PAGE INDICATORS
        // ====================================================

        Row(

            modifier = Modifier
                .fillMaxWidth(),

            horizontalArrangement =
                Arrangement.Center
        ) {

            repeat(
                bannerPages.size
            ) { index ->

                val selected =
                    pagerState.currentPage ==
                            index

                Box(

                    modifier = Modifier
                        .padding(
                            horizontal = 3.dp
                        )
                        .height(5.dp)
                        .width(
                            if (selected) {
                                18.dp
                            } else {
                                5.dp
                            }
                        )
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                        .background(
                            if (selected) {
                                HireLKGreen
                            } else {
                                Color(0xFFD6DCD7)
                            }
                        )
                )
            }
        }


        Spacer(
            modifier = Modifier.height(22.dp)
        )


        // ====================================================
        // QUICK ACTIONS
        // ====================================================

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            PremiumQuickAction(
                icon =
                    Icons.Default.Search,

                title =
                    "Find a\nWorker",

                modifier =
                    Modifier.weight(1f),

                onClick =
                    onSearchBarClick
            )

            PremiumQuickAction(
                icon =
                    Icons.Default.BookmarkBorder,

                title =
                    "My\nBookings",

                modifier =
                    Modifier.weight(1f),

                onClick =
                    onSearchBarClick
            )

            PremiumQuickAction(
                icon =
                    Icons.Default.FavoriteBorder,

                title =
                    "Saved\nWorkers",

                modifier =
                    Modifier.weight(1f),

                onClick =
                    onSearchBarClick
            )
        }


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // ====================================================
        // CATEGORY HEADER
        // ====================================================

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text =
                        "Browse services",

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        HireLKText
                )

                Text(
                    text =
                        "Find the right person for the job",

                    fontSize =
                        12.sp,

                    color =
                        HireLKMuted
                )
            }


            TextButton(
                onClick =
                    onMoreClick
            ) {

                Text(
                    text =
                        "View all",

                    color =
                        HireLKGreen,

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription =
                        null,

                    tint =
                        HireLKGreen,

                    modifier =
                        Modifier.size(17.dp)
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        // ====================================================
        // CATEGORY GRID
        // ====================================================

        val homeCategories =
            categories.take(6)


        Column(
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                homeCategories
                    .getOrNull(0)
                    ?.let {

                        PremiumCategoryCard(
                            category = it,
                            modifier =
                                Modifier.weight(1f),

                            onClick = {
                                onCategoryClick(
                                    it.name
                                )
                            }
                        )
                    }


                homeCategories
                    .getOrNull(1)
                    ?.let {

                        PremiumCategoryCard(
                            category = it,
                            modifier =
                                Modifier.weight(1f),

                            onClick = {
                                onCategoryClick(
                                    it.name
                                )
                            }
                        )
                    }


                homeCategories
                    .getOrNull(2)
                    ?.let {

                        PremiumCategoryCard(
                            category = it,
                            modifier =
                                Modifier.weight(1f),

                            onClick = {
                                onCategoryClick(
                                    it.name
                                )
                            }
                        )
                    }
            }


            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                homeCategories
                    .getOrNull(3)
                    ?.let {

                        PremiumCategoryCard(
                            category = it,
                            modifier =
                                Modifier.weight(1f),

                            onClick = {
                                onCategoryClick(
                                    it.name
                                )
                            }
                        )
                    }


                homeCategories
                    .getOrNull(4)
                    ?.let {

                        PremiumCategoryCard(
                            category = it,
                            modifier =
                                Modifier.weight(1f),

                            onClick = {
                                onCategoryClick(
                                    it.name
                                )
                            }
                        )
                    }


                homeCategories
                    .getOrNull(5)
                    ?.let {

                        PremiumCategoryCard(
                            category = it,
                            modifier =
                                Modifier.weight(1f),

                            onClick = {
                                onCategoryClick(
                                    it.name
                                )
                            }
                        )
                    }
            }
        }


        Spacer(
            modifier =
                Modifier.height(22.dp)
        )


        // ====================================================
        // TRUST STRIP
        // ====================================================

        Card(

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(16.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        HireLKLightGreen
                ),

            elevation =
                CardDefaults.cardElevation(
                    0.dp
                )
        ) {

            Row(

                modifier =
                    Modifier.padding(15.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(

                    modifier =
                        Modifier
                            .size(42.dp)
                            .background(
                                Color.White,
                                CircleShape
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        Icons.Default.VerifiedUser,
                        contentDescription =
                            null,

                        tint =
                            HireLKGreen,

                        modifier =
                            Modifier.size(22.dp)
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(12.dp)
                )


                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Hire with confidence",

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            HireLKText
                    )

                    Spacer(
                        modifier =
                            Modifier.height(2.dp)
                    )

                    Text(
                        text =
                            "Verified workers • Clear pricing • Easy booking",

                        fontSize =
                            11.sp,

                        color =
                            HireLKMuted
                    )
                }


                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription =
                        null,

                    tint =
                        HireLKGreen,

                    modifier =
                        Modifier.size(20.dp)
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(28.dp)
        )


        // Bottom spacing

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )
    }
}


// ============================================================
// HOME BANNER DATA
// ============================================================

data class HomeBanner(

    val image: Int,

    val category: String,

    val label: String,

    val title: String,

    val subtitle: String,

    val button: String
)


// ============================================================
// PREMIUM BANNER
// ============================================================

@Composable
fun PremiumHomeBanner(

    banner: HomeBanner,

    onClick: () -> Unit

) {

    Card(

        modifier = Modifier
            .fillMaxSize()
            .clickable {
                onClick()
            },

        shape =
            RoundedCornerShape(22.dp),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
    ) {

        Box(
            modifier =
                Modifier.fillMaxSize()
        ) {

            Image(

                painter =
                    painterResource(
                        id = banner.image
                    ),

                contentDescription =
                    banner.category,

                modifier =
                    Modifier.fillMaxSize(),

                contentScale =
                    ContentScale.Crop
            )


            // ==================================================
            // DARK GRADIENT
            // ==================================================

            Box(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(

                            Brush.horizontalGradient(

                                colors =
                                    listOf(

                                        Color.Black.copy(
                                            alpha = 0.78f
                                        ),

                                        Color.Black.copy(
                                            alpha = 0.42f
                                        ),

                                        Color.Transparent
                                    )
                            )
                        )
            )


            Column(

                modifier =
                    Modifier
                        .align(
                            Alignment.CenterStart
                        )
                        .padding(20.dp)

            ) {

                // Label

                Box(

                    modifier =
                        Modifier
                            .background(
                                Color.White.copy(
                                    alpha = 0.16f
                                ),
                                RoundedCornerShape(
                                    20.dp
                                )
                            )
                            .padding(
                                horizontal = 9.dp,
                                vertical = 5.dp
                            )
                ) {

                    Text(

                        text =
                            banner.label,

                        fontSize =
                            9.sp,

                        letterSpacing =
                            1.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color.White
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(9.dp)
                )


                Text(

                    text =
                        banner.title,

                    fontSize =
                        22.sp,

                    lineHeight =
                        25.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color.White
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(

                    text =
                        banner.subtitle,

                    fontSize =
                        11.sp,

                    color =
                        Color.White.copy(
                            alpha = 0.82f
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp
                        )
                )


                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(

                        text =
                            banner.button,

                        fontSize =
                            11.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.width(5.dp)
                    )

                    Icon(

                        Icons.Default.ArrowForward,

                        contentDescription =
                            null,

                        tint =
                            Color.White,

                        modifier =
                            Modifier.size(15.dp)
                    )
                }
            }


            // Premium badge

            Box(

                modifier =
                    Modifier
                        .align(
                            Alignment.TopEnd
                        )
                        .padding(14.dp)
                        .background(
                            Color.White.copy(
                                alpha = 0.16f
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        )
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription =
                            null,

                        tint =
                            Color.White,

                        modifier =
                            Modifier.size(12.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(4.dp)
                    )

                    Text(
                        text =
                            "HireLK",

                        fontSize =
                            9.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color.White
                    )
                }
            }
        }
    }
}


// ============================================================
// QUICK ACTION
// ============================================================

@Composable
fun PremiumQuickAction(

    icon: ImageVector,

    title: String,

    modifier: Modifier = Modifier,

    onClick: () -> Unit

) {

    Card(

        modifier =
            modifier
                .height(76.dp)
                .clickable {
                    onClick()
                },

        shape =
            RoundedCornerShape(15.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        border =
            BorderStroke(
                1.dp,
                HireLKBorder
            ),

        elevation =
            CardDefaults.cardElevation(
                0.dp
            )
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(10.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(

                modifier =
                    Modifier
                        .size(35.dp)
                        .background(
                            HireLKLightGreen,
                            RoundedCornerShape(
                                10.dp
                            )
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector =
                        icon,

                    contentDescription =
                        null,

                    tint =
                        HireLKGreen,

                    modifier =
                        Modifier.size(18.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )


            Text(

                text =
                    title,

                fontSize =
                    11.sp,

                lineHeight =
                    14.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    HireLKText
            )
        }
    }
}


// ============================================================
// PREMIUM CATEGORY CARD
// ============================================================

@Composable
fun PremiumCategoryCard(

    category: WorkerCategory,

    modifier: Modifier = Modifier,

    onClick: () -> Unit

) {

    Card(

        modifier =
            modifier
                .height(92.dp)
                .clickable {
                    onClick()
                },

        shape =
            RoundedCornerShape(15.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        border =
            BorderStroke(
                1.dp,
                HireLKBorder
            ),

        elevation =
            CardDefaults.cardElevation(
                0.dp
            )
    ) {

        Column(

            modifier =
                Modifier.fillMaxSize(),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Box(

                modifier =
                    Modifier
                        .size(37.dp)
                        .background(
                            HireLKLightGreen,
                            RoundedCornerShape(
                                11.dp
                            )
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector =
                        category.icon,

                    contentDescription =
                        category.name,

                    tint =
                        HireLKGreen,

                    modifier =
                        Modifier.size(20.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )


            Text(

                text =
                    category.name,

                fontSize =
                    11.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    HireLKText,

                maxLines = 1
            )
        }
    }
}


// ============================================================
// ALL CATEGORIES
// ============================================================

@Composable
fun AllCategoriesScreen(
    categories: List<WorkerCategory>,
    primaryGreen: Color,
    onBackClick: () -> Unit,
    onCategoryClick: (String) -> Unit
) {

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick =
                    onBackClick
            ) {

                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription =
                        "Back",
                    tint =
                        Color.Black
                )
            }


            Spacer(
                modifier =
                    Modifier.width(4.dp)
            )


            Text(
                text =
                    "All Services",

                fontSize =
                    21.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    HireLKText
            )
        }


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        LazyVerticalGrid(

            columns =
                GridCells.Fixed(2),

            horizontalArrangement =
                Arrangement.spacedBy(12.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp),

            modifier =
                Modifier.fillMaxSize()
        ) {

            items(
                categories
            ) { category ->

                Card(

                    shape =
                        RoundedCornerShape(16.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        ),

                    border =
                        BorderStroke(
                            1.dp,
                            HireLKBorder
                        ),

                    modifier =
                        Modifier
                            .height(115.dp)
                            .fillMaxWidth()
                            .clickable {
                                onCategoryClick(
                                    category.name
                                )
                            }
                ) {

                    Column(

                        modifier =
                            Modifier.fillMaxSize(),

                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.Center
                    ) {

                        Box(

                            modifier =
                                Modifier
                                    .size(42.dp)
                                    .background(
                                        HireLKLightGreen,
                                        RoundedCornerShape(
                                            12.dp
                                        )
                                    ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                category.icon,
                                contentDescription =
                                    category.name,

                                tint =
                                    primaryGreen,

                                modifier =
                                    Modifier.size(24.dp)
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(9.dp)
                        )


                        Text(
                            text =
                                category.name,

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                HireLKText
                        )
                    }
                }
            }
        }
    }
}


// ============================================================
// SEARCH TAB
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientSearchTab(
    primaryGreen: Color,
    onBackClick: () -> Unit,
    categoriesList: List<WorkerCategory>,
    preselectedCategory: String? = null,
    onWorkerClick: (WorkerModel) -> Unit
) {

    var allWorkers by remember {
        mutableStateOf(
            listOf<WorkerModel>()
        )
    }

    var filteredWorkers by remember {
        mutableStateOf(
            listOf<WorkerModel>()
        )
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var selectedCategory by remember {
        mutableStateOf("All Categories")
    }

    var selectedDistrict by remember {
        mutableStateOf("Select District")
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    var districtExpanded by remember {
        mutableStateOf(false)
    }

    val categories =
        listOf("All Categories") +
                categoriesList.map {
                    it.name
                }

    val districts =
        listOf(
            "Select District",
            "Colombo",
            "Gampaha",
            "Kalutara",
            "Kandy",
            "Galle",
            "Matara",
            "Kurunegala",
            "Ratnapura"
        )

    val db =
        FirebaseFirestore.getInstance()

    var hasAppliedPreselected by remember {
        mutableStateOf(false)
    }


    fun applyFilters() {

        filteredWorkers =
            allWorkers.filter { worker ->

                val matchesCategory =
                    selectedCategory ==
                            "All Categories" ||
                            worker.serviceCategory
                                .equals(
                                    selectedCategory,
                                    ignoreCase = true
                                )

                val matchesDistrict =
                    selectedDistrict ==
                            "Select District" ||
                            worker.serviceDistrict
                                .equals(
                                    selectedDistrict,
                                    ignoreCase = true
                                )

                matchesCategory &&
                        matchesDistrict
            }
    }


    LaunchedEffect(Unit) {

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->

                val list =
                    mutableListOf<WorkerModel>()

                for (doc in result) {

                    val userType =
                        doc.getString(
                            "userType"
                        ) ?: ""

                    val approvalStatus =
                        doc.getString(
                            "approvalStatus"
                        ) ?: ""

                    if (
                        userType.equals(
                            "worker",
                            ignoreCase = true
                        )
                    ) {

                        list.add(

                            WorkerModel(

                                id =
                                    doc.id,

                                fullName =
                                    doc.getString(
                                        "fullName"
                                    ) ?: "Worker",

                                serviceCategory =
                                    doc.getString(
                                        "serviceCategory"
                                    ) ?: "Electrician",

                                serviceDistrict =
                                    doc.getString(
                                        "serviceDistrict"
                                    ) ?: "Colombo",

                                rateAmount =
                                    doc.getString(
                                        "rateAmount"
                                    ) ?: "1,000",

                                rateType =
                                    doc.getString(
                                        "rateType"
                                    ) ?: "Per Hour",

                                approvalStatus =
                                    approvalStatus,

                                shortBio =
                                    doc.getString(
                                        "shortBio"
                                    ) ?: "",

                                profileImageUrl =
                                    doc.getString(
                                        "profileImageUrl"
                                    ) ?: ""
                            )
                        )
                    }
                }


                allWorkers =
                    list

                filteredWorkers =
                    list

                isLoading =
                    false


                if (
                    preselectedCategory != null &&
                    !hasAppliedPreselected
                ) {

                    selectedCategory =
                        preselectedCategory

                    applyFilters()

                    hasAppliedPreselected =
                        true
                }
            }

            .addOnFailureListener {

                isLoading =
                    false
            }
    }


    LaunchedEffect(
        preselectedCategory
    ) {

        if (
            preselectedCategory != null &&
            !isLoading &&
            !hasAppliedPreselected
        ) {

            selectedCategory =
                preselectedCategory

            applyFilters()

            hasAppliedPreselected =
                true
        }
    }


    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick =
                    onBackClick
            ) {

                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription =
                        "Back",

                    tint =
                        Color.Black
                )
            }


            Spacer(
                modifier =
                    Modifier.width(4.dp)
            )


            Text(
                text =
                    "Find Workers",

                fontSize =
                    21.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    HireLKText
            )
        }


        Spacer(
            modifier =
                Modifier.height(16.dp)
        )


        Card(

            shape =
                RoundedCornerShape(18.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            border =
                BorderStroke(
                    1.dp,
                    HireLKBorder
                ),

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Column(
                modifier =
                    Modifier.padding(16.dp)
            ) {

                Text(
                    text =
                        "Service",

                    fontSize =
                        12.sp,

                    color =
                        HireLKMuted
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                ExposedDropdownMenuBox(

                    expanded =
                        categoryExpanded,

                    onExpandedChange = {
                        categoryExpanded =
                            !categoryExpanded
                    }
                ) {

                    OutlinedTextField(

                        value =
                            selectedCategory,

                        onValueChange = {},

                        readOnly =
                            true,

                        trailingIcon = {

                            ExposedDropdownMenuDefaults
                                .TrailingIcon(
                                    expanded =
                                        categoryExpanded
                                )
                        },

                        modifier =
                            Modifier
                                .menuAnchor()
                                .fillMaxWidth(),

                        shape =
                            RoundedCornerShape(12.dp),

                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    primaryGreen,

                                unfocusedBorderColor =
                                    HireLKBorder
                            )
                    )


                    ExposedDropdownMenu(

                        expanded =
                            categoryExpanded,

                        onDismissRequest = {
                            categoryExpanded =
                                false
                        }
                    ) {

                        categories.forEach { cat ->

                            DropdownMenuItem(

                                text = {
                                    Text(cat)
                                },

                                onClick = {

                                    selectedCategory =
                                        cat

                                    categoryExpanded =
                                        false

                                    hasAppliedPreselected =
                                        true

                                    applyFilters()
                                }
                            )
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                Text(
                    text =
                        "Location",

                    fontSize =
                        12.sp,

                    color =
                        HireLKMuted
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                ExposedDropdownMenuBox(

                    expanded =
                        districtExpanded,

                    onExpandedChange = {
                        districtExpanded =
                            !districtExpanded
                    }
                ) {

                    OutlinedTextField(

                        value =
                            selectedDistrict,

                        onValueChange = {},

                        readOnly =
                            true,

                        trailingIcon = {

                            ExposedDropdownMenuDefaults
                                .TrailingIcon(
                                    expanded =
                                        districtExpanded
                                )
                        },

                        modifier =
                            Modifier
                                .menuAnchor()
                                .fillMaxWidth(),

                        shape =
                            RoundedCornerShape(12.dp),

                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    primaryGreen,

                                unfocusedBorderColor =
                                    HireLKBorder
                            )
                    )


                    ExposedDropdownMenu(

                        expanded =
                            districtExpanded,

                        onDismissRequest = {
                            districtExpanded =
                                false
                        }
                    ) {

                        districts.forEach { dist ->

                            DropdownMenuItem(

                                text = {
                                    Text(dist)
                                },

                                onClick = {

                                    selectedDistrict =
                                        dist

                                    districtExpanded =
                                        false

                                    applyFilters()
                                }
                            )
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(15.dp)
                )


                Button(

                    onClick =
                        ::applyFilters,

                    shape =
                        RoundedCornerShape(12.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                primaryGreen
                        ),

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                ) {

                    Icon(
                        Icons.Default.Search,
                        contentDescription =
                            null,

                        tint =
                            Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text =
                            "Search Workers",

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(16.dp)
        )


        Text(
            text =
                "${filteredWorkers.size} workers available",

            fontSize =
                12.sp,

            color =
                HireLKMuted
        )


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        if (isLoading) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator(
                    color =
                        primaryGreen
                )
            }

        } else if (
            filteredWorkers.isEmpty()
        ) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(
                        Icons.Default.SearchOff,
                        contentDescription =
                            null,

                        tint =
                            HireLKGreen,

                        modifier =
                            Modifier.size(48.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Text(
                        text =
                            "No workers found",

                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            "Try changing your filters",

                        color =
                            HireLKMuted,

                        fontSize =
                            13.sp
                    )
                }
            }

        } else {

            LazyColumn(

                verticalArrangement =
                    Arrangement.spacedBy(10.dp),

                modifier =
                    Modifier.fillMaxSize()
            ) {

                items(
                    filteredWorkers
                ) { worker ->

                    WorkerCardItem(

                        worker =
                            worker,

                        onClick = {
                            onWorkerClick(
                                worker
                            )
                        }
                    )
                }
            }
        }
    }
}


// ============================================================
// WORKER CARD
// ============================================================

@Composable
fun WorkerCardItem(
    worker: WorkerModel,
    onClick: () -> Unit
) {

    Card(

        shape =
            RoundedCornerShape(17.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        border =
            BorderStroke(
                1.dp,
                HireLKBorder
            ),

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
    ) {

        Column(
            modifier =
                Modifier.padding(15.dp)
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    // ================================================
                    // PROFILE IMAGE - WORKER
                    // ================================================

                    ProfileImage(
                        imageUrl = worker.profileImageUrl,
                        initials = if (worker.fullName.isNotEmpty()) {
                            worker.fullName.take(2).uppercase()
                        } else {
                            "W"
                        },
                        size = 46.dp
                    )


                    Spacer(
                        modifier =
                            Modifier.width(12.dp)
                    )


                    Column {

                        Text(

                            text =
                                worker.fullName,

                            fontSize =
                                15.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                HireLKText
                        )


                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )


                        Text(

                            text =
                                "${worker.serviceCategory} • ${worker.serviceDistrict}",

                            fontSize =
                                12.sp,

                            color =
                                HireLKMuted
                        )
                    }
                }


                Box(

                    modifier =
                        Modifier
                            .background(
                                HireLKLightGreen,
                                RoundedCornerShape(
                                    20.dp
                                )
                            )
                            .padding(
                                horizontal = 9.dp,
                                vertical = 5.dp
                            )
                ) {

                    Text(

                        text =
                            "Active",

                        fontSize =
                            10.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            HireLKGreen
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            HorizontalDivider(
                color =
                    Color(0xFFF0F1F0)
            )


            Spacer(
                modifier =
                    Modifier.height(11.dp)
            )


            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(

                    text =
                        "LKR ${worker.rateAmount} / ${worker.rateType}",

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        HireLKGreen
                )


                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            "★",

                        color =
                            Color(0xFFF4B400),

                        fontSize =
                            14.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.width(3.dp)
                    )

                    Text(
                        text =
                            "4.9",

                        fontSize =
                            12.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}


// ============================================================
// ALERTS
// ============================================================

@Composable
fun ClientAlertsTab(
    primaryGreen: Color,
    onBack: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick =
                    onBack
            ) {

                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription =
                        "Back",

                    tint =
                        Color.Black
                )
            }


            Text(
                text =
                    "Notifications",

                fontSize =
                    21.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    HireLKText
            )
        }


        Box(

            modifier =
                Modifier.fillMaxSize(),

            contentAlignment =
                Alignment.Center
        ) {

            Column(

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Box(

                    modifier =
                        Modifier
                            .size(72.dp)
                            .background(
                                HireLKLightGreen,
                                CircleShape
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        Icons.Default.NotificationsNone,
                        contentDescription =
                            null,

                        tint =
                            primaryGreen,

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
                        "You're all caught up",

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )


                Text(
                    text =
                        "New booking updates and offers will appear here.",

                    fontSize =
                        13.sp,

                    color =
                        HireLKMuted,

                    textAlign =
                        TextAlign.Center
                )
            }
        }
    }
}


// ============================================================
// PROFILE
// ============================================================

@Composable
fun ClientProfileTab(
    userName: String,
    userPhone: String,
    userDistrict: String,
    profileImageUrl: String,
    onLogout: () -> Unit,
    primaryGreen: Color,
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onHelpClick: () -> Unit,
    onBookingHistoryClick: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(16.dp)
    ) {

        Text(
            text =
                "Profile",

            fontSize =
                24.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                HireLKText
        )


        Spacer(
            modifier =
                Modifier.height(16.dp)
        )


        Card(

            shape =
                RoundedCornerShape(20.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            border =
                BorderStroke(
                    1.dp,
                    HireLKBorder
                ),

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Row(

                modifier =
                    Modifier.padding(18.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                // ================================================
                // PROFILE IMAGE - CLIENT
                // ================================================

                ProfileImage(
                    imageUrl = profileImageUrl,
                    initials = if (userName.isNotEmpty() && userName != "Loading...") {
                        val parts = userName.split(" ")
                        if (parts.size > 1) {
                            "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
                        } else {
                            userName.take(2).uppercase()
                        }
                    } else {
                        "HK"
                    },
                    size = 58.dp
                )


                Spacer(
                    modifier =
                        Modifier.width(14.dp)
                )


                Column {

                    Text(
                        text =
                            userName,

                        fontSize =
                            18.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            HireLKText
                    )


                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )


                    Text(
                        text =
                            "$userPhone • $userDistrict",

                        fontSize =
                            12.sp,

                        color =
                            HireLKMuted
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(24.dp)
        )


        Text(
            text =
                "Account",

            fontSize =
                14.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                HireLKMuted
        )


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        Card(

            shape =
                RoundedCornerShape(17.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            border =
                BorderStroke(
                    1.dp,
                    HireLKBorder
                ),

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Column {

                ProfileMenuItem(
                    icon =
                        Icons.Default.PersonOutline,

                    title =
                        "Edit Profile",

                    onClick =
                        onEditProfileClick
                )


                HorizontalDivider(
                    color =
                        Color(0xFFF1F2F1)
                )


                ProfileMenuItem(
                    icon =
                        Icons.Default.BookmarkBorder,

                    title =
                        "My Bookings",

                    onClick =
                        onBookingHistoryClick
                )


                HorizontalDivider(
                    color =
                        Color(0xFFF1F2F1)
                )


                ProfileMenuItem(
                    icon =
                        Icons.Default.Settings,

                    title =
                        "Settings",

                    onClick =
                        onSettingsClick
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(22.dp)
        )


        Text(
            text =
                "Support",

            fontSize =
                14.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                HireLKMuted
        )


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        Card(

            shape =
                RoundedCornerShape(17.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            border =
                BorderStroke(
                    1.dp,
                    HireLKBorder
                ),

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Column {

                ProfileMenuItem(
                    icon =
                        Icons.Default.Info,

                    title =
                        "About HireLK",

                    onClick =
                        onAboutClick
                )


                HorizontalDivider(
                    color =
                        Color(0xFFF1F2F1)
                )


                ProfileMenuItem(
                    icon =
                        Icons.Default.HelpOutline,

                    title =
                        "Help & Support",

                    onClick =
                        onHelpClick
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(28.dp)
        )


        OutlinedButton(

            onClick = {

                FirebaseAuth
                    .getInstance()
                    .signOut()

                onLogout()
            },

            shape =
                RoundedCornerShape(13.dp),

            border =
                BorderStroke(
                    1.dp,
                    Color(0xFFFFCDD2)
                ),

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
        ) {

            Icon(
                Icons.Default.Logout,
                contentDescription =
                    null,

                tint =
                    Color(0xFFD32F2F)
            )


            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )


            Text(
                text =
                    "Log Out",

                color =
                    Color(0xFFD32F2F),

                fontWeight =
                    FontWeight.Bold
            )
        }


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )
    }
}


// ============================================================
// PROFILE MENU ITEM
// ============================================================

@Composable
fun ProfileMenuItem(

    icon: ImageVector,

    title: String,

    onClick: () -> Unit

) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = 16.dp,
                    vertical = 15.dp
                ),

        horizontalArrangement =
            Arrangement.SpaceBetween,

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(

                modifier =
                    Modifier
                        .size(36.dp)
                        .background(
                            HireLKLightGreen,
                            RoundedCornerShape(
                                10.dp
                            )
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector =
                        icon,

                    contentDescription =
                        title,

                    tint =
                        HireLKGreen,

                    modifier =
                        Modifier.size(19.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.width(13.dp)
            )


            Text(

                text =
                    title,

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.Medium,

                color =
                    HireLKText
            )
        }


        Icon(

            imageVector =
                Icons.Default.ChevronRight,

            contentDescription =
                "Open",

            tint =
                Color(0xFF9AA09B),

            modifier =
                Modifier.size(19.dp)
        )
    }
}