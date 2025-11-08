package ru.xaxaton.startrainer.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite
import ru.xaxaton.startrainer.ui.theme.DarkBurgundy
import ru.xaxaton.startrainer.data.SimpleUser

@Composable
fun PasswordRecoveryScreen(
    onBackClick: () -> Unit,
    users: List<SimpleUser>,
    onCodeSent: (String, String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var shownCode by remember { mutableStateOf("") }

    // ✅ Цвета без обводки — как в LoginScreen
    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = CreamWhite,
        unfocusedContainerColor = CreamWhite,
        cursorColor = DarkBurgundy,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black.copy(alpha = 0.7f),
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primary)
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
                tint = DarkBurgundy, // ← исправлено
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
                text = "Восстановление пароля",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CreamWhite
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ TextField вместо OutlinedTextField
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Введите вашу почту") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val existingUser = users.find { it.email.trim().equals(email.trim(), ignoreCase = true) }

                    if (existingUser == null) {
                        message = "Пользователь с такой почтой не найден"
                        return@Button
                    }

                    val code = (100000..999999).random().toString()

                    Log.d("PasswordRecovery", "Сгенерированный код для $email: $code")

                    shownCode = code

                    onCodeSent(email, code)

                    message = "Код отправлен на почту"
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
                Text(
                    text = "Отправить код на почту",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkBurgundy // ← как в других экранах
                )
            }

            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = CreamWhite, // ← кремовый, а не чёрный
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (shownCode.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Код: $shownCode",
                    color = CreamWhite.copy(alpha = 0.7f), // ← тоже кремовый
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}