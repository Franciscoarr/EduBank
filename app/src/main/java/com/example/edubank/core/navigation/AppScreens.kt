package com.example.edubank.core.navigation

sealed class AppScreens(val route: String) {
    object Splash : AppScreens("splash_screen")
    object RoleSelection : AppScreens("role_selection_screen")

    object Login : AppScreens("login_screen/{role}") {
        fun createRoute(role: String) = "login_screen/$role"
    }

    object TeacherDashboard : AppScreens("teacher_dashboard")
    object ClassDetail : AppScreens("class_detail/{classId}") {
        fun createRoute(classId: String) = "class_detail/$classId"
    }
    object StudentManage : AppScreens("student_manage/{studentId}") {
        fun createRoute(studentId: String) = "student_manage/$studentId"
    }
    object StudentLogin : AppScreens("student_login_screen")
    object StudentDashboard : AppScreens("student_dashboard/{studentId}") {
        fun createRoute(studentId: String) = "student_dashboard/$studentId"
    }
    object StudentQuests : AppScreens("student_quests")

    object ParentDashboard : AppScreens("parent_dashboard")
    object QrScanner : AppScreens("qr_scanner")
}