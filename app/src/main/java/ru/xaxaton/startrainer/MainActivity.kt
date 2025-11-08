package ru.xaxaton.startrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import ru.xaxaton.startrainer.ui.screens.*
import ru.xaxaton.startrainer.ui.theme.STARTrainerTheme
import ru.xaxaton.startrainer.data.SimpleUser
import ru.xaxaton.startrainer.data.Group
import ru.xaxaton.startrainer.data.GroupMembership
import ru.xaxaton.startrainer.data.GroupTestAssignment
import ru.xaxaton.startrainer.data.Test
import ru.xaxaton.startrainer.data.TestSession
import ru.xaxaton.startrainer.data.Question
import ru.xaxaton.startrainer.data.TriageCategory
import ru.xaxaton.startrainer.data.TestMode
import ru.xaxaton.startrainer.data.TestQuestion
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            STARTrainerTheme {
                NavigationApp()
            }
        }
    }
}

@Composable
fun NavigationApp() {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    fun copyTextToClipboard(text: String) {
        clipboardManager.setText(AnnotatedString(text))
    }

    var currentScreen by remember { mutableStateOf("start") }
    var users by remember { mutableStateOf(listOf<SimpleUser>()) }
    var currentUser by remember { mutableStateOf<SimpleUser?>(null) }
    var recoveryEmail by remember { mutableStateOf("") }
    var generatedCode by remember { mutableStateOf("") }
    var groups by remember { mutableStateOf(listOf<Group>()) }
    var groupMemberships by remember { mutableStateOf(listOf<GroupMembership>()) }
    var editingGroupId by remember { mutableStateOf<UUID?>(null) }
    var tests by remember { mutableStateOf(listOf<Test>()) }
    var groupTestAssignments by remember { mutableStateOf(listOf<GroupTestAssignment>()) }
    var testSessions by remember { mutableStateOf(listOf<TestSession>()) }
    
    // Вопросы и связи тест-вопрос (в реальном приложении будут загружаться из API)
    var questions by remember { mutableStateOf(listOf<Question>()) }
    var testQuestions by remember { mutableStateOf(listOf<TestQuestion>()) }
    
    // Состояние для прохождения теста
    var currentTestId by remember { mutableStateOf<UUID?>(null) }
    var currentTestQuestions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var currentTestAnswers by remember { mutableStateOf<Map<Int, TriageCategory>>(emptyMap()) }
    var currentQuestionIndex by remember { mutableStateOf(1) }
    var testDifficulty by remember { mutableStateOf<String?>(null) }

    // Получение групп пользователя
    fun getUserGroups(userId: UUID): List<Group> {
        val userGroupIds = groupMemberships.filter { it.userId == userId }.map { it.groupId }.toSet()
        val ownedGroupIds = groups.filter { it.ownerId == userId }.map { it.id }.toSet()
        val allGroupIds = userGroupIds + ownedGroupIds
        return groups.filter { it.id in allGroupIds }
    }

    // Проверка, является ли пользователь участником группы
    fun isUserMemberOfGroup(userId: UUID, groupId: UUID): Boolean {
        return groupMemberships.any { it.userId == userId && it.groupId == groupId } ||
               groups.any { it.id == groupId && it.ownerId == userId }
    }

    // Получение участников группы
    fun getGroupMembers(groupId: UUID): List<UUID> {
        val ownerId = groups.find { it.id == groupId }?.ownerId
        val memberIds = groupMemberships.filter { it.groupId == groupId }.map { it.userId }
        return if (ownerId != null) {
            (memberIds + ownerId).distinct()
        } else {
            memberIds
        }
    }

    // Создание группы
    fun createGroup(name: String, creatorId: UUID) {
        val newGroup = Group(
            id = UUID.randomUUID(),
            name = name,
            ownerId = creatorId
        )
        groups = groups + newGroup
    }

    // Вступление в группу по коду (код генерируется из UUID группы)
    fun joinGroupByCode(code: String, userId: UUID): JoinResult {
        // Ищем группу по коду (первые 6 символов UUID)
        val group = groups.find { 
            it.getJoinCode().equals(code, ignoreCase = true) 
        } ?: return JoinResult.InvalidCode
        
        // Проверяем, не является ли пользователь уже участником
        if (isUserMemberOfGroup(userId, group.id)) {
            return JoinResult.AlreadyMember
        }
        
        // Добавляем членство
        val membership = GroupMembership(userId, group.id)
        groupMemberships = groupMemberships + membership
        return JoinResult.Success
    }

    // Выход из группы
    fun leaveGroup(groupId: UUID, userId: UUID) {
        val group = groups.find { it.id == groupId }
        // Владелец не может выйти из группы
        if (group != null && group.ownerId != userId) {
            groupMemberships = groupMemberships.filter { 
                !(it.userId == userId && it.groupId == groupId) 
            }
        }
    }

    // Удаление участника (для создателя)
    fun removeMemberFromGroup(groupId: UUID, memberId: UUID) {
        val group = groups.find { it.id == groupId }
        // Нельзя удалить владельца
        if (group != null && group.ownerId != memberId) {
            groupMemberships = groupMemberships.filter { 
                !(it.userId == memberId && it.groupId == groupId) 
            }
        }
    }

    // Редактирование названия группы
    fun updateGroupName(groupId: UUID, newName: String) {
        val group = groups.find { it.id == groupId }
        if (group != null) {
            val updatedGroup = group.copy(name = newName)
            groups = groups.map { if (it.id == groupId) updatedGroup else it }
        }
    }

    // Удаление группы
    fun deleteGroup(groupId: UUID) {
        groups = groups.filter { it.id != groupId }
        // Удаляем все членства в этой группе
        groupMemberships = groupMemberships.filter { it.groupId != groupId }
    }

    // Генерация тестовых вопросов (только для разработки, когда нет данных в БД)
    fun generateTestQuestions(count: Int): List<Question> {
        val categories = listOf(
            TriageCategory.RED,
            TriageCategory.YELLOW,
            TriageCategory.GREEN,
            TriageCategory.BLACK
        )
        return (1..count).map { index ->
            val category = categories[index % categories.size]
            Question(
                id = UUID.randomUUID(),
                description = "Вопрос $index: Описание ситуации для тестирования. Это пример вопроса для обучения.",
                imageUrl = if (index % 3 == 0) "/images/questions/test-image-$index.jpg" else null,
                correctAnswer = category,
                hint = "Подсказка для вопроса $index: Это подсказка, которая поможет вам выбрать правильный ответ."
            )
        }
    }

    // Получение вопросов для теста из БД (в реальном приложении будет запрос к API)
    fun getTestQuestions(testId: UUID, difficulty: String): List<Question> {
        // Получаем все связи тест-вопрос для данного теста, отсортированные по Order
        val testQuestionLinks = testQuestions
            .filter { it.testId == testId }
            .sortedBy { it.order }
        
        // Определяем количество вопросов в зависимости от сложности
        val questionCount = when (difficulty) {
            "easy" -> 10
            "medium" -> 15
            "hard" -> 20
            else -> testQuestionLinks.size
        }
        
        // Берем первые N вопросов (или все, если меньше)
        val selectedTestQuestions = testQuestionLinks.take(questionCount)
        
        // Получаем сами вопросы по их ID
        val questionIds = selectedTestQuestions.map { it.questionId }.toSet()
        val testQuestionsList = questions
            .filter { it.id in questionIds }
            .sortedBy { question -> 
                selectedTestQuestions.find { it.questionId == question.id }?.order ?: Int.MAX_VALUE
            }
        
        // Если вопросов в БД нет, генерируем тестовые (для разработки)
        return if (testQuestionsList.isEmpty()) {
            generateTestQuestions(questionCount)
        } else {
            testQuestionsList
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (currentScreen) {
            "start" -> StartScreen(
                onRegisterClick = { currentScreen = "register" },
                onLoginClick = { currentScreen = "login" },
                modifier = Modifier.padding(innerPadding)
            )

            "register" -> RegistrationScreen(
                onBackClick = { currentScreen = "start" },
                existingUsers = users,
                onRegister = { newUser ->
                    users = users + newUser
                    currentUser = newUser
                    currentScreen = "home"
                }
            )

            "login" -> LoginScreen(
                onBackClick = { currentScreen = "start" },
                users = users,
                onLoginSuccess = { loggedInUser ->
                    currentUser = loggedInUser
                    currentScreen = "home"
                },
                onUsersUpdate = { updatedList -> users = updatedList },
                onPasswordRecoveryClick = { currentScreen = "password_recovery" }
            )

            "password_recovery" -> PasswordRecoveryScreen(
                onBackClick = { currentScreen = "login" },
                users = users,
                onCodeSent = { email, code ->
                    recoveryEmail = email
                    generatedCode = code
                    currentScreen = "password_recovery_code"
                }
            )

            "password_recovery_code" -> PasswordRecoveryCodeScreen(
                email = recoveryEmail,
                sentCode = generatedCode,
                onBackClick = { currentScreen = "password_recovery" },
                onCodeVerified = { currentScreen = "password_recovery_new_password" }
            )

            "password_recovery_new_password" -> PasswordRecoveryNewPasswordScreen(
                onBackClick = { currentScreen = "login" },
                users = users,
                onPasswordChanged = { updatedUsers ->
                    users = updatedUsers
                    currentScreen = "start"
                }
            )

            "editProfile" -> {
                val user = currentUser
                if (user != null) {
                    EditProfileScreen(
                        user = user,
                        onBackClick = { currentScreen = "home" },
                        onHomeClick = { currentScreen = "home" },
                        onTestsClick = { currentScreen = "tests" },
                        onGroupsClick = { currentScreen = "groups" },
                        onSaveChanges = { updatedUser ->
                            users = users.map { if (it.email == user.email) updatedUser else it }
                            currentUser = updatedUser
                            currentScreen = "home"
                        },
                        onChangePasswordClick = { currentScreen = "changePassword" }
                    )
                }
            }

            "changePassword" -> currentUser?.let { user ->
                ChangePasswordScreen(
                    user = user,
                    onBackClick = { currentScreen = "editProfile" },
                    onHomeClick = { currentScreen = "home" },
                    onTestsClick = { currentScreen = "tests" },
                    onGroupsClick = { currentScreen = "groups" },
                    onPasswordChanged = { updated ->
                        // Обновляем пользователя в списке users
                        users = users.map { if (it.id == user.id) updated else it }
                        currentUser = updated
                        currentScreen = "home"
                    }
                )
            }

            "home" -> HomeScreen(
                user = currentUser,
                onLogout = {
                    currentUser = null
                    currentScreen = "start"
                },
                onHomeClick = { currentScreen = "home" },
                onResultsClick = { currentScreen = "results" },
                onEditProfileClick = { currentScreen = "editProfile" },
                onTestsClick = { currentScreen = "tests" },
                onGroupsClick = { currentScreen = "groups" }
            )

            "results" -> currentUser?.let { user ->
                val userSessions = testSessions.filter { it.userId == user.id }
                ResultsScreen(
                    testSessions = userSessions,
                    tests = tests,
                    onBackClick = { currentScreen = "home" },
                    onHomeClick = { currentScreen = "home" },
                    onGroupsClick = { currentScreen = "groups" },
                    onTestsClick = { currentScreen = "tests" }
                )
            }

            "tests" -> currentUser?.let { user ->
                // Получаем тесты, назначенные группам пользователя
                val userGroupIds = getUserGroups(user.id).map { it.id }.toSet()
                val assignedTestIds = groupTestAssignments
                    .filter { it.groupId in userGroupIds }
                    .map { it.testId }
                    .toSet()
                val availableTests = tests.filter { it.id in assignedTestIds }
                
                TestsChooseScreen(
                    availableTests = availableTests,
                    onHomeClick = { currentScreen = "home" },
                    onGroupsClick = { currentScreen = "groups" },
                    onTestsClick = { currentScreen = "tests" },
                    onTrainingClick = { testId -> 
                        currentTestId = testId
                        currentScreen = "trainingLevel" 
                    },
                    onTestingClick = { testId -> 
                        // TODO: начать экзаменационную сессию
                    }
                )
            }

            "groups" -> currentUser?.let { user ->
                GroupsScreen(
                    onHomeClick = { currentScreen = "home" },
                    onTestsClick = { currentScreen = "tests" },
                    onGroupsClick = { currentScreen = "groups" },
                    onCreateGroupClick = { currentScreen = "createGroup" },
                    onJoinGroupClick = { currentScreen = "joinGroup" },
                    onEditGroupClick = { groupId ->
                        editingGroupId = UUID.fromString(groupId)
                        currentScreen = "editGroup"
                    },
                    onLeaveGroup = { groupId -> 
                        leaveGroup(UUID.fromString(groupId), user.id) 
                    },
                    groups = getUserGroups(user.id),
                    userId = user.id,
                    users = users.map { it.toUser() }
                )
            }

            "joinGroup" -> currentUser?.let { user ->
                JoinGroupScreen(
                    onBackClick = { currentScreen = "groups" },
                    onHomeClick = { currentScreen = "home" },
                    onTestsClick = { currentScreen = "tests" },
                    onGroupsClick = { currentScreen = "groups" },
                    onJoinGroup = { code -> joinGroupByCode(code, user.id) },
                    onSuccess = { currentScreen = "groups" }
                )
            }

            "createGroup" -> currentUser?.let { user ->
                CreateGroupScreen(
                    onBackClick = { currentScreen = "groups" },
                    onHomeClick = { currentScreen = "home" },
                    onTestsClick = { currentScreen = "tests" },
                    onGroupsClick = { currentScreen = "groups" },
                    onCreateGroup = { groupName ->
                        createGroup(groupName, user.id)
                        currentScreen = "groups"
                    }
                )
            }

            "editGroup" -> {
                val group = editingGroupId?.let { groups.find { it.id == editingGroupId } }
                val user = currentUser
                if (group != null && user != null) {
                    val groupMembers = getGroupMembers(group.id)
                    val memberUsers = users.map { it.toUser() }.filter { it.id in groupMembers }
                    EditGroupScreen(
                        group = group,
                        owner = users.map { it.toUser() }.find { it.id == group.ownerId },
                        members = memberUsers,
                        currentUserId = user.id,
                        onBackClick = { currentScreen = "groups" },
                        onHomeClick = { currentScreen = "home" },
                        onTestsClick = { currentScreen = "tests" },
                        onGroupsClick = { currentScreen = "groups" },
                        onEditClick = { currentScreen = "editGroupName" },
                        onRemoveMember = { memberId -> removeMemberFromGroup(group.id, UUID.fromString(memberId)) },
                        onCopyLink = { joinCode -> copyTextToClipboard(joinCode) }
                    )
                }
            }

            "editGroupName" -> {
                val group = editingGroupId?.let { groups.find { it.id == editingGroupId } }
                val user = currentUser
                if (group != null && user != null) {
                    EditGroupNameScreen(
                        group = group,
                        onBackClick = { currentScreen = "editGroup" },
                        onHomeClick = { currentScreen = "home" },
                        onTestsClick = { currentScreen = "tests" },
                        onGroupsClick = { currentScreen = "groups" },
                        onSave = { newName ->
                            updateGroupName(group.id, newName)
                            currentScreen = "editGroup"
                        },
                        onDelete = {
                            deleteGroup(group.id)
                            currentScreen = "groups"
                        }
                    )
                }
            }

            "trainingLevel" -> currentUser?.let { user ->
                TrainingLevelScreen(
                    onBackClick = { 
                        currentTestId = null
                        currentScreen = "tests" 
                    },
                    onHomeClick = { currentScreen = "home" },
                    onGroupsClick = { currentScreen = "groups" },
                    onTestsClick = { currentScreen = "tests" },
                    onLevelSelected = { level ->
                        testDifficulty = level
                        val testId = currentTestId
                        if (testId != null) {
                            // Получаем вопросы из теста в зависимости от сложности
                            currentTestQuestions = getTestQuestions(testId, level)
                            currentTestAnswers = emptyMap()
                            currentQuestionIndex = 1
                            currentScreen = "testSession"
                        }
                    }
                )
            }

            "testSession" -> currentUser?.let { user ->
                val testId = currentTestId
                if (testId != null && currentTestQuestions.isNotEmpty()) {
                    val currentQuestion = currentTestQuestions[currentQuestionIndex - 1]
                    val selectedAnswer = currentTestAnswers[currentQuestionIndex]
                    
                    TestSessionScreen(
                        currentQuestionIndex = currentQuestionIndex,
                        totalQuestions = currentTestQuestions.size,
                        question = currentQuestion,
                        selectedAnswer = selectedAnswer,
                        onAnswerSelected = { answer ->
                            currentTestAnswers = currentTestAnswers + (currentQuestionIndex to answer)
                        },
                        onPreviousQuestion = {
                            if (currentQuestionIndex > 1) {
                                currentQuestionIndex--
                            }
                        },
                        onNextQuestion = {
                            if (currentQuestionIndex < currentTestQuestions.size) {
                                currentQuestionIndex++
                            }
                        },
                        onFinishClick = {
                            // Подсчитываем результаты
                            val correctAnswers = currentTestQuestions.mapIndexed { index, question ->
                                val userAnswer = currentTestAnswers[index + 1]
                                userAnswer == question.correctAnswer
                            }.count { it }
                            
                            // Сохраняем сессию
                            val session = TestSession(
                                id = UUID.randomUUID(),
                                testId = testId,
                                userId = user.id,
                                mode = TestMode.TRAINING,
                                startTime = System.currentTimeMillis(),
                                endTime = System.currentTimeMillis(),
                                score = (correctAnswers.toDouble() / currentTestQuestions.size * 100)
                            )
                            testSessions = testSessions + session
                            
                            // Переходим на экран результатов
                            currentScreen = "testResult"
                        },
                        onBackClick = { currentScreen = "trainingLevel" },
                        onHomeClick = { currentScreen = "home" },
                        onGroupsClick = { currentScreen = "groups" },
                        onTestsClick = { currentScreen = "tests" }
                    )
                }
            }

            "testResult" -> {
                val correctAnswers = currentTestQuestions.mapIndexed { index, question ->
                    val userAnswer = currentTestAnswers[index + 1]
                    userAnswer == question.correctAnswer
                }.count { it }
                
                TestResultScreen(
                    correctAnswers = correctAnswers,
                    totalQuestions = currentTestQuestions.size,
                    onShareClick = { /* Уже обработано в экране */ },
                    onHomeClick = { 
                        currentTestId = null
                        currentTestQuestions = emptyList()
                        currentTestAnswers = emptyMap()
                        currentQuestionIndex = 1
                        currentScreen = "home" 
                    },
                    onGroupsClick = { 
                        currentTestId = null
                        currentTestQuestions = emptyList()
                        currentTestAnswers = emptyMap()
                        currentQuestionIndex = 1
                        currentScreen = "groups" 
                    },
                    onTestsClick = { 
                        currentTestId = null
                        currentTestQuestions = emptyList()
                        currentTestAnswers = emptyMap()
                        currentQuestionIndex = 1
                        currentScreen = "tests" 
                    }
                )
            }
        }
    }
}