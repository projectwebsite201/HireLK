package com.example.hirelk

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// Color Definitions
// ==========================================
private val BgColor = Color(0xFFF8FAFC)
private val CardBg = Color(0xFFFFFFFF)
private val PrimaryGreen = Color(0xFF13A14F)
private val DarkGreenText = Color(0xFF0F5128)
private val TextDark = Color(0xFF0F172A)
private val TextGray = Color(0xFF64748B)
private val LabelGray = Color(0xFF64748B)
private val AvatarBg = Color(0xFFCBEBD6)
private val BorderColor = Color(0xFFE2E8F0)

// ==========================================
// Vector Icons
// ==========================================
private val IconChevronLeft: ImageVector
    get() = ImageVector.Builder(
        name = "ChevronLeft", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(stroke = SolidColor(TextDark), strokeLineWidth = 2.2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(15f, 18f); lineTo(9f, 12f); lineTo(15f, 6f)
        }
    }.build()

private val IconMapPin: ImageVector
    get() = ImageVector.Builder(
        name = "MapPin", defaultWidth = 18.dp, defaultHeight = 18.dp, viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(stroke = SolidColor(DarkGreenText), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 2f); curveTo(8.13f, 2f, 5f, 5.13f, 5f, 9f); curveTo(5f, 14.25f, 12f, 22f, 12f, 22f); curveTo(12f, 22f, 19f, 14.25f, 19f, 9f); curveTo(19f, 5.13f, 15.87f, 2f, 12f, 2f); close()
            moveTo(12f, 11.5f); curveTo(10.62f, 11.5f, 9.5f, 10.38f, 9.5f, 9f); curveTo(9.5f, 7.62f, 10.62f, 6.5f, 12f, 6.5f); curveTo(13.38f, 6.5f, 14.5f, 7.62f, 14.5f, 9f); curveTo(14.5f, 10.38f, 13.38f, 11.5f, 12f, 11.5f); close()
        }
    }.build()

private val IconPhone: ImageVector
    get() = ImageVector.Builder(
        name = "Phone", defaultWidth = 20.dp, defaultHeight = 20.dp, viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(stroke = SolidColor(DarkGreenText), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(22f, 16.92f); verticalLineTo(19.92f); curveTo(22f, 20.48f, 21.53f, 20.94f, 20.97f, 20.92f); curveTo(17.48f, 20.78f, 14.13f, 19.59f, 11.23f, 17.65f); curveTo(8.6f, 15.89f, 6.42f, 13.71f, 4.66f, 11.08f); curveTo(2.72f, 8.18f, 1.53f, 4.82f, 1.39f, 1.33f); curveTo(1.37f, 0.77f, 1.83f, 0.3f, 2.39f, 0.3f); horizontalLineTo(5.39f); curveTo(5.89f, 0.3f, 6.31f, 0.67f, 6.38f, 1.17f); curveTo(6.51f, 2.15f, 6.77f, 3.1f, 7.15f, 4f); curveTo(7.28f, 4.31f, 7.2f, 4.67f, 6.96f, 4.91f); lineTo(5.69f, 6.18f); curveTo(7.28f, 8.97f, 9.53f, 11.22f, 12.32f, 12.81f); lineTo(13.59f, 11.54f); curveTo(13.83f, 11.3f, 14.19f, 11.22f, 14.5f, 11.35f); curveTo(15.4f, 11.73f, 16.35f, 11.99f, 17.33f, 12.12f); curveTo(17.84f, 12.19f, 18.21f, 12.62f, 18.21f, 13.12f); close()
        }
    }.build()

private val IconMessage: ImageVector
    get() = ImageVector.Builder(
        name = "Message", defaultWidth = 20.dp, defaultHeight = 20.dp, viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(stroke = SolidColor(DarkGreenText), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(21f, 15f); curveTo(21f, 16.1f, 20.1f, 17f, 19f, 17f); horizontalLineTo(7f); lineTo(3f, 21f); verticalLineTo(5f); curveTo(3f, 3.9f, 3.9f, 3f, 5f, 3f); horizontalLineTo(19f); curveTo(20.1f, 3f, 21f, 3.9f, 21f, 5f); verticalLineTo(15f); close()
        }
    }.build()

// ==========================================
// Booking Details Screen UI
// ==========================================
@Composable
fun BookingDetailsScreen(
    onBackClick: () -> Unit = {},
    onViewMapClick: () -> Unit = {},
    onCallClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // Top App Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardBg,
            shadowElevation = 0.5.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onBackClick() },
                    shape = CircleShape,
                    color = BgColor
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = IconChevronLeft,
                            contentDescription = "Back",
                            tint = TextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Booking Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
        }

        // Main Content Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Client Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CLIENT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LabelGray,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = AvatarBg
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "OK",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkGreenText
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Oshan Kavinda",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "+94 77 123 4567",
                                fontSize = 13.sp,
                                color = TextGray
                            )
                        }
                    }
                }
            }

            // 2. Location Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LOCATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LabelGray,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "128/A, Highlevel Road, Maharagama",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier
                            .clickable { onViewMapClick() }
                            .border(1.2.dp, DarkGreenText, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = IconMapPin,
                                contentDescription = null,
                                tint = DarkGreenText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "View on Map",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkGreenText
                            )
                        }
                    }
                }
            }

            // 3. Schedule Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SCHEDULE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LabelGray,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Date", fontSize = 14.sp, color = TextGray)
                        Text(
                            text = "May 18, 2026",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Time", fontSize = 14.sp, color = TextGray)
                        Text(
                            text = "2:00 PM",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }
                }
            }

            // 4. Problem Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PROBLEM",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LabelGray,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Main breaker trips instantly when the water geyser turns on. Need immediate checkup.\"",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = TextDark,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 5. Contact Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clickable { onCallClick() }
                        .border(1.2.dp, DarkGreenText, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    color = CardBg
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = IconPhone,
                            contentDescription = null,
                            tint = DarkGreenText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Call Client",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreenText
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clickable { onChatClick() }
                        .border(1.2.dp, DarkGreenText, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    color = CardBg
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = IconMessage,
                            contentDescription = null,
                            tint = DarkGreenText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chat",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreenText
                        )
                    }
                }
            }

            // 6. Action Button
            Button(
                onClick = onConfirmClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text(
                    text = "Confirm Appointment ✓",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}