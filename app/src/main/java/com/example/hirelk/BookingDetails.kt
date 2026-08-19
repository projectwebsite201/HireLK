package com.example.hirelk

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
private val MainGreen = Color(0xFF13A14F)
private val BorderGreen = Color(0xFF1E6C3B)
private val LightGreenBg = Color(0xFFCBEBD6)
private val TextDark = Color(0xFF0F172A)
private val TextGray = Color(0xFF71717A)
private val CardBg = Color(0xFFFFFFFF)

// ==========================================
// Custom SVG Vector Icons
// ==========================================
private val IconBack: ImageVector
    get() = ImageVector.Builder(
        name = "IconBack",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(TextDark),
            strokeLineWidth = 2.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(15f, 18f)
            lineTo(9f, 12f)
            lineTo(15f, 6f)
        }
    }.build()

private val IconLocation: ImageVector
    get() = ImageVector.Builder(
        name = "IconLocation",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(BorderGreen),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 21.5f)
            curveTo(12f, 21.5f, 19f, 15.5f, 19f, 10f)
            curveTo(19f, 6.13f, 15.87f, 3f, 12f, 3f)
            curveTo(8.13f, 3f, 5f, 6.13f, 5f, 10f)
            curveTo(5f, 15.5f, 12f, 21.5f, 12f, 21.5f)
            close()
            moveTo(12f, 12f)
            curveTo(10.9f, 12f, 10f, 11.1f, 10f, 10f)
            curveTo(10f, 8.9f, 10.9f, 8f, 12f, 8f)
            curveTo(13.1f, 8f, 14f, 8.9f, 14f, 10f)
            curveTo(14f, 11.1f, 13.1f, 12f, 12f, 12f)
            close()
        }
    }.build()

private val IconPhone: ImageVector
    get() = ImageVector.Builder(
        name = "IconPhone",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(BorderGreen),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(22f, 16.92f)
            verticalLineTo(19.92f)
            curveTo(22f, 20.48f, 21.54f, 20.94f, 20.98f, 20.92f)
            curveTo(17.47f, 20.76f, 14.15f, 19.57f, 11.27f, 17.57f)
            curveTo(8.64f, 15.75f, 6.42f, 13.53f, 4.6f, 10.9f)
            curveTo(2.59f, 8.01f, 1.4f, 4.68f, 1.25f, 1.17f)
            curveTo(1.23f, 0.61f, 1.68f, 0.15f, 2.24f, 0.15f)
            horizontalLineTo(5.24f)
            curveTo(5.72f, 0.15f, 6.13f, 0.49f, 6.22f, 0.96f)
            curveTo(6.4f, 1.95f, 6.72f, 2.91f, 7.17f, 3.82f)
            curveTo(7.31f, 4.11f, 7.24f, 4.45f, 7.02f, 4.67f)
            lineTo(5.75f, 5.94f)
            curveTo(7.4f, 8.84f, 9.81f, 11.25f, 12.71f, 12.9f)
            lineTo(13.98f, 11.63f)
            curveTo(14.2f, 11.41f, 14.54f, 11.34f, 14.83f, 11.48f)
            curveTo(15.74f, 11.93f, 16.7f, 12.25f, 17.69f, 12.43f)
            curveTo(18.17f, 12.52f, 18.5f, 12.93f, 18.5f, 13.41f)
            verticalLineTo(16.92f)
            close()
        }
    }.build()

private val IconChat: ImageVector
    get() = ImageVector.Builder(
        name = "IconChat",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(BorderGreen),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(21f, 15f)
            curveTo(21f, 16.1f, 20.1f, 17f, 19f, 17f)
            horizontalLineTo(7f)
            lineTo(3f, 21f)
            verticalLineTo(5f)
            curveTo(3f, 3.9f, 3.9f, 3f, 5f, 3f)
            horizontalLineTo(19f)
            curveTo(20.1f, 3f, 21f, 3.9f, 21f, 5f)
            verticalLineTo(15f)
            close()
        }
    }.build()

// ==========================================
// Complete Screen Component
// ==========================================
@Composable
fun BookingDetailsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color(0xFFF1F5F9)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = IconBack,
                        contentDescription = "Back",
                        tint = TextDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Booking Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        // Scrollable Body Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CLIENT CARD
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
                        color = TextGray,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = LightGreenBg
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "OK",
                                    fontWeight = FontWeight.Bold,
                                    color = BorderGreen,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Oshan Kavinda",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "+94 77 123 4567",
                                color = TextGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // LOCATION CARD
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
                        color = TextGray,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "128/A, Highlevel Road, Maharagama",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderGreen),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = IconLocation,
                            contentDescription = "Map Pin",
                            tint = BorderGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View on Map",
                            color = BorderGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // SCHEDULE CARD
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
                        color = TextGray,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Date", color = TextGray, fontSize = 14.sp)
                        Text(
                            text = "May 18, 2026",
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Time", color = TextGray, fontSize = 14.sp)
                        Text(
                            text = "2:00 PM",
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // PROBLEM CARD
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
                        color = TextGray,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Main breaker trips instantly when the water geyser turns on. Need immediate checkup.\"",
                        color = TextDark,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // CALL & CHAT ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderGreen)
                ) {
                    Icon(
                        imageVector = IconPhone,
                        contentDescription = "Call",
                        tint = BorderGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Call Client",
                        color = BorderGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderGreen)
                ) {
                    Icon(
                        imageVector = IconChat,
                        contentDescription = "Chat",
                        tint = BorderGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Chat",
                        color = BorderGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // MAIN CONFIRMATION BUTTON
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MainGreen)
            ) {
                Text(
                    text = "Confirm Appointment ✓",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}