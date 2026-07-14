package com.example.edubank.presentation.student.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
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
import com.example.edubank.domain.model.Transaction

@Composable
fun StudentDashboardScreen(
    viewModel: StudentDashboardViewModel = hiltViewModel(),
    onNavigateToQuests: () -> Unit,
    onNavigateToTrophies: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = state.errorMessage!!, color = MaterialTheme.colorScheme.error)
        }
        return
    }

    val student = state.student ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            PlayerHeader(
                username = student.username,
                level = student.level,
                xp = student.xp
            )
        }

        item {
            TreasureCard(balance = student.balance)
        }

        item {
            ActionButtons(
                onQuestsClick = onNavigateToQuests,
                onTrophiesClick = onNavigateToTrophies
            )
        }

        // 4. Últimos Movimientos (Mini-historial)
        item {
            Text(
                text = "Últimas Aventuras",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(state.recentTransactions) { transaction ->
            TransactionItem(transaction = transaction)
        }
    }
}

@Composable
fun PlayerHeader(username: String, level: Int, xp: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Star, contentDescription = "Avatar", tint = Color.White, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = username, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Nvl $level", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                val progress = (xp % 100) / 100f
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .height(12.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp)),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun TreasureCard(balance: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Tu Tesoro", color = Color.White, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🪙", fontSize = 48.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = String.format("%.2f", balance),
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun ActionButtons(onQuestsClick: () -> Unit, onTrophiesClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onQuestsClick,
            modifier = Modifier
                .weight(1f)
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Misiones", color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = onTrophiesClick,
            modifier = Modifier
                .weight(1f)
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9F1C))
        ) {
            Text("Trofeos", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val isIncome = transaction.isIncome
    val color = if (isIncome) Color(0xFF06D6A0) else Color(0xFFEF476F)
    val icon = if (isIncome) Icons.Default.Add else Icons.Default.Close

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = transaction.reason, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = if (isIncome) "Recompensa" else "Penalización", color = Color.Gray, fontSize = 12.sp)
            }

            Text(
                text = "${if(isIncome) "+" else "-"}${transaction.amount}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = color
            )
        }
    }
}