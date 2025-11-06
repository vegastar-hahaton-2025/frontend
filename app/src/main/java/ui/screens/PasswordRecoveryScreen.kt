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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primary)
    ) {
        // 🔹 Фоновые волны
        TopCreamWave(modifier = Modifier.align(Alignment.TopCenter))
        BottomCreamWave(modifier = Modifier.align(Alignment.BottomCenter))

        // 🔹 Стрелка "Назад" — в левом верхнем углу
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = Color.Black,
                modifier = Modifier.size(48.dp)
            )
        }

        // 🔹 Основное содержимое — по центру
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Заголовок
            Text(
                text = "Восстановление пароля",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CreamWhite
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Настройка цветов для текстового поля
            val fieldColors = TextFieldDefaults.colors(
                focusedContainerColor = CreamWhite,
                unfocusedContainerColor = CreamWhite,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )

            // Поле для почты
            OutlinedTextField(
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

            // Кнопка "Отправить код"
            Button(
                onClick = {
                    val existingUser = users.find { it.email.trim().equals(email.trim(), ignoreCase = true) }

                    if (existingUser == null) {
                        message = "Пользователь с такой почтой не найден"
                        return@Button
                    }

                    // Генерируем 6-значный код
                    val code = (100000..999999).random().toString()

                    // Логируем код (для теста)
                    Log.d("PasswordRecovery", "Сгенерированный код для $email: $code")

                    // Для визуального теста
                    shownCode = code

                    // Передаём код в навигацию
                    onCodeSent(email, code)

                    message = "Код отправлен на почту (для теста: $code)"
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
                Text("Отправить код на почту", style = MaterialTheme.typography.titleMedium)
            }

            // Сообщения
            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Для теста показываем код
            if (shownCode.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Код (для теста): $shownCode",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
