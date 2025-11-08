package ru.xaxaton.startrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite
import ru.xaxaton.startrainer.ui.theme.DarkBurgundy

@Composable
fun CreateGroupScreen(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTestsClick: () -> Unit,
    onCreateGroup: (String) -> Unit,
    onGroupsClick: () -> Unit
) {
    var groupName by remember { mutableStateOf("") }

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
                .padding(start = 16.dp, top = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = DarkBurgundy,
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
                        text = "Создание группы",
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                textStyle = TextStyle(color = CreamWhite, fontSize = 16.sp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    if (groupName.isNotBlank()) {
                        onCreateGroup(groupName.trim())
                    }
                },
                enabled = groupName.isNotBlank(),
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
                    text = "Создать группу",
                    style = MaterialTheme.typography.bodyLarge
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
                    tint = DarkBurgundy,
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = onGroupsClick) { // Чат → Groups
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

