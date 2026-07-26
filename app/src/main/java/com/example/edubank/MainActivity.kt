package com.example.edubank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.edubank.core.navigation.AppScreens
import com.example.edubank.presentation.auth.role_selection.RoleSelectionScreen
import com.example.edubank.presentation.auth.student_login.StudentLoginScreen
import com.example.edubank.presentation.student.dashboard.StudentDashboardScreen
import com.example.edubank.ui.theme.EduBankTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EduBankTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EduBankNavHost()
                }
            }
        }
    }
}

@Composable
fun EduBankNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.RoleSelection.route
    ) {

        composable(AppScreens.RoleSelection.route) {
            RoleSelectionScreen(
                onStudentClick = { navController.navigate(AppScreens.StudentLogin.route) },
                onTeacherClick = { /* TODO en el futuro */ },
                onParentClick = { /* TODO en el futuro */ }
            )
        }

        composable(AppScreens.StudentLogin.route) {
            StudentLoginScreen(
                onLoginSuccess = {
                    val dummyStudentId = "test_id_123"
                    navController.navigate(AppScreens.StudentDashboard.createRoute(dummyStudentId)) {
                        popUpTo(AppScreens.RoleSelection.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = AppScreens.StudentDashboard.route,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) {
            StudentDashboardScreen(
                onNavigateToQuests = { /* TODO: Navegar a historial */ },
                onNavigateToTrophies = { /* TODO: Navegar a logros */ }
            )
        }
    }
}