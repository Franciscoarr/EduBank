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
import java.util.UUID

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

    override suspend fun createClass(name: String, grade: String, teacherId: String): Resource<Unit> {
        return try {
            val newClassRef = firestore.collection("classes").document()

            val classroom = Classroom(
                id = newClassRef.id,
                teacherId = teacherId,
                name = name,
                grade = grade
            )

            newClassRef.set(classroom).await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error al crear la clase: ${e.localizedMessage}", e)
        }
    }

    override suspend fun createStudent(classId: String, teacherId: String, username: String, pin: String): Resource<Unit> {
        return try {
            val newStudentRef = firestore.collection("students").document()

            val student = Student(
                id = newStudentRef.id,
                classId = classId,
                teacherId = teacherId,
                username = username,
                balance = 0.0,
                xp = 0,
                level = 1,
                pinHash = pin,
                qrPairingCode = UUID.randomUUID().toString().substring(0, 8)
            )

            newStudentRef.set(student).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error al matricular alumno: ${e.localizedMessage}", e)
        }
    }
}