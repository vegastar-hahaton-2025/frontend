package ru.xaxaton.startrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.xaxaton.startrainer.data.Question
import ru.xaxaton.startrainer.data.TriageCategory
import ru.xaxaton.startrainer.data.ApiConfig
import ru.xaxaton.startrainer.data.UserAnswer
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite

@Composable
fun TrainingSessionDetailScreen(
    questions: List<Question>,
    userAnswers: List<UserAnswer>,
    currentQuestionIndex: Int,
    onQuestionIndexChange: (Int) -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onTestsClick: () -> Unit
) {
    val currentQuestion = questions.getOrNull(currentQuestionIndex - 1)
    val currentUserAnswer = userAnswers.find { it.questionId == currentQuestion?.id }
    
    var showHint by remember { mutableStateOf(false) }
    
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
                .padding(top = 80.dp, bottom = 100.dp)
        ) {
            // Заголовок и счетчик вопросов
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Результаты обучения",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$currentQuestionIndex/${questions.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (currentQuestion != null) {
                // Карточка с вопросом
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CreamWhite
                    )
                ) {
                    Box {
                        // Иконка лампочки для подсказки
                        IconButton(
                            onClick = { showHint = !showHint },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = "Подсказка",
                                tint = Color(0xFFFFA726),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                                .padding(top = 16.dp)
                        ) {
                            Text(
                                text = "вопрос $currentQuestionIndex",
                                color = Color.Black,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = currentQuestion.description,
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            // Изображение (если показана подсказка и есть изображение)
                            if (showHint && currentQuestion.imageUrl != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                val fullImageUrl = ApiConfig.getImageUrl(currentQuestion.imageUrl)
                                if (fullImageUrl != null) {
                                    AsyncImage(
                                        model = fullImageUrl,
                                        contentDescription = "Изображение вопроса",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            // Подсказка (если показана)
                            if (showHint && currentQuestion.hint != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = currentQuestion.hint!!,
                                    color = Color.Black.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Показываем выбранный и правильный ответ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Ваш ответ
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ваш ответ",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        val userAnswer = currentUserAnswer?.selectedAnswer
                        if (userAnswer != null) {
                            AnswerCircle(
                                category = userAnswer,
                                isSelected = true,
                                showCheckmark = false
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray.copy(alpha = 0.3f))
                            )
                        }
                    }

                    // Верный ответ
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "верный ответ",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        AnswerCircle(
                            category = currentQuestion.correctAnswer,
                            isSelected = false,
                            showCheckmark = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Навигация между вопросами
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { 
                            if (currentQuestionIndex > 1) {
                                onQuestionIndexChange(currentQuestionIndex - 1)
                                showHint = false
                            }
                        },
                        enabled = currentQuestionIndex > 1,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CreamWhite,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Предыдущий",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Предыдущий")
                    }

                    Button(
                        onClick = { 
                            if (currentQuestionIndex < questions.size) {
                                onQuestionIndexChange(currentQuestionIndex + 1)
                                showHint = false
                            }
                        },
                        enabled = currentQuestionIndex < questions.size,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(start = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CreamWhite,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Следующий")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "Следующий",
                            modifier = Modifier.size(20.dp)
                        )
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

@Composable
fun AnswerCircle(
    category: TriageCategory,
    isSelected: Boolean,
    showCheckmark: Boolean
) {
    val color = when (category) {
        TriageCategory.RED -> Color(0xFFFF5252)
        TriageCategory.YELLOW -> Color(0xFFFFEB3B)
        TriageCategory.GREEN -> Color(0xFF4CAF50)
        TriageCategory.BLACK -> Color(0xFF212121)
    }

    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(color)
            .then(
                if (isSelected) {
                    Modifier.border(4.dp, Color.Black, CircleShape)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (showCheckmark) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Правильный ответ",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}



