package ru.xaxaton.startrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite
import ru.xaxaton.startrainer.ui.theme.DarkBurgundy
import ru.xaxaton.startrainer.data.SimpleUser
import ru.xaxaton.startrainer.utils.hashPasswordWithSalt
import ru.xaxaton.startrainer.utils.verifyPassword

@Composable
fun ChangePasswordScreen(
    user: SimpleUser,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTestsClick: () -> Unit,
    onPasswordChanged: (SimpleUser) -> Unit,
    onGroupsClick: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // ✅ Цвета без обводки — как в других экранах
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
                tint = DarkBurgundy, // ← исправлено
                modifier = Modifier.size(48.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = currentPassword,
                onValueChange = {
                    currentPassword = it
                    errorMessage = null
                },
                label = { Text("Текущий пароль") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = null
                },
                label = { Text("Новый пароль") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = null
                },
                label = { Text("Подтвердите новый пароль") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    when {
                        currentPassword.isBlank() ->
                            errorMessage = "Введите текущий пароль"
                        newPassword.isBlank() ->
                            errorMessage = "Введите новый пароль"
                        confirmPassword.isBlank() ->
                            errorMessage = "Подтвердите новый пароль"
                        !verifyPassword(currentPassword, user.passwordHash, user.salt) ->
                            errorMessage = "Неверный пароль"
                        newPassword != confirmPassword ->
                            errorMessage = "Пароли не совпадают"
                        else -> {
                            val (newHash, newSalt) = hashPasswordWithSalt(newPassword)
                            val updatedUser = user.copy(
                                passwordHash = newHash,
                                salt = newSalt
                            )
                            onPasswordChanged(updatedUser)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CreamWhite,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Сохранить изменения",
                    color = DarkBurgundy,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage!!,
                    color = CreamWhite, // ← кремовый, а не красный
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onHomeClick) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Домашняя страница",
                    tint = DarkBurgundy, // ← исправлено
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = onGroupsClick) {
                Icon(
                    imageVector = Icons.Filled.ChatBubbleOutline,
                    contentDescription = "Группы",
                    tint = DarkBurgundy, // ← исправлено
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = onTestsClick) {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = "Тесты",
                    tint = DarkBurgundy, // ← исправлено
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}