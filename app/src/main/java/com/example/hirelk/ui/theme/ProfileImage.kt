package com.example.hirelk.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun ProfileImage(
    imageUrl: String,
    initials: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    if (imageUrl.isNotEmpty()) {
        // URL එක තියෙනවා නම් Coil එකෙන් Image Load කරන්න
        AsyncImage(
            model = imageUrl,
            contentDescription = "Profile Image",
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        // URL එක හිස් නම් Initials පෙන්වන්න
        Box(
            modifier = modifier
                .size(size)
                .background(Color(0xFFE8F5E9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontSize = (size.value / 3).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E6030)
            )
        }
    }
}