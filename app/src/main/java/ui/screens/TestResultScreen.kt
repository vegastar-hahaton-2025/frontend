package ru.xaxaton.startrainer.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite
import ru.xaxaton.startrainer.ui.theme.DarkBurgundy

@Composable
fun TestResultScreen(
    correctAnswers: Int,
    totalQuestions: Int,
    onShareClick: (() -> Unit)? = null, // Опциональный callback для поделиться
    onHomeClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onTestsClick: () -> Unit
) {
    val context = LocalContext.current
    val score = "$correctAnswers/$totalQuestions"
    val percentage = (correctAnswers.toDouble() / totalQuestions * 100).toInt()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBurgundy)
    ) {
        TopCreamWave(modifier = Modifier.align(Alignment.TopCenter))
        BottomCreamWave(modifier = Modifier.align(Alignment.BottomCenter))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 80.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Карточка "Ваш результат"
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CreamWhite
                )
            ) {
                Text(
                    text = "Ваш результат",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Карточка с результатом
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CreamWhite
                )
            ) {
                Text(
                    text = score,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    color = Color.Black,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка "Поделиться результатами"
            Button(
                onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Я прошел тест и набрал $score ($percentage%)!")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Поделиться результатами"))
                    onShareClick?.invoke()
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
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Поделиться результатами")
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
                    tint = DarkBurgundy,
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = onGroupsClick) {
                Icon(
                    imageVector = Icons.Filled.ChatBubbleOutline,
                    contentDescription = "Группы",
                    tint = DarkBurgundy,
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = onTestsClick) {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = "Тесты",
                    tint = DarkBurgundy,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

