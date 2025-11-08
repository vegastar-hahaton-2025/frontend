package ru.xaxaton.startrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite
import ru.xaxaton.startrainer.ui.theme.DarkBurgundy
import ru.xaxaton.startrainer.data.SimpleUser

@Composable
fun EditProfileScreen(
    user: SimpleUser?,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTestsClick: () -> Unit,
    onSaveChanges: (SimpleUser) -> Unit,
    onChangePasswordClick: () -> Unit,
    onGroupsClick: () -> Unit
) {
    var family by remember { mutableStateOf(user?.family.orEmpty()) }
    var name by remember { mutableStateOf(user?.name.orEmpty()) }
    var patronymic by remember { mutableStateOf(user?.patronymic.orEmpty()) }
    var email by remember { mutableStateOf(user?.email.orEmpty()) }

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
                tint = DarkBurgundy, // ← изменено
                modifier = Modifier.size(48.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = family,
                onValueChange = { family = it },
                label = { Text("Фамилия") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = patronymic,
                onValueChange = { patronymic = it },
                label = { Text("Отчество") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Почта") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onChangePasswordClick,
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
                    text = "Смена пароля",
                    color = DarkBurgundy, // ← жирный кремовый не нужен, но цвет как в других кнопках
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (user != null) {
                        val updatedUser = user.copy(
                            family = family,
                            name = name,
                            patronymic = patronymic,
                            email = email
                        )
                        onSaveChanges(updatedUser)
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
                    tint = DarkBurgundy, // ← изменено
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = onGroupsClick) {
                Icon(
                    imageVector = Icons.Filled.ChatBubbleOutline,
                    contentDescription = "Группы",
                    tint = DarkBurgundy, // ← изменено
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = onTestsClick) {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = "Тесты",
                    tint = DarkBurgundy, // ← изменено
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}