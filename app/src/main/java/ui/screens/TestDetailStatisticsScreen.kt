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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite
import ru.xaxaton.startrainer.data.GroupTesting
import ru.xaxaton.startrainer.data.TestSession
import ru.xaxaton.startrainer.data.TestMode
import ru.xaxaton.startrainer.data.User
import java.util.UUID

/**
 * Модель для отображения результата участника
 */
data class ParticipantResult(
    val user: User,
    val session: TestSession?,
    val correctAnswers: Int,
    val totalQuestions: Int
)

@Composable
fun TestDetailStatisticsScreen(
    test: GroupTesting,
    participants: List<User>,
    testSessions: List<TestSession>, // Все сессии для данного теста
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onTestsClick: () -> Unit
) {
    // Определяем количество вопросов в зависимости от сложности
    val totalQuestions = when (test.difficulty) {
        "easy" -> 10
        "medium" -> 15
        "hard" -> 20
        else -> 10
    }

    // Фильтруем участников - исключаем создателя теста
    val filteredParticipants = participants.filter { it.id != test.creatorId }

    // Создаем список результатов участников (без создателя)
    val participantResults = filteredParticipants.map { user ->
        // Ищем сессию для этого пользователя и теста
        val session = testSessions.find {
            it.userId == user.id &&
                    it.testId == test.testId &&
                    it.mode == TestMode.EXAM // Только экзаменационные режимы
        }

        val correctAnswers = if (session != null && session.score != null) {
            // Вычисляем правильные ответы из процента
            ((session.score!! / 100.0) * totalQuestions).toInt()
        } else {
            0
        }

        ParticipantResult(
            user = user,
            session = session,
            correctAnswers = correctAnswers,
            totalQuestions = totalQuestions
        )
    }

    // Подсчитываем, сколько участников прошли тест (имеют завершенную сессию)
    val passedCount = participantResults.count { it.session != null && it.session.endTime != null }
    val totalParticipants = filteredParticipants.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8D1725))
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
                tint = Color.Black,
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
            // Заголовок с информацией о прохождении
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CreamWhite
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (totalParticipants == 0) {
                            "В группе пока нет участников"
                        } else {
                            "$passedCount/$totalParticipants прошли тест"
                        },
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp)) // Увеличено до 32.dp

            if (totalParticipants == 0) {
                Text(
                    text = "Добавьте участников в группу, чтобы увидеть их результаты",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Список участников (уже исключая создателя теста)
                participantResults.forEach { result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp), // Увеличен отступ
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CreamWhite
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp), // Увеличен внутренний отступ
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Имя участника
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = result.user.getFormattedName(),
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Результат или статус "не проходил"
                            if (result.session != null && result.session.endTime != null) {
                                // Показываем баллы
                                Text(
                                    text = "${result.correctAnswers}/${result.totalQuestions}",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                // Показываем, что не проходил
                                Text(
                                    text = "Не проходил",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
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
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = onGroupsClick) {
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