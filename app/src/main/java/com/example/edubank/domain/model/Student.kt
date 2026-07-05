package com.example.edubank.domain.model

data class Student(
    val id: String = "",
    val classId: String = "",
    val teacherId: String = "",
    val username: String = "",
    val balance: Double = 0.0,
    val xp: Int = 0,
    val level: Int = 1,
    val avatarId: String = "default_avatar",
    val qrPairingCode: String = ""
)