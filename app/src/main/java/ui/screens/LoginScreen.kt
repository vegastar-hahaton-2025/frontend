package ru.xaxaton.startrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ru.xaxaton.startrainer.ui.components.*
import ru.xaxaton.startrainer.ui.theme.CreamWhite
import ru.xaxaton.startrainer.ui.theme.DarkBurgundy
import ru.xaxaton.startrainer.utils.*
import ru.xaxaton.startrainer.data.SimpleUser

@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    users: List<SimpleUser> = emptyList(),
    onLoginSuccess: (SimpleUser) -> Unit = {},
    onUsersUpdate: (List<SimpleUser>) -> Unit = {},
    onPasswordRecoveryClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    // ✅ Цвета без обводки, с кремовой меткой при фокусе
    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = CreamWhite,
        unfocusedContainerColor = CreamWhite,
        cursorColor = DarkBurgundy,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black.copy(alpha = 0.7f),
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        errorLabelColor = Color.Black,
        errorCursorColor = DarkBurgundy
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
                tint = DarkBurgundy, // ← было Color.Black
                modifier = Modifier.size(48.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Вход",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CreamWhite
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ✅ Без цвета в label — Material сам управляет
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Почта") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val user = users.find { it.email.trim().equals(email.trim(), ignoreCase = true) }
                    message = when {
                        user == null -> "Пользователь не найден"
                        !verifyPassword(password, user.passwordHash, user.salt) -> "Неверный пароль"
                        else -> {
                            onLoginSuccess(user)
                            "Успешный вход!"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CreamWhite,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Войти", style = MaterialTheme.typography.titleMedium, color = DarkBurgundy)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onPasswordRecoveryClick,
                modifier = Modifier
                    .height(40.dp)
                    .wrapContentWidth(Alignment.Start),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Восстановить пароль",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CreamWhite,
                    fontWeight = FontWeight.Medium
                )
            }

            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = CreamWhite, // ← сообщения — кремовые
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

// 💡 Диалоговое окно оставлено без изменений (оно не использует ваши кастомные цвета)
@Composable
fun PasswordResetDialog(
    users: List<SimpleUser>,
    onDismiss: () -> Unit,
    onUsersUpdate: (List<SimpleUser>) -> Unit,
    onMessage: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var stage by remember { mutableStateOf(1) }
    var userToReset by remember { mutableStateOf<SimpleUser?>(null) }
    var newPass1 by remember { mutableStateOf("") }
    var newPass2 by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                when (stage) {
                    1 -> "Восстановление пароля"
                    2 -> "Новый пароль"
                    else -> "Готово"
                }
            )
        },
        text = {
            Column {
                when (stage) {
                    1 -> {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Введите вашу почту") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                        )
                    }

                    2 -> {
                        OutlinedTextField(
                            value = newPass1,
                            onValueChange = { newPass1 = it },
                            label = { Text("Новый пароль") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newPass2,
                            onValueChange = { newPass2 = it },
                            label = { Text("Повторите пароль") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }

                    3 -> Text(info)
                }
            }
        },
        confirmButton = {
            when (stage) {
                1 -> TextButton(onClick = {
                    val found = users.find { it.email.trim().equals(email.trim(), ignoreCase = true) }
                    if (found != null) {
                        userToReset = found
                        stage = 2
                    } else {
                        onMessage("Пользователь с такой почтой не найден")
                        onDismiss()
                    }
                }) { Text("Далее") }

                2 -> TextButton(onClick = {
                    if (newPass1 != newPass2) {
                        info = "Пароли не совпадают"
                        return@TextButton
                    }
                    val (hash, salt) = hashPasswordWithSalt(newPass1)
                    userToReset?.let { user ->
                        val updatedUser = user.copy(passwordHash = hash, salt = salt)
                        val newList = users.map {
                            if (it.email == user.email) updatedUser else it
                        }
                        onUsersUpdate(newList)
                        info = "Пароль успешно обновлён!"
                        onMessage(info)
                        stage = 3
                    }
                }) { Text("Сохранить") }

                3 -> TextButton(onClick = onDismiss) { Text("Закрыть") }
            }
        },
        dismissButton = {
            if (stage < 3) TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}