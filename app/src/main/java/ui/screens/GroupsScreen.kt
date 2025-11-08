package ru.xaxaton.startrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite
import ru.xaxaton.startrainer.ui.theme.DarkBurgundy
import ru.xaxaton.startrainer.data.Group
import ru.xaxaton.startrainer.data.User
import java.util.UUID

@Composable
fun GroupsScreen(
    onHomeClick: () -> Unit,
    onTestsClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    onJoinGroupClick: () -> Unit,
    onEditGroupClick: (String) -> Unit, // groupId as String
    onLeaveGroup: (String) -> Unit, // groupId as String
    groups: List<Group>,
    userId: UUID,
    users: List<User>,
    onGroupsClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Все группы") }
    val filterOptions = listOf("Все группы", "Созданные", "Состоите")
    
    // Фильтрация групп
    val filteredGroups = when (selectedFilter) {
        "Созданные" -> groups.filter { it.ownerId == userId }
        "Состоите" -> groups.filter { it.ownerId != userId } // Все группы, где пользователь не владелец
        else -> groups
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBurgundy)
    ) {
        TopCreamWave(modifier = Modifier.align(Alignment.TopCenter))
        BottomCreamWave(modifier = Modifier.align(Alignment.BottomCenter))

        // Верхний ряд с кнопками
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onJoinGroupClick) { // Кнопка ссылки для вступления
                Icon(
                    imageVector = Icons.Filled.Link,
                    contentDescription = "Вступить в группу",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(onClick = onCreateGroupClick) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Создать группу",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 120.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ваши группы",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box {
                Button(
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CreamWhite,
                        contentColor = Color.Black
                    )
                ) { Text(selectedFilter) }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filterOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedFilter = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (filteredGroups.isEmpty()) {
                Text(
                    text = "Группы не найдены",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top
                ) {
                    filteredGroups.forEach { group ->
                        val isCreator = group.ownerId == userId
                        // Получаем владельца группы
                        val owner = users.find { it.id == group.ownerId }
                        // Подсчитываем участников (включая владельца)
                        // В реальном приложении это должно приходить из БД через GroupMembership
                        val memberCount = 1 // Минимум владелец, в реальности нужно считать через GroupMembership
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = CreamWhite
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = group.name,
                                        color = Color.Black,
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    // Иконка троеточия для создателя
                                    if (isCreator) {
                                        IconButton(
                                            onClick = { onEditGroupClick(group.id.toString()) },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.MoreVert,
                                                contentDescription = "Редактировать группу",
                                                tint = Color.Black,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    
                                    // Иконка крестика для участника (не создателя)
                                    if (!isCreator) {
                                        IconButton(
                                            onClick = { onLeaveGroup(group.id.toString()) },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = "Выйти из группы",
                                                tint = Color.Black,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = "Участников: $memberCount",
                                    color = Color.Black.copy(alpha = 0.6f),
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
            IconButton(onClick = onHomeClick) { // Домик → Home
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
            IconButton(onClick = onTestsClick) { // Тесты → Tests
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
