package ru.xaxaton.startrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite

@Composable
fun HomeScreen(
    user: SimpleUser?,
    onLogout: () -> Unit,
    onResultsClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8D1725))
    ) {
        // 🔹 Волны сверху и снизу (такие же, как на других экранах)
        TopCreamWave(
            modifier = Modifier.align(Alignment.TopCenter)
        )
        BottomCreamWave(
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // 🔹 Центр экрана — блок с именем, почтой и кнопками
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val fullName = "${user?.family.orEmpty()} ${user?.name.orEmpty()} ${user?.patronymic.orEmpty()}".trim()

            Text(
                text = fullName,
                color = CreamWhite,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = user?.email ?: "example@mail.com",
                color = CreamWhite,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 🔹 Кнопки
            Button(
                onClick = onResultsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CreamWhite,
                    contentColor = Color.Black
                )
            ) {
                Text("Мои результаты")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onEditProfileClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CreamWhite,
                    contentColor = Color.Black
                )
            ) {
                Text("Редактировать профиль")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CreamWhite,
                    contentColor = Color.Black
                )
            ) {
                Text("Выйти из профиля")
            }
        }

        // 🔹 Нижняя панель навигации (на волне)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Домашняя страница",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
            Icon(
                imageVector = Icons.Filled.ChatBubbleOutline,
                contentDescription = "Группа",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
            Icon(
                imageVector = Icons.Filled.Description,
                contentDescription = "Тесты",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
