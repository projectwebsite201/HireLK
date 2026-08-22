package com.example.hirelk

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


// ============================================================
// HIRELK COLORS
// ============================================================

private val PrimaryGreen = Color(0xFF1E6030)
private val DarkGreen = Color(0xFF154724)
private val LightGreen = Color(0xFFE8F5E9)
private val Background = Color(0xFFF7F9F8)
private val TextDark = Color(0xFF111827)
private val TextGray = Color(0xFF6B7280)
private val BorderGray = Color(0xFFE5E7EB)


// ============================================================
// ONBOARDING PAGE MODEL
// ============================================================

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int
)


// ============================================================
// ONBOARDING SCREEN
// ============================================================

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    onSignInClick: () -> Unit
) {

    // ========================================================
    // ONBOARDING PAGES
    // ========================================================

    val pages = listOf(

        OnboardingPage(
            title = "Welcome to HireLK",
            description =
                "Your trusted place to find skilled professionals and reliable services near you.",
            imageRes =
                R.drawable.onboarding_welcome
        ),

        OnboardingPage(
            title = "Find Trusted Experts",
            description =
                "Discover verified technicians, repairmen and skilled service providers around your area.",
            imageRes =
                R.drawable.onboarding_find_worker
        ),

        OnboardingPage(
            title = "Grow Your Business",
            description =
                "Showcase your skills, connect with local customers and grow your earnings with HireLK.",
            imageRes =
                R.drawable.onboarding_worker
        ),

        OnboardingPage(
            title = "Ready to Get Started?",
            description =
                "Join HireLK and make finding or offering reliable services simple and effortless.",
            imageRes =
                R.drawable.onboarding_get_started
        )
    )

    // ========================================================
    // PAGER STATE
    // ========================================================

    val pagerState =
        rememberPagerState(
            pageCount = {
                pages.size
            }
        )

    val coroutineScope =
        rememberCoroutineScope()

    val currentPage =
        pagerState.currentPage

    val isLastPage =
        currentPage == pages.lastIndex


    // ========================================================
    // MAIN SCREEN
    // ========================================================

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(
                    horizontal = 22.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            // ====================================================
            // TOP HEADER
            // ====================================================

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 10.dp
                        ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                // ================================================
                // LOGO
                // ================================================

                Column {

                    Text(
                        text = "HireLK",
                        color = PrimaryGreen,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Services made simple",
                        color = TextGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

                // ================================================
                // PAGE NUMBER
                // ================================================

                Box(
                    modifier =
                        Modifier
                            .background(
                                LightGreen,
                                RoundedCornerShape(
                                    20.dp
                                )
                            )
                            .padding(
                                horizontal = 11.dp,
                                vertical = 6.dp
                            )
                ) {

                    Text(
                        text =
                            "${currentPage + 1}/${pages.size}",

                        color =
                            PrimaryGreen,

                        fontSize =
                            11.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }


            // ====================================================
            // IMAGE + CONTENT PAGER
            // ====================================================

            HorizontalPager(

                state =
                    pagerState,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),

                pageSpacing =
                    16.dp

            ) { page ->

                Column(

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = 2.dp
                            ),

                    horizontalAlignment =
                        Alignment.CenterHorizontally,

                    verticalArrangement =
                        Arrangement.Center
                ) {

                    // ==================================================
                    // IMAGE CONTAINER
                    // ==================================================

                    Box(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(270.dp)
                                .clip(
                                    RoundedCornerShape(
                                        30.dp
                                    )
                                )
                                .background(
                                    LightGreen
                                ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Image(

                            painter =
                                painterResource(
                                    id =
                                        pages[page].imageRes
                                ),

                            contentDescription =
                                pages[page].title,

                            contentScale =
                                ContentScale.Crop,

                            modifier =
                                Modifier.fillMaxSize()
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(28.dp)
                    )


                    // ==================================================
                    // SMALL GREEN LABEL
                    // ==================================================

                    Box(

                        modifier =
                            Modifier
                                .background(
                                    LightGreen,
                                    RoundedCornerShape(
                                        50.dp
                                    )
                                )
                                .padding(
                                    horizontal = 13.dp,
                                    vertical = 6.dp
                                )
                    ) {

                        Text(

                            text =
                                when (page) {

                                    0 -> "WELCOME"

                                    1 -> "DISCOVER"

                                    2 -> "FOR PROFESSIONALS"

                                    else -> "GET STARTED"
                                },

                            color =
                                PrimaryGreen,

                            fontSize =
                                10.sp,

                            fontWeight =
                                FontWeight.Bold,

                            letterSpacing =
                                0.8.sp
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )


                    // ==================================================
                    // TITLE
                    // ==================================================

                    Text(

                        text =
                            pages[page].title,

                        color =
                            TextDark,

                        fontSize =
                            27.sp,

                        fontWeight =
                            FontWeight.Bold,

                        textAlign =
                            TextAlign.Center,

                        lineHeight =
                            34.sp,

                        modifier =
                            Modifier.padding(
                                horizontal = 8.dp
                            )
                    )


                    Spacer(
                        modifier =
                            Modifier.height(11.dp)
                    )


                    // ==================================================
                    // DESCRIPTION
                    // ==================================================

                    Text(

                        text =
                            pages[page].description,

                        color =
                            TextGray,

                        fontSize =
                            14.sp,

                        lineHeight =
                            21.sp,

                        textAlign =
                            TextAlign.Center,

                        modifier =
                            Modifier.padding(
                                horizontal = 18.dp
                            )
                    )
                }
            }


            // ====================================================
            // DOT INDICATOR
            // ====================================================

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(10.dp),

                horizontalArrangement =
                    Arrangement.Center,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                repeat(
                    pages.size
                ) { index ->

                    val isSelected =
                        currentPage == index

                    Box(

                        modifier =
                            Modifier
                                .padding(
                                    horizontal = 3.dp
                                )
                                .height(7.dp)
                                .width(
                                    if (isSelected) {
                                        24.dp
                                    } else {
                                        7.dp
                                    }
                                )
                                .clip(
                                    CircleShape
                                )
                                .background(

                                    if (isSelected) {
                                        PrimaryGreen
                                    } else {
                                        Color(0xFFD1D5DB)
                                    }
                                )
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(22.dp)
            )


            // ====================================================
            // MAIN BUTTON
            // ====================================================

            Button(

                onClick = {

                    if (isLastPage) {

                        onFinished()

                    } else {

                        coroutineScope.launch {

                            pagerState.animateScrollToPage(
                                currentPage + 1
                            )
                        }
                    }
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(54.dp),

                shape =
                    RoundedCornerShape(
                        16.dp
                    ),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            PrimaryGreen
                    ),

                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation =
                            2.dp
                    )
            ) {

                Text(

                    text =
                        if (isLastPage) {
                            "Get Started"
                        } else {
                            "Continue"
                        },

                    color =
                        Color.White,

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Icon(

                    imageVector =
                        if (isLastPage) {
                            Icons.Default.Check
                        } else {
                            Icons.Default.ArrowForward
                        },

                    contentDescription =
                        null,

                    tint =
                        Color.White,

                    modifier =
                        Modifier.size(18.dp)
                )
            }


            // ====================================================
            // SKIP BUTTON
            // ====================================================

            if (!isLastPage) {

                TextButton(

                    onClick =
                        onFinished,

                    modifier =
                        Modifier.height(40.dp)
                ) {

                    Text(

                        text = "Skip",

                        color =
                            TextGray,

                        fontSize =
                            13.sp,

                        fontWeight =
                            FontWeight.Medium
                    )
                }

            } else {

                Spacer(
                    modifier =
                        Modifier.height(40.dp)
                )
            }


            // ====================================================
            // SIGN IN
            // ====================================================

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 12.dp
                        ),

                horizontalArrangement =
                    Arrangement.Center,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(

                    text =
                        "Already have an account? ",

                    color =
                        TextGray,

                    fontSize =
                        13.sp
                )

                TextButton(

                    onClick =
                        onSignInClick,

                    contentPadding =
                        PaddingValues(
                            horizontal = 2.dp,
                            vertical = 0.dp
                        )
                ) {

                    Text(

                        text = "Sign in",

                        color =
                            PrimaryGreen,

                        fontSize =
                            13.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}