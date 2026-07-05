package com.example.edubank.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.example.edubank.core.utils.Resource
import com.example.edubank.domain.model.Student
import com.example.edubank.domain.model.Transaction
import com.example.edubank.domain.repository.StudentRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StudentRepository {

    override fun getStudentData(studentId: String): Flow<Resource<Student>> = callbackFlow {
        trySend(Resource.Loading)

        val listener = firestore.collection("students").document(studentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error("Error en la base de datos", error))
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val student = snapshot.toObject(Student::class.java)
                    if (student != null) {
                        trySend(Resource.Success(student.copy(id = snapshot.id)))
                    }
                }
            }
        awaitClose { listener.remove() }
    }

    override fun getStudentTransactions(studentId: String): Flow<Resource<List<Transaction>>> = callbackFlow {
        trySend(Resource.Loading)

        val listener = firestore.collection("transactions")
            .whereEqualTo("studentId", studentId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error("Error al cargar movimientos", error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val txList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Transaction::class.java)?.copy(id = doc.id)
                    }
                    trySend(Resource.Success(txList))
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun pairStudentWithParent(parentId: String, qrPairingCode: String): Resource<Unit> {
        return try {
            val query = firestore.collection("students")
                .whereEqualTo("qrPairingCode", qrPairingCode)
                .get()
                .await()

            if (query.isEmpty) {
                return Resource.Error("Código QR inválido o expirado")
            }

            val studentDoc = query.documents.first()
            val studentId = studentDoc.id

            firestore.collection("parents").document(parentId)
                .update("childrenIds", FieldValue.arrayUnion(studentId))
                .await()

            firestore.collection("students").document(studentId)
                .update("qrPairingCode", "")
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error al vincular la cuenta", e)
        }
    }
}