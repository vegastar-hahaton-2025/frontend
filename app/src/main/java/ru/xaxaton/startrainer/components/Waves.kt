package ru.xaxaton.startrainer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import ru.xaxaton.startrainer.ui.theme.CreamWhite
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun TopCreamWave(modifier: Modifier = Modifier) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(screenHeight * 0.2f)
    ) {
        val path = Path().apply {
            moveTo(0f, size.height)
            cubicTo(
                size.width * 0.25f, size.height * 0.3f,
                size.width * 0.6f, size.height * 0.7f,
                size.width, size.height * 0.2f
            )
            lineTo(size.width, 0f)
            lineTo(0f, 0f)
            close()
        }
        drawPath(path = path, color = CreamWhite)
    }
}

@Composable
fun BottomCreamWave(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val path = Path().apply {
            moveTo(0f, size.height * 0.5f)
            cubicTo(
                size.width * 0.15f, size.height * 0.3f,
                size.width * 0.35f, size.height * 0.7f,
                size.width * 0.5f, size.height * 0.5f
            )
            cubicTo(
                size.width * 0.65f, size.height * 0.3f,
                size.width * 0.85f, size.height * 0.7f,
                size.width, size.height * 0.5f
            )
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path = path, color = CreamWhite)
    }
}
