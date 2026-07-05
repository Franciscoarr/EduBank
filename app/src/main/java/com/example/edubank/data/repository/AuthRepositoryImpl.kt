package com.example.edubank.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.edubank.core.utils.Resource
import com.example.edubank.domain.repository.AuthRepository
import com.example.edubank.domain.model.UserRole
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUserUid: String?
        get() = firebaseAuth.currentUser?.uid

    override suspend fun getCurrentUserRole(uid: String): Resource<UserRole> {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            val roleStr = document.getString("role") ?: "UNKNOWN"
            Resource.Success(UserRole.valueOf(roleStr))
        } catch (e: Exception) {
            Resource.Error("Error al obtener el rol del usuario", e)
        }
    }

    override suspend fun loginWithEmail(email: String, pass: String): Resource<String> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid ?: throw Exception("Usuario nulo")
            Resource.Success(uid)
        } catch (e: Exception) {
            Resource.Error("Credenciales incorrectas o error de conexión", e)
        }
    }

    override suspend fun loginStudent(classCode: String, username: String, pin: String): Resource<String> {
        return try {
            val querySnapshot = firestore.collection("students")
                .whereEqualTo("classId", classCode)
                .whereEqualTo("username", username)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                return Resource.Error("Alumno o clase no encontrados")
            }

            val document = querySnapshot.documents.first()
            val dbPin = document.getString("pinHash")

            if (dbPin == pin) {
                Resource.Success(document.id)
            } else {
                Resource.Error("PIN incorrecto")
            }
        } catch (e: Exception) {
            Resource.Error("Error al iniciar sesión del alumno", e)
        }
    }

    override suspend fun registerWithEmail(email: String, pass: String, role: UserRole, name: String): Resource<String> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid ?: throw Exception("Error al crear usuario")

            val userMap = hashMapOf(
                "uid" to uid,
                "email" to email,
                "role" to role.name,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("users").document(uid).set(userMap).await()

            val profileCollection = if (role == UserRole.TEACHER) "teachers" else "parents"
            val profileMap = hashMapOf("id" to uid, "name" to name, "email" to email)
            firestore.collection(profileCollection).document(uid).set(profileMap).await()

            Resource.Success(uid)
        } catch (e: Exception) {
            Resource.Error("Error en el registro: ${e.localizedMessage}", e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}