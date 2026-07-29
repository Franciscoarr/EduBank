package com.example.edubank.presentation.teacher.class_rules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.edubank.domain.model.CustomReward

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassRulesScreen(
    viewModel: ClassRulesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    var ruleToEdit by remember { mutableStateOf<CustomReward?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reglas de la Clase", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Atrás") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    ruleToEdit = CustomReward(classId = viewModel.classId)
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, "Nueva Regla")
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (state.rules.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay reglas configuradas.", color = Color.Gray) }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.rules) { rule ->
                    RuleCard(
                        rule = rule,
                        onEdit = { ruleToEdit = rule },
                        onDelete = { viewModel.deleteRule(rule.id) }
                    )
                }
            }
        }

        ruleToEdit?.let { currentRule ->
            EditRewardDialog(
                initialReward = currentRule,
                onDismiss = { ruleToEdit = null },
                onConfirm = { updatedRule ->
                    viewModel.saveRule(updatedRule)
                    ruleToEdit = null
                }
            )
        }
    }
}

@Composable
fun RuleCard(rule: CustomReward, onEdit: () -> Unit, onDelete: () -> Unit) {
    val color = if (rule.isIncome) Color(0xFF06D6A0) else Color(0xFFEF476F)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(rule.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = if (rule.autoDayOfMonth != null) "🗓 Automático: Día ${rule.autoDayOfMonth}" else "✋ Manual",
                    color = Color.Gray, fontSize = 14.sp
                )
            }
            Text("${if(rule.isIncome) "+" else "-"}${rule.amount}", fontWeight = FontWeight.ExtraBold, color = color, fontSize = 18.sp)
            Spacer(Modifier.width(16.dp))
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Editar", tint = Color.Gray) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Borrar", tint = Color(0xFFEF476F)) }
        }
    }
}

@Composable
fun EditRewardDialog(initialReward: CustomReward, onDismiss: () -> Unit, onConfirm: (CustomReward) -> Unit) {
    var name by remember { mutableStateOf(initialReward.name) }
    var amountStr by remember { mutableStateOf(if (initialReward.amount > 0) initialReward.amount.toString() else "") }
    var isIncome by remember { mutableStateOf(initialReward.isIncome) }
    var isAuto by remember { mutableStateOf(initialReward.autoDayOfMonth != null) }
    var dayStr by remember { mutableStateOf(initialReward.autoDayOfMonth?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialReward.id.isEmpty()) "Nueva Regla" else "Editar Regla", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = amountStr, onValueChange = { amountStr = it }, label = { Text("Cantidad (🪙)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isIncome) "Recompensa (+)" else "Penalización (-)", color = if (isIncome) Color(0xFF06D6A0) else Color(0xFFEF476F), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Switch(checked = isIncome, onCheckedChange = { isIncome = it })
                }
                Divider()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Pago Automático mensual", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Switch(checked = isAuto, onCheckedChange = { isAuto = it })
                }
                if (isAuto) {
                    OutlinedTextField(value = dayStr, onValueChange = { if(it.length <= 2) dayStr = it }, label = { Text("Día del mes (1-31)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(initialReward.copy(name = name, amount = amountStr.toDoubleOrNull() ?: 0.0, isIncome = isIncome, autoDayOfMonth = if (isAuto) dayStr.toIntOrNull() else null))
                },
                enabled = name.isNotBlank() && amountStr.isNotBlank() && (!isAuto || dayStr.isNotBlank())
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}