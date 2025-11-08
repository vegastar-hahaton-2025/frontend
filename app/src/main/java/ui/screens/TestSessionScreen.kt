package ru.xaxaton.startrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite

@Composable
fun TestSessionScreen(
    currentQuestionIndex: Int,
    totalQuestions: Int,
    question: Question,
    selectedAnswer: TriageCategory?,
    onAnswerSelected: (TriageCategory) -> Unit,
    onPreviousQuestion: () -> Unit,
    onNextQuestion: () -> Unit,
    onFinishClick: () -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onTestsClick: () -> Unit,
    allowHints: Boolean = true // Разрешить показ подсказок (для режима обучения)
) {
    var showFinishDialog by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8D1725))
    ) {
        TopCreamWave(modifier = Modifier.align(Alignment.TopCenter))
        BottomCreamWave(modifier = Modifier.align(Alignment.BottomCenter))

        // Верхняя панель с номером вопроса и кнопкой завершить
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$currentQuestionIndex/$totalQuestions",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { showFinishDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CreamWhite,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Завершить")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 80.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Карточка с вопросом
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Box {
                    // Иконка лампочки в левом верхнем углу (только если разрешены подсказки)
                    if (allowHints) {
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
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            text = question.description,
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // Изображение (если показана подсказка и есть изображение)
                        if (showHint && question.imageUrl != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            val fullImageUrl = ApiConfig.getImageUrl(question.imageUrl)
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
                        if (showHint && question.hint != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = question.hint!!,
                                color = Color.Black.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Варианты ответов
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Красный кружок
                AnswerCircle(
                    color = Color(0xFFE53935),
                    isSelected = selectedAnswer == TriageCategory.RED,
                    onClick = { onAnswerSelected(TriageCategory.RED) }
                )

                // Желтый кружок
                AnswerCircle(
                    color = Color(0xFFFFB300),
                    isSelected = selectedAnswer == TriageCategory.YELLOW,
                    onClick = { onAnswerSelected(TriageCategory.YELLOW) }
                )

                // Зеленый кружок
                AnswerCircle(
                    color = Color(0xFF4CAF50),
                    isSelected = selectedAnswer == TriageCategory.GREEN,
                    onClick = { onAnswerSelected(TriageCategory.GREEN) }
                )

                // Черный кружок
                AnswerCircle(
                    color = Color(0xFF212121),
                    isSelected = selectedAnswer == TriageCategory.BLACK,
                    onClick = { onAnswerSelected(TriageCategory.BLACK) }
                )
            }

            // Навигация между вопросами
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPreviousQuestion,
                    enabled = currentQuestionIndex > 1,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Предыдущий вопрос",
                        tint = if (currentQuestionIndex > 1) Color.White else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(
                    onClick = onNextQuestion,
                    enabled = currentQuestionIndex < totalQuestions,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Следующий вопрос",
                        tint = if (currentQuestionIndex < totalQuestions) Color.White else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Диалог подтверждения завершения
        if (showFinishDialog) {
            AlertDialog(
                onDismissRequest = { showFinishDialog = false },
                title = {
                    Text("Завершение теста")
                },
                text = {
                    Text("Вы хотите завершить тест?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showFinishDialog = false
                            onFinishClick()
                        }
                    ) {
                        Text("Да", color = Color(0xFF8D1725))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showFinishDialog = false }
                    ) {
                        Text("Нет")
                    }
                },
                containerColor = CreamWhite
            )
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
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Выбрано",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

