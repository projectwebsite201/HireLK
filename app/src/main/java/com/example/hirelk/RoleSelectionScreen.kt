package com.example.hirelk

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RoleSelectionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: (String) -> Unit
) {
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    BackHandler {
        onNavigateBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Back Arrow (මෙතැනින් බැක් වුණාම ලොගින් පේජ් එකට යයි)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Select Your Role",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose how you want to use HireLK to get started with your account.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Role Card 1: Worker / Technician
            RoleCard(
                title = "I want to Find Work",
                description = "Create your professional profile, showcase your skills, set your hourly or project rates, and apply for verified job opportunities posted by clients across Sri Lanka.",
                icon = Icons.Default.Work,
                isSelected = selectedRole == "worker",
                onClick = { selectedRole = "worker" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Role Card 2: Client / Employer
            RoleCard(
                title = "I want to Hire Talent",
                description = "Post your job vacancies or daily tasks, review skilled professionals, check ratings and reviews, and hire the best experts to get your work done efficiently.",
                icon = Icons.Default.PersonSearch,
                isSelected = selectedRole == "client",
                onClick = { selectedRole = "client" }
            )

            Spacer(modifier = Modifier.weight(1f))

            val isButtonEnabled = selectedRole != null && !isLoading

            // Continue Button (ඩේටාබේස් එකේ සේව් වීම සිදු වේ)
            Button(
                onClick = {
                    val role = selectedRole ?: return@Button
                    val userId = auth.currentUser?.uid

                    if (userId != null) {
                        isLoading = true
                        val userData = hashMapOf(
                            "userType" to role,
                            "email" to (auth.currentUser?.email ?: "")
                        )

                        db.collection("users").document(userId)
                            .set(userData, com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "Role saved successfully!", Toast.LENGTH_SHORT).show()
                                onNavigateToDashboard(role)
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(context, "User session not found. Please log in again.", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    disabledContainerColor = Color.DarkGray
                ),
                enabled = isButtonEnabled
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                } else {
                    Text(
                        text = "Continue",
                        color = if (isButtonEnabled) Color.Black else Color.LightGray,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color.White else Color.DarkGray
    val backgroundColor = if (isSelected) Color(0xFF1C1C1E) else Color(0xFF121212)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = description,
                color = Color.Gray,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}