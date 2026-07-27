package com.example.edubank.presentation.teacher.student_manage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentManageScreen(
    viewModel: StudentManageViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val student = state.student

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Jugador") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading || student == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                StudentInfoCard(
                    username = student.username,
                    level = student.level,
                    xp = student.xp,
                    balance = student.balance
                )
            }

            item {
                Text("Recompensas (+)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF06D6A0))
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TransactionButton(text = "Tarea (+10)", amount = 10.0, color = Color(0xFF06D6A0), isIncome = true, onClick = {
                        viewModel.processTransaction(10.0, "Hacer la tarea", true)
                    }, modifier = Modifier.weight(1f))

                    TransactionButton(text = "Ayudar (+5)", amount = 5.0, color = Color(0xFF06D6A0), isIncome = true, onClick = {
                        viewModel.processTransaction(5.0, "Ayudar a un compañero", true)
                    }, modifier = Modifier.weight(1f))
                }
            }

            item {
                Text("Penalizaciones (-)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF476F))
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TransactionButton(text = "Mal comp. (-5)", amount = 5.0, color = Color(0xFFEF476F), isIncome = false, onClick = {
                        viewModel.processTransaction(5.0, "Mal comportamiento", false)
                    }, modifier = Modifier.weight(1f))

                    TransactionButton(text = "Material (-2)", amount = 2.0, color = Color(0xFFEF476F), isIncome = false, onClick = {
                        viewModel.processTransaction(2.0, "Olvidar material", false)
                    }, modifier = Modifier.weight(1f))
                }
            }

            if (state.isTransactionLoading) {
                item { CircularProgressIndicator() }
            }
        }
    }
}

@Composable
fun StudentInfoCard(username: String, level: Int, xp: Int, balance: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(username.take(1).uppercase(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(username, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Nivel $level | $xp XP", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tesoro: $balance 🪙", fontSize = 32.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun TransactionButton(text: String, amount: Double, color: Color, isIncome: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text, fontWeight = FontWeight.Bold, color = Color.White)
    }
}