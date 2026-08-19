package com.example.hirelk

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences("HireLKPrefs", Context.MODE_PRIVATE)
    val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)

    val currentUser = auth.currentUser

    val startDestination = when {
        isFirstTime -> "onboarding"
        currentUser == null -> "login"
        else -> "login" // will be redirected in LaunchedEffect
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") {
            OnboardingScreen(
                onFinished = {
                    sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
                    navController.navigate("login") { popUpTo("onboarding") { inclusive = true } }
                },
                onSignInClick = {
                    sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
                    navController.navigate("login") { popUpTo("onboarding") { inclusive = true } }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onBackToOnboarding = {
                    navController.navigate("onboarding") { popUpTo("login") { inclusive = true } }
                },
                onNavigateToRoleSelection = {
                    navController.navigate("role_selection") { popUpTo("login") { inclusive = true } }
                },
                onNavigateToDashboard = {
                    checkUserStatusAndNavigate(auth, db, navController, sharedPreferences)
                }
            )
        }

        composable("role_selection") {
            RoleSelectionScreen(
                onNavigateBack = {
                    navController.navigate("login") { popUpTo("role_selection") { inclusive = true } }
                },
                onNavigateToDashboard = { selectedRole ->
                    if (selectedRole == "client") {
                        navController.navigate("client_register") {
                            popUpTo("role_selection") { inclusive = true }
                        }
                    } else {
                        navController.navigate("worker_register") {
                            popUpTo("role_selection") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("client_register") {
            ClientRegisterScreen(
                onNavigateBack = {
                    navController.navigate("role_selection") { popUpTo("client_register") { inclusive = true } }
                },
                onRegisterSuccess = {
                    navController.navigate("client_dashboard") { popUpTo("client_register") { inclusive = true } }
                }
            )
        }

        composable("client_dashboard") {
            ClientDashboardScreen(
                onLogout = {
                    auth.signOut()
                    sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
                    navController.navigate("login") {
                        popUpTo("client_dashboard") { inclusive = true }
                    }
                }
            )
        }

        composable("worker_register") {
            WorkerRegisterScreen(
                onNavigateBack = {
                    navController.navigate("role_selection") { popUpTo("worker_register") { inclusive = true } }
                },
                onRegistrationSuccess = {
                    checkUserStatusAndNavigate(auth, db, navController, sharedPreferences)
                },
                onSwitchToRoleSelect = {
                    navController.navigate("role_selection") { popUpTo("worker_register") { inclusive = true } }
                }
            )
        }

        composable("worker_dashboard") {
            MainProviderApp()
        }
    }

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null && startDestination == "login") {
            checkUserStatusAndNavigate(auth, db, navController, sharedPreferences)
        }
    }
}

fun checkUserStatusAndNavigate(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    navController: NavController,
    sharedPreferences: android.content.SharedPreferences
) {
    val userId = auth.currentUser?.uid
    val userEmail = auth.currentUser?.email

    if (userId != null) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userType = document.getString("userType") ?: document.getString("role")
                    val approvalStatus = document.getString("approvalStatus") ?: "none"

                    if (userType != null) {
                        when {
                            userType == "client" -> {
                                navController.navigate("client_dashboard") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                            userType == "worker" && approvalStatus == "approved" -> {
                                navController.navigate("worker_dashboard") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                            userType == "worker" -> {
                                navController.navigate("worker_register") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                            else -> {
                                navController.navigate("role_selection") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    } else {
                        val initialData = hashMapOf(
                            "email" to (userEmail ?: ""),
                            "uid" to userId
                        )
                        db.collection("users").document(userId).set(initialData, com.google.firebase.firestore.SetOptions.merge())
                        navController.navigate("role_selection") { popUpTo(0) { inclusive = true } }
                    }
                } else {
                    val initialData = hashMapOf(
                        "email" to (userEmail ?: ""),
                        "uid" to userId
                    )
                    db.collection("users").document(userId).set(initialData, com.google.firebase.firestore.SetOptions.merge())
                    navController.navigate("role_selection") { popUpTo(0) { inclusive = true } }
                }
            }
            .addOnFailureListener {
                navController.navigate("role_selection") { popUpTo(0) { inclusive = true } }
            }
    } else {
        navController.navigate("login") { popUpTo(0) { inclusive = true } }
    }
}