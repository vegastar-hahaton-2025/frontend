package ru.xaxaton.startrainer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FinishTestScreen(onConfirm: () -> Unit, onCancel: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Завершить тест?", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37634B))) {
                Text("Да", color = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF905A5E))) {
                Text("Нет", color = Color.White)
            }
        }
    }
}
