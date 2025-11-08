package ru.xaxaton.startrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite
import ru.xaxaton.startrainer.ui.theme.DarkBurgundy
import ru.xaxaton.startrainer.data.TestSession
import ru.xaxaton.startrainer.data.Test
import ru.xaxaton.startrainer.data.TestMode
import java.util.UUID

@Composable
fun TrainingResultsScreen(
    testSessions: List<TestSession>,
    tests: List<Test>,
    sessionDifficulties: Map<UUID, String>, // sessionId -> difficulty (easy/medium/hard)
    onSessionClick: (UUID) -> Unit, // Callback при клике на сессию
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onTestsClick: () -> Unit
) {
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
                .padding(horizontal = 32.dp)
                .padding(top = 160.dp, bottom = 100.dp) // Увеличено top до 160.dp
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Заголовок в белом боксе
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CreamWhite
                )
            ) {
                Text(
                    text = "Мои результаты\nобучений",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp)) // Увеличено до 32.dp

            if (testSessions.isEmpty()) {
                Text(
                    text = "У вас пока нет завершенных тестов",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // Сортируем сессии от последнего к первому
                val sortedSessions = testSessions
                    .filter { it.endTime != null && it.score != null }
                    .sortedByDescending { it.endTime }

                sortedSessions.forEach { session ->
                    val difficulty = sessionDifficulties[session.id] ?: "easy"

                    // Вычисляем правильные ответы из процента
                    val totalQuestions = when (difficulty) {
                        "easy" -> 10
                        "medium" -> 15
                        "hard" -> 20
                        else -> 10
                    }
                    val correctAnswers = ((session.score!! / 100.0) * totalQuestions).toInt()

                    // Название сложности
                    val difficultyName = when (difficulty) {
                        "easy" -> "лёгкий"
                        "medium" -> "средний"
                        "hard" -> "тяжёлый"
                        else -> "лёгкий"
                    }

                    // Цвет для сложности
                    val difficultyColor = when (difficulty) {
                        "easy" -> Color(0xFF81C784) // Зелёный
                        "medium" -> Color(0xFFFFB74D) // Жёлтый
                        "hard" -> Color(0xFFE57373) // Красный
                        else -> Color(0xFF81C784)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onSessionClick(session.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CreamWhite
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Бокс с результатом (правильные/всего)
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color.Gray.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "$correctAnswers/$totalQuestions",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Бокс с названием сложности
                            Box(
                                modifier = Modifier
                                    .background(
                                        difficultyColor.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = difficultyName,
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
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

@Composable
fun TestingResultsScreen(
    testSessions: List<TestSession>,
    tests: List<Test>,
    sessionDifficulties: Map<UUID, String>, // sessionId -> difficulty (easy/medium/hard)
    onSessionClick: (UUID) -> Unit, // Callback при клике на сессию
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onTestsClick: () -> Unit
) {
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
                .padding(horizontal = 32.dp)
                .padding(top = 160.dp, bottom = 100.dp) // Увеличено top до 160.dp
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Заголовок в белом боксе
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CreamWhite
                )
            ) {
                Text(
                    text = "Мои результаты\nтестирований",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp)) // Увеличено до 32.dp

            if (testSessions.isEmpty()) {
                Text(
                    text = "У вас пока нет завершенных тестов",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // Сортируем сессии от последнего к первому
                val sortedSessions = testSessions
                    .filter { it.endTime != null && it.score != null }
                    .sortedByDescending { it.endTime }

                sortedSessions.forEach { session ->
                    val difficulty = sessionDifficulties[session.id] ?: "easy"

                    // Вычисляем правильные ответы из процента
                    val totalQuestions = when (difficulty) {
                        "easy" -> 10
                        "medium" -> 15
                        "hard" -> 20
                        else -> 10
                    }
                    val correctAnswers = ((session.score!! / 100.0) * totalQuestions).toInt()

                    // Название сложности
                    val difficultyName = when (difficulty) {
                        "easy" -> "лёгкий"
                        "medium" -> "средний"
                        "hard" -> "тяжёлый"
                        else -> "лёгкий"
                    }

                    // Цвет для сложности
                    val difficultyColor = when (difficulty) {
                        "easy" -> Color(0xFF81C784) // Зелёный
                        "medium" -> Color(0xFFFFB74D) // Жёлтый
                        "hard" -> Color(0xFFE57373) // Красный
                        else -> Color(0xFF81C784)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onSessionClick(session.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CreamWhite
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Бокс с результатом (правильные/всего)
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color.Gray.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "$correctAnswers/$totalQuestions",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Бокс с названием сложности
                            Box(
                                modifier = Modifier
                                    .background(
                                        difficultyColor.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = difficultyName,
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
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