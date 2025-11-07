package ru.xaxaton.startrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite

sealed class JoinResult {
    data object Success : JoinResult()
    data object AlreadyMember : JoinResult()
    data object InvalidCode : JoinResult()
}

@Composable
fun JoinGroupScreen(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTestsClick: () -> Unit,
    onJoinGroup: (String) -> JoinResult, // возвращает результат вступления
    userEmail: String,
    onSuccess: () -> Unit = {} // вызывается после успешного вступления
) {
    var joinCode by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }
    var isAlreadyMember by remember { mutableStateOf(false) }
    
    // Автоматический возврат после успешного вступления
    LaunchedEffect(isSuccess) {
        if (isSuccess && !isAlreadyMember) {
            delay(1500) // задержка 1.5 секунды для показа сообщения
            onSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8D1725))
    ) {
        TopCreamWave(modifier = Modifier.align(Alignment.TopCenter))
        BottomCreamWave(modifier = Modifier.align(Alignment.BottomCenter))

        // Верхний ряд с кнопкой назад
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 80.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Заголовок "Вступление в группу"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = CreamWhite,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Вступление в группу",
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Поле для кода
            val fieldColors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = CreamWhite,
                focusedTextColor = CreamWhite,
                unfocusedTextColor = CreamWhite,
                focusedIndicatorColor = CreamWhite,
                unfocusedIndicatorColor = CreamWhite,
                focusedLabelColor = CreamWhite,
                unfocusedLabelColor = CreamWhite,
                focusedPlaceholderColor = CreamWhite.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = CreamWhite.copy(alpha = 0.6f)
            )

            TextField(
                value = joinCode,
                onValueChange = { 
                    joinCode = it
                    message = "" // очищаем сообщение при вводе
                    isSuccess = false
                    isAlreadyMember = false
                },
                label = { Text("Код группы") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                textStyle = TextStyle(color = CreamWhite, fontSize = 16.sp)
            )

            // Сообщение о результате
            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = when {
                        isSuccess -> Color(0xFF4CAF50)
                        isAlreadyMember -> Color(0xFFFF9800)
                        else -> Color(0xFFFF5252)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Кнопка "Вступить в группу"
            Button(
                onClick = {
                    if (joinCode.isNotBlank()) {
                        val result = onJoinGroup(joinCode.trim())
                        when (result) {
                            is JoinResult.Success -> {
                                message = "Вы успешно вступили в группу"
                                isSuccess = true
                                isAlreadyMember = false
                                joinCode = "" // очищаем поле после успешного вступления
                            }
                            is JoinResult.AlreadyMember -> {
                                message = "Вы уже состоите в этой группе"
                                isSuccess = false
                                isAlreadyMember = true
                            }
                            is JoinResult.InvalidCode -> {
                                message = "Код неверный"
                                isSuccess = false
                                isAlreadyMember = false
                            }
                        }
                    }
                },
                enabled = joinCode.isNotBlank() && !isSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CreamWhite,
                    contentColor = Color.Black,
                    disabledContainerColor = CreamWhite.copy(alpha = 0.5f),
                    disabledContentColor = Color.Black.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Вступить в группу",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Нижнее меню
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
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = { /* здесь остаёмся на группах */ }) {
                Icon(
                    imageVector = Icons.Filled.ChatBubbleOutline,
                    contentDescription = "Группы",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = onTestsClick) {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = "Тесты",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

