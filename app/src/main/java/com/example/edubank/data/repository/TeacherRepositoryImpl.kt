package com.example.edubank.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.edubank.core.utils.Resource
import com.example.edubank.domain.model.Classroom
import com.example.edubank.domain.model.Student
import com.example.edubank.domain.model.Transaction
import com.example.edubank.domain.repository.TeacherRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TeacherRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TeacherRepository {

    override fun getTeacherClasses(teacherId: String): Flow<Resource<List<Classroom>>> = callbackFlow {
        trySend(Resource.Loading)

        val listener = firestore.collection("classes")
            .whereEqualTo("teacherId", teacherId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error("Error al cargar las clases", error))
                    return@addSnapshotListener
                }

                val classes = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Classroom::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(Resource.Success(classes))
            }

        awaitClose { listener.remove() }
    }

    override fun getStudentsByClass(classId: String): Flow<Resource<List<Student>>> = callbackFlow {
        trySend(Resource.Loading)

        val listener = firestore.collection("students")
            .whereEqualTo("classId", classId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error("Error al cargar los alumnos", error))
                    return@addSnapshotListener
                }

                val students = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Student::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(Resource.Success(students.sortedBy { it.username.lowercase() }))
            }

        awaitClose { listener.remove() }
    }

    override suspend fun processTransaction(transaction: Transaction): Resource<Unit> {
        return try {
            firestore.runTransaction { firebaseTransaction ->
                val studentRef = firestore.collection("students").document(transaction.studentId)
                val snapshot = firebaseTransaction.get(studentRef)

                if (!snapshot.exists()) {
                    throw Exception("El alumno no existe")
                }

                val currentBalance = snapshot.getDouble("balance") ?: 0.0
                val currentXp = snapshot.getLong("xp")?.toInt() ?: 0
                val currentLevel = snapshot.getLong("level")?.toInt() ?: 1

                val newBalance = if (transaction.isIncome) {
                    currentBalance + transaction.amount
                } else {
                    val result = currentBalance - transaction.amount
                    if (result < 0) 0.0 else result
                }

                var newXp = currentXp
                var newLevel = currentLevel

                if (transaction.isIncome) {
                    newXp += (transaction.amount * 10).toInt()
                    newLevel = (newXp / 100) + 1
                }

                firebaseTransaction.update(
                    studentRef,
                    mapOf(
                        "balance" to newBalance,
                        "xp" to newXp,
                        "level" to newLevel
                    )
                )

                val newTxRef = firestore.collection("transactions").document()
                val finalTransaction = transaction.copy(
                    id = newTxRef.id,
                    timestamp = System.currentTimeMillis()
                )
                firebaseTransaction.set(newTxRef, finalTransaction)

            }.await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al procesar el pago", e)
        }
    }
}