package ru.xaxaton.startrainer.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite
import ru.xaxaton.startrainer.ui.theme.DarkBurgundy


@Composable
fun StartScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBurgundy)
    ) {
        TopCreamWave(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-40).dp)
        )

        BottomCreamWave(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (40).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Заголовок + слоган
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "StarTrainer",
                    style = MaterialTheme.typography.headlineLarge,
                    color = CreamWhite
                )
                Text(
                    text = "Ваш первый шаг к правильной помощи",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CreamWhite.copy(alpha = 0.8f)
                )
            }

            // ЭКГ (на Canvas)
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .alpha(0.5f)
            ) {
                val path = Path().apply {
                    val w = size.width
                    val h = size.height
                    val midY = h / 2

                    // Начало — базовая линия
                    moveTo(0f, midY)

                    // Комплекс P (маленький подъём)
                    lineTo(w * 0.1f, midY)
                    lineTo(w * 0.15f, midY * 0.8f)  // пик P
                    lineTo(w * 0.2f, midY)

                    // Линия до QRS
                    lineTo(w * 0.3f, midY)

                    // Комплекс QRS — главный пик (острый и угловатый!)
                    lineTo(w * 0.32f, midY * 1.2f) // Q — небольшой провал
                    lineTo(w * 0.35f, h * 0.2f)    // R — высокий острый пик
                    lineTo(w * 0.38f, h * 0.9f)    // S — провал
                    lineTo(w * 0.4f, midY)          // возврат к базе

                    // Сегмент ST
                    lineTo(w * 0.45f, midY)

                    // Волна T (широкий подъём)
                    lineTo(w * 0.5f, midY * 0.85f) // подъём T
                    lineTo(w * 0.55f, midY)         // спуск T

                    // Повторим второй цикл для большей наглядности
                    lineTo(w * 0.6f, midY)

                    // Второй P
                    lineTo(w * 0.65f, midY * 0.8f)
                    lineTo(w * 0.7f, midY)

                    // Второй QRS (острый!)
                    lineTo(w * 0.72f, midY * 1.2f)
                    lineTo(w * 0.75f, h * 0.2f)
                    lineTo(w * 0.78f, h * 0.9f)
                    lineTo(w * 0.8f, midY)

                    // Второй T
                    lineTo(w * 0.85f, midY * 0.85f)
                    lineTo(w * 0.9f, midY)

                    // Завершение
                    lineTo(w, midY)
                }

                drawPath(
                    path = path,
                    color = CreamWhite,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Butt, // острые окончания → больше "углов"
                        join = androidx.compose.ui.graphics.StrokeJoin.Miter // острые углы при соединении
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопка "Регистрация"
            val buttonShape = RoundedCornerShape(12.dp)
            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CreamWhite,
                    contentColor = Color.Black
                ),
                shape = buttonShape
            ) {
                Text(
                    text = "Регистрация",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkBurgundy
                )

            }

            // Ссылка "У меня уже есть аккаунт"
            Text(
                text = "Войти",
                style = MaterialTheme.typography.bodyLarge,
                color = CreamWhite,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable { onLoginClick() }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}