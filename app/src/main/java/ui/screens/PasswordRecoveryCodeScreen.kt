package ru.xaxaton.startrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite
import ru.xaxaton.startrainer.ui.theme.DarkBurgundy

@Composable
fun PasswordRecoveryCodeScreen(
    email: String,
    sentCode: String,
    onBackClick: () -> Unit,
    onCodeVerified: () -> Unit
) {
    var inputCode by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBurgundy)
    ) {
        TopCreamWave(modifier = Modifier.align(Alignment.TopCenter))
        BottomCreamWave(modifier = Modifier.align(Alignment.BottomCenter))

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = DarkBurgundy,
                modifier = Modifier.size(48.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Подтверждение кода",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CreamWhite
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Введите код, отправленный на $email",
                style = MaterialTheme.typography.bodyMedium,
                color = CreamWhite
            )

            Spacer(modifier = Modifier.height(24.dp))

            val fieldColors = TextFieldDefaults.colors(
                focusedContainerColor = CreamWhite,
                unfocusedContainerColor = CreamWhite,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )

            OutlinedTextField(
                value = inputCode,
                onValueChange = { inputCode = it },
                label = { Text("Введите код") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (inputCode.trim() == sentCode.trim()) {
                        message = "Код подтверждён"
                        onCodeVerified()
                    } else {
                        message = "Неверный код"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CreamWhite,
                    contentColor = Color.Black
                )
            ) {
                Text("Подтвердить", style = MaterialTheme.typography.titleMedium)
            }

            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    color = if (message.contains("")) Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
