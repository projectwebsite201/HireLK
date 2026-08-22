package com.example.hirelk

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


// ============================================================
// COLORS
// ============================================================

private val PrimaryGreen = Color(0xFF1E6030)
private val DarkGreen = Color(0xFF154724)
private val LightGreen = Color(0xFFE8F5E9)
private val Background = Color(0xFFF7F9F8)
private val TextDark = Color(0xFF111827)
private val TextGray = Color(0xFF6B7280)
private val BorderGray = Color(0xFFE5E7EB)


// ============================================================
// LOGIN SCREEN
// ============================================================

@Composable
fun LoginScreen(
    onBackToOnboarding: () -> Unit,
    onNavigateToRoleSelection: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {

    // ========================================================
    // STATE
    // ========================================================

    var isSignUp by remember {
        mutableStateOf(false)
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    // ========================================================
    // FIREBASE
    // ========================================================

    val context = LocalContext.current

    val auth =
        FirebaseAuth.getInstance()

    val db =
        FirebaseFirestore.getInstance()

    val coroutineScope =
        rememberCoroutineScope()

    // ========================================================
    // BACK
    // ========================================================

    BackHandler {
        onBackToOnboarding()
    }

    // ========================================================
    // MAIN UI
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
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 22.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // ====================================================
            // TOP IMAGE
            // ====================================================

            Box(
                modifier = Modifier
                    .size(
                        width = 150.dp,
                        height = 130.dp
                    )
                    .clip(
                        RoundedCornerShape(28.dp)
                    )
                    .background(
                        LightGreen
                    ),
                contentAlignment =
                    Alignment.Center
            ) {

                androidx.compose.foundation.Image(

                    painter =
                        painterResource(
                            id = R.drawable.worker_img
                        ),

                    contentDescription =
                        "HireLK Worker",

                    contentScale =
                        ContentScale.Crop,

                    modifier =
                        Modifier.fillMaxSize()
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // ====================================================
            // APP NAME
            // ====================================================

            Text(
                text = "HireLK",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            // ====================================================
            // TITLE
            // ====================================================

            Text(
                text =
                    if (isSignUp) {
                        "Create your account"
                    } else {
                        "Welcome back"
                    },

                fontSize = 23.sp,

                fontWeight =
                    FontWeight.Bold,

                color = TextDark,

                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    if (isSignUp) {
                        "Join HireLK and connect with trusted workers"
                    } else {
                        "Sign in to continue to your HireLK account"
                    },

                fontSize = 13.sp,

                color = TextGray,

                textAlign =
                    TextAlign.Center,

                lineHeight = 19.sp
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // ====================================================
            // FORM CARD
            // ====================================================

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(22.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    ),

                border =
                    BorderStroke(
                        1.dp,
                        BorderGray
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
            ) {

                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {

                    // ============================================
                    // EMAIL
                    // ============================================

                    Text(
                        text = "Email address",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    OutlinedTextField(

                        value = email,

                        onValueChange = {
                            email = it
                        },

                        placeholder = {
                            Text(
                                text =
                                    "Enter your email",
                                color =
                                    Color(0xFF9CA3AF),
                                fontSize = 14.sp
                            )
                        },

                        singleLine = true,

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(13.dp),

                        colors =
                            OutlinedTextFieldDefaults.colors(

                                focusedBorderColor =
                                    PrimaryGreen,

                                unfocusedBorderColor =
                                    BorderGray,

                                focusedContainerColor =
                                    Color.White,

                                unfocusedContainerColor =
                                    Color.White,

                                focusedTextColor =
                                    TextDark,

                                unfocusedTextColor =
                                    TextDark,

                                cursorColor =
                                    PrimaryGreen
                            )
                    )

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

                    // ============================================
                    // PASSWORD
                    // ============================================

                    Text(
                        text = "Password",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    OutlinedTextField(

                        value = password,

                        onValueChange = {
                            password = it
                        },

                        placeholder = {
                            Text(
                                text =
                                    "Enter your password",
                                color =
                                    Color(0xFF9CA3AF),
                                fontSize = 14.sp
                            )
                        },

                        singleLine = true,

                        visualTransformation =
                            if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },

                        trailingIcon = {

                            if (password.isNotEmpty()) {

                                IconButton(
                                    onClick = {
                                        passwordVisible =
                                            !passwordVisible
                                    }
                                ) {

                                    Icon(

                                        imageVector =
                                            if (passwordVisible) {
                                                Icons.Filled.VisibilityOff
                                            } else {
                                                Icons.Filled.Visibility
                                            },

                                        contentDescription =
                                            "Toggle password visibility",

                                        tint =
                                            TextGray
                                    )
                                }
                            }
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(13.dp),

                        colors =
                            OutlinedTextFieldDefaults.colors(

                                focusedBorderColor =
                                    PrimaryGreen,

                                unfocusedBorderColor =
                                    BorderGray,

                                focusedContainerColor =
                                    Color.White,

                                unfocusedContainerColor =
                                    Color.White,

                                focusedTextColor =
                                    TextDark,

                                unfocusedTextColor =
                                    TextDark,

                                cursorColor =
                                    PrimaryGreen
                            )
                    )

                    // ============================================
                    // CONFIRM PASSWORD
                    // ============================================

                    if (isSignUp) {

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        Text(
                            text = "Confirm password",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDark
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        OutlinedTextField(

                            value =
                                confirmPassword,

                            onValueChange = {
                                confirmPassword = it
                            },

                            placeholder = {
                                Text(
                                    text =
                                        "Re-enter your password",
                                    color =
                                        Color(0xFF9CA3AF),
                                    fontSize = 14.sp
                                )
                            },

                            singleLine = true,

                            visualTransformation =
                                if (
                                    confirmPasswordVisible
                                ) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },

                            trailingIcon = {

                                if (
                                    confirmPassword.isNotEmpty()
                                ) {

                                    IconButton(
                                        onClick = {
                                            confirmPasswordVisible =
                                                !confirmPasswordVisible
                                        }
                                    ) {

                                        Icon(

                                            imageVector =
                                                if (
                                                    confirmPasswordVisible
                                                ) {
                                                    Icons.Filled.VisibilityOff
                                                } else {
                                                    Icons.Filled.Visibility
                                                },

                                            contentDescription =
                                                "Toggle password visibility",

                                            tint = TextGray
                                        )
                                    }
                                }
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            shape =
                                RoundedCornerShape(13.dp),

                            colors =
                                OutlinedTextFieldDefaults.colors(

                                    focusedBorderColor =
                                        PrimaryGreen,

                                    unfocusedBorderColor =
                                        BorderGray,

                                    focusedContainerColor =
                                        Color.White,

                                    unfocusedContainerColor =
                                        Color.White,

                                    focusedTextColor =
                                        TextDark,

                                    unfocusedTextColor =
                                        TextDark,

                                    cursorColor =
                                        PrimaryGreen
                                )
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )

                    // ============================================
                    // MAIN BUTTON
                    // ============================================

                    Button(

                        onClick = {

                            // ------------------------------------
                            // VALIDATION
                            // ------------------------------------

                            if (
                                email.isBlank() ||
                                password.isBlank()
                            ) {

                                Toast.makeText(
                                    context,
                                    "Please fill in all fields",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@Button
                            }

                            if (
                                isSignUp &&
                                password != confirmPassword
                            ) {

                                Toast.makeText(
                                    context,
                                    "Passwords do not match",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@Button
                            }

                            isLoading = true

                            // ------------------------------------
                            // SIGN UP
                            // ------------------------------------

                            if (isSignUp) {

                                auth
                                    .createUserWithEmailAndPassword(
                                        email,
                                        password
                                    )

                                    .addOnCompleteListener { task ->

                                        isLoading = false

                                        if (
                                            task.isSuccessful
                                        ) {

                                            Toast.makeText(
                                                context,
                                                "Account created successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            onNavigateToRoleSelection()

                                        } else {

                                            Toast.makeText(
                                                context,
                                                "Error: ${task.exception?.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }

                            }

                            // ------------------------------------
                            // SIGN IN
                            // ------------------------------------

                            else {

                                auth
                                    .signInWithEmailAndPassword(
                                        email,
                                        password
                                    )

                                    .addOnCompleteListener { task ->

                                        if (
                                            task.isSuccessful
                                        ) {

                                            val userId =
                                                auth.currentUser
                                                    ?.uid
                                                    ?: ""

                                            db
                                                .collection("users")
                                                .document(userId)
                                                .get()

                                                .addOnSuccessListener {
                                                        document ->

                                                    isLoading =
                                                        false

                                                    if (
                                                        document.exists() &&
                                                        document.contains(
                                                            "userType"
                                                        )
                                                    ) {

                                                        onNavigateToDashboard()

                                                    } else {

                                                        onNavigateToRoleSelection()
                                                    }
                                                }

                                                .addOnFailureListener {

                                                    isLoading =
                                                        false

                                                    onNavigateToDashboard()
                                                }

                                        } else {

                                            isLoading =
                                                false

                                            Toast.makeText(
                                                context,
                                                "Error: ${task.exception?.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            }
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(50.dp),

                        shape =
                            RoundedCornerShape(14.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    PrimaryGreen,
                                disabledContainerColor =
                                    PrimaryGreen.copy(
                                        alpha = 0.65f
                                    )
                            ),

                        enabled =
                            !isLoading
                    ) {

                        if (isLoading) {

                            CircularProgressIndicator(

                                modifier =
                                    Modifier.size(21.dp),

                                strokeWidth = 2.dp,

                                color =
                                    Color.White
                            )

                        } else {

                            Text(

                                text =
                                    if (isSignUp) {
                                        "Create Account"
                                    } else {
                                        "Sign In"
                                    },

                                color =
                                    Color.White,

                                fontSize = 14.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )

                    // ============================================
                    // DIVIDER
                    // ============================================

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        HorizontalDivider(
                            modifier =
                                Modifier.weight(1f),

                            color =
                                BorderGray
                        )

                        Text(
                            text = "  OR  ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextGray
                        )

                        HorizontalDivider(
                            modifier =
                                Modifier.weight(1f),

                            color =
                                BorderGray
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )

                    // ============================================
                    // GOOGLE BUTTON
                    // ============================================

                    OutlinedButton(

                        onClick = {

                            coroutineScope.launch {

                                try {

                                    isLoading = true

                                    val serverClientId =
                                        "563770186951-c6tfr9tfc2nfj6c0c66df5u853ckbtdn.apps.googleusercontent.com"

                                    val googleIdOption =
                                        GetGoogleIdOption
                                            .Builder()

                                            .setFilterByAuthorizedAccounts(
                                                false
                                            )

                                            .setServerClientId(
                                                serverClientId
                                            )

                                            .setAutoSelectEnabled(
                                                false
                                            )

                                            .build()

                                    val request =
                                        GetCredentialRequest
                                            .Builder()

                                            .addCredentialOption(
                                                googleIdOption
                                            )

                                            .build()

                                    val credentialManager =
                                        CredentialManager
                                            .create(
                                                context
                                            )

                                    val result =
                                        credentialManager
                                            .getCredential(
                                                context,
                                                request
                                            )

                                    val credential =
                                        result.credential

                                    if (
                                        credential is
                                                androidx.credentials.CustomCredential &&
                                        credential.type ==
                                        GoogleIdTokenCredential
                                            .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                    ) {

                                        val googleIdTokenCredential =
                                            GoogleIdTokenCredential
                                                .createFrom(
                                                    credential.data
                                                )

                                        val firebaseCredential =
                                            GoogleAuthProvider
                                                .getCredential(
                                                    googleIdTokenCredential
                                                        .idToken,
                                                    null
                                                )

                                        auth
                                            .signInWithCredential(
                                                firebaseCredential
                                            )

                                            .addOnCompleteListener {
                                                    task ->

                                                isLoading =
                                                    false

                                                if (
                                                    task.isSuccessful
                                                ) {

                                                    val userId =
                                                        auth
                                                            .currentUser
                                                            ?.uid
                                                            ?: ""

                                                    db
                                                        .collection(
                                                            "users"
                                                        )
                                                        .document(
                                                            userId
                                                        )
                                                        .get()

                                                        .addOnSuccessListener {
                                                                document ->

                                                            if (
                                                                document
                                                                    .exists() &&
                                                                document
                                                                    .contains(
                                                                        "userType"
                                                                    )
                                                            ) {

                                                                onNavigateToDashboard()

                                                            } else {

                                                                onNavigateToRoleSelection()
                                                            }
                                                        }

                                                        .addOnFailureListener {

                                                            onNavigateToRoleSelection()
                                                        }

                                                } else {

                                                    Toast.makeText(
                                                        context,
                                                        "Google Sign-In Failed: ${task.exception?.message}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                    }

                                } catch (e: Exception) {

                                    isLoading = false

                                    Toast.makeText(
                                        context,
                                        "Cancelled or Error: ${e.localizedMessage}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(48.dp),

                        shape =
                            RoundedCornerShape(14.dp),

                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                containerColor =
                                    Color.White
                            ),

                        border =
                            BorderStroke(
                                1.dp,
                                BorderGray
                            )
                    ) {

                        // Simple Google "G" indicator

                        Box(
                            modifier =
                                Modifier.size(22.dp),
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text = "G",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4285F4)
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                "Continue with Google",

                            color =
                                TextDark,

                            fontSize = 14.sp,

                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            // ====================================================
            // SWITCH LOGIN / SIGNUP
            // ====================================================

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 16.dp
                        ),

                horizontalArrangement =
                    Arrangement.Center,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(

                    text =
                        if (isSignUp) {
                            "Already have an account? "
                        } else {
                            "Don't have an account? "
                        },

                    color =
                        TextGray,

                    fontSize = 13.sp
                )

                Text(

                    text =
                        if (isSignUp) {
                            "Sign In"
                        } else {
                            "Create account"
                        },

                    color =
                        PrimaryGreen,

                    fontSize = 13.sp,

                    fontWeight =
                        FontWeight.Bold,

                    modifier =
                        Modifier.clickable {

                            isSignUp =
                                !isSignUp

                            // Clear signup-only field
                            if (!isSignUp) {
                                confirmPassword = ""
                            }
                        }
                )
            }
        }
    }
}