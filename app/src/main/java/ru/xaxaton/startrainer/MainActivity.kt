package ru.xaxaton.startrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import ru.xaxaton.startrainer.data.UserAnswer
import ru.xaxaton.startrainer.data.GroupTesting
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
    var userAnswers by remember { mutableStateOf(listOf<UserAnswer>()) }
    
    // Тестирования, назначенные группам
    var groupTestings by remember { mutableStateOf(listOf<GroupTesting>()) }
    
    // Вопросы и связи тест-вопрос (в реальном приложении будут загружаться из API)
    var questions by remember { mutableStateOf(listOf<Question>()) }
    var testQuestions by remember { mutableStateOf(listOf<TestQuestion>()) }
    
    // Состояние для прохождения теста
    var currentTestId by remember { mutableStateOf<UUID?>(null) }
    var currentTestQuestions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var currentTestAnswers by remember { mutableStateOf<Map<Int, TriageCategory>>(emptyMap()) }
    var currentQuestionIndex by remember { mutableStateOf(1) }
    var testDifficulty by remember { mutableStateOf<String?>(null) }
    
    // Храним сложность для каждой сессии теста (sessionId -> difficulty)
    var sessionDifficulties by remember { mutableStateOf<Map<UUID, String>>(emptyMap()) }
    
    // Выбранная сессия для детального просмотра
    var selectedSessionId by remember { mutableStateOf<UUID?>(null) }
    
    // Выбранный тест для детальной статистики
    var selectedTestForStatistics by remember { mutableStateOf<GroupTesting?>(null) }
    
    // Выбранное тестирование для подтверждения начала
    var selectedTesting by remember { mutableStateOf<GroupTesting?>(null) }
    var showTestingConfirmation by remember { mutableStateOf(false) }

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
    // Согласно структуре БД, при удалении группы каскадно удаляются:
    // - GroupMemberships (CASCADE)
    // - GroupTestAssignments (CASCADE)
    fun deleteGroup(groupId: UUID) {
        groups = groups.filter { it.id != groupId }
        // Удаляем все членства в этой группе
        groupMemberships = groupMemberships.filter { it.groupId != groupId }
        // Удаляем все назначения тестов этой группе
        groupTestAssignments = groupTestAssignments.filter { it.groupId != groupId }
        // Удаляем локальные DTO для отображения
        groupTestings = groupTestings.filter { it.groupId != groupId }
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
    // Согласно структуре БД, вопросы теста определяются через TestQuestions
    fun getTestQuestions(testId: UUID, difficulty: String? = null): List<Question> {
        // Получаем все связи тест-вопрос для данного теста, отсортированные по Order
        val testQuestionLinks = testQuestions
            .filter { it.testId == testId }
            .sortedBy { it.order }
        
        // Если есть связи в БД, используем их
        if (testQuestionLinks.isNotEmpty()) {
            // Получаем сами вопросы по их ID из TestQuestions
            val questionIds = testQuestionLinks.map { it.questionId }.toSet()
            return questions
                .filter { it.id in questionIds }
                .sortedBy { question -> 
                    testQuestionLinks.find { it.questionId == question.id }?.order ?: Int.MAX_VALUE
                }
        }
        
        // Если вопросов в БД нет, генерируем тестовые (для разработки)
        // Используем difficulty только для определения количества при генерации
        val questionCount = when (difficulty) {
            "easy" -> 10
            "medium" -> 15
            "hard" -> 20
            else -> 10
        }
        return generateTestQuestions(questionCount)
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
                onResultsClick = { currentScreen = "resultsType" },
                onEditProfileClick = { currentScreen = "editProfile" },
                onTestsClick = { currentScreen = "tests" },
                onGroupsClick = { currentScreen = "groups" }
            )

            "resultsType" -> ResultsTypeScreen(
                onBackClick = { currentScreen = "home" },
                onTrainingResultsClick = { currentScreen = "trainingResults" },
                onTestingResultsClick = { currentScreen = "testingResults" },
                onHomeClick = { currentScreen = "home" },
                onGroupsClick = { currentScreen = "groups" },
                onTestsClick = { currentScreen = "tests" }
            )

            "trainingResults" -> currentUser?.let { user ->
                // Получаем только сессии обучения
                val trainingSessions = testSessions
                    .filter { it.userId == user.id && it.mode == TestMode.TRAINING }
                    .sortedByDescending { it.endTime }
                
                // Создаем мапу sessionId -> difficulty для каждой сессии
                val sessionIdToDifficulty = trainingSessions.associate { session ->
                    val difficulty = sessionDifficulties[session.id] ?: "easy"
                    session.id to difficulty
                }
                
                TrainingResultsScreen(
                    testSessions = trainingSessions,
                    tests = tests,
                    sessionDifficulties = sessionIdToDifficulty,
                    onSessionClick = { sessionId ->
                        selectedSessionId = sessionId
                        currentScreen = "trainingSessionDetail"
                    },
                    onBackClick = { currentScreen = "resultsType" },
                    onHomeClick = { currentScreen = "home" },
                    onGroupsClick = { currentScreen = "groups" },
                    onTestsClick = { currentScreen = "tests" }
                )
            }

            "testingResults" -> currentUser?.let { user ->
                // TODO: Реализовать экран результатов тестирования
                // Пока показываем пустой экран
                TrainingResultsScreen(
                    testSessions = emptyList(),
                    tests = tests,
                    sessionDifficulties = emptyMap(),
                    onSessionClick = { },
                    onBackClick = { currentScreen = "resultsType" },
                    onHomeClick = { currentScreen = "home" },
                    onGroupsClick = { currentScreen = "groups" },
                    onTestsClick = { currentScreen = "tests" }
                )
            }

            "generateTest" -> editingGroupId?.let { groupId ->
                val group = groups.find { it.id == groupId }
                if (group != null) {
                    var selectedDifficulty by remember { mutableStateOf<String?>(null) }
                    
                    GenerateTestScreen(
                        groupName = group.name,
                        selectedDifficulty = selectedDifficulty,
                        onBackClick = { currentScreen = "editGroup" },
                        onDifficultySelected = { difficulty ->
                            selectedDifficulty = difficulty
                        },
                        onSendClick = {
                            if (selectedDifficulty != null) {
                                // Создаем тест согласно структуре БД
                                val testId = UUID.randomUUID()
                                val test = Test(
                                    id = testId,
                                    name = "${group.name} - ${when(selectedDifficulty) {
                                        "easy" -> "Легкий"
                                        "medium" -> "Средний"
                                        "hard" -> "Сложный"
                                        else -> "Легкий"
                                    }}",
                                    creatorId = group.ownerId
                                )
                                tests = tests + test
                                
                                // Создаем назначение теста группе (GroupTestAssignment в БД)
                                val assignment = GroupTestAssignment(
                                    groupId = groupId,
                                    testId = testId
                                )
                                groupTestAssignments = groupTestAssignments + assignment
                                
                                // Определяем количество вопросов в зависимости от сложности
                                val questionCount = when (selectedDifficulty) {
                                    "easy" -> 10
                                    "medium" -> 15
                                    "hard" -> 20
                                    else -> 10
                                }
                                
                                // Выбираем вопросы из пула (если есть доступные вопросы)
                                val availableQuestions = if (questions.isNotEmpty()) {
                                    questions.shuffled().take(questionCount)
                                } else {
                                    // Если вопросов нет, генерируем тестовые
                                    generateTestQuestions(questionCount).also { generated ->
                                        questions = questions + generated
                                    }
                                }
                                
                                // Создаем связи тест-вопрос (TestQuestion в БД)
                                val newTestQuestions = availableQuestions.mapIndexed { index, question ->
                                    TestQuestion(
                                        testId = testId,
                                        questionId = question.id,
                                        order = index + 1
                                    )
                                }
                                testQuestions = testQuestions + newTestQuestions
                                
                                // Создаем DTO для отображения (GroupTesting - не сущность БД)
                                val testing = GroupTesting(
                                    id = testId, // Используем testId как идентификатор
                                    testId = testId,
                                    groupId = groupId,
                                    groupName = group.name,
                                    difficulty = selectedDifficulty!!,
                                    publishedDate = System.currentTimeMillis(),
                                    creatorId = group.ownerId
                                )
                                groupTestings = groupTestings + testing
                                
                                currentScreen = "editGroup"
                            }
                        },
                        onHomeClick = { currentScreen = "home" },
                        onGroupsClick = { currentScreen = "groups" },
                        onTestsClick = { currentScreen = "tests" }
                    )
                }
            }

            "testingList" -> currentUser?.let { user ->
                // Получаем тестирования для групп пользователя (кроме созданных им)
                val userGroupIds = getUserGroups(user.id).map { it.id }.toSet()
                val availableTestings = groupTestings
                    .filter { it.groupId in userGroupIds && it.creatorId != user.id }
                    .sortedByDescending { it.publishedDate }
                
                TestingListScreen(
                    availableTestings = availableTestings,
                    onBackClick = { currentScreen = "tests" },
                    onTestingClick = { testing ->
                        selectedTesting = testing
                        showTestingConfirmation = true
                    },
                    onHomeClick = { currentScreen = "home" },
                    onGroupsClick = { currentScreen = "groups" },
                    onTestsClick = { currentScreen = "tests" }
                )
            }

            "trainingSessionDetail" -> selectedSessionId?.let { sessionId ->
                val session = testSessions.find { it.id == sessionId }
                if (session != null) {
                    // Получаем ответы для этой сессии (в порядке сохранения)
                    val sessionAnswers = userAnswers.filter { it.testSessionId == sessionId }
                    
                    // Получаем вопросы из ответов (в правильном порядке)
                    val sessionQuestions = sessionAnswers.mapNotNull { answer ->
                        questions.find { it.id == answer.questionId }
                    }
                    
                    var currentDetailQuestionIndex by remember { mutableStateOf(1) }
                    
                    if (sessionQuestions.isNotEmpty()) {
                        TrainingSessionDetailScreen(
                            questions = sessionQuestions,
                            userAnswers = sessionAnswers,
                            currentQuestionIndex = currentDetailQuestionIndex,
                            onQuestionIndexChange = { newIndex ->
                                currentDetailQuestionIndex = newIndex
                            },
                            onBackClick = { 
                                selectedSessionId = null
                                currentScreen = "trainingResults"
                            },
                            onHomeClick = { currentScreen = "home" },
                            onGroupsClick = { currentScreen = "groups" },
                            onTestsClick = { currentScreen = "tests" }
                        )
                    }
                }
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
                    onTestingClick = { 
                        currentScreen = "testingList"
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
                        onGenerateTestClick = { 
                            editingGroupId = group.id
                            currentScreen = "generateTest"
                        },
                        onCopyLink = { joinCode -> copyTextToClipboard(joinCode) },
                        onStatisticsClick = {
                            editingGroupId = group.id
                            currentScreen = "testStatistics"
                        }
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
                            // Сохраняем сессию
                            val sessionId = UUID.randomUUID()
                            
                            // Сохраняем вопросы в общий список (если их там еще нет)
                            val newQuestions = currentTestQuestions.filter { question ->
                                questions.none { it.id == question.id }
                            }
                            questions = questions + newQuestions
                            
                            // Сохраняем ответы пользователя
                            val savedAnswers = currentTestQuestions.mapIndexed { index, question ->
                                val userAnswer = currentTestAnswers[index + 1] ?: question.correctAnswer
                                val isCorrect = userAnswer == question.correctAnswer
                                
                                UserAnswer(
                                    id = UUID.randomUUID(),
                                    testSessionId = sessionId,
                                    questionId = question.id,
                                    selectedAnswer = userAnswer,
                                    isCorrect = isCorrect
                                )
                            }
                            userAnswers = userAnswers + savedAnswers
                            
                            // Подсчитываем результаты
                            val correctAnswers = savedAnswers.count { it.isCorrect }
                            
                            val session = TestSession(
                                id = sessionId,
                                testId = testId,
                                userId = user.id,
                                mode = TestMode.TRAINING,
                                startTime = System.currentTimeMillis(),
                                endTime = System.currentTimeMillis(),
                                score = (correctAnswers.toDouble() / currentTestQuestions.size * 100)
                            )
                            testSessions = testSessions + session
                            
                            // Сохраняем сложность для этой сессии
                            val difficulty = testDifficulty ?: "easy"
                            sessionDifficulties = sessionDifficulties + (sessionId to difficulty)
                            
                            // Переходим на экран результатов
                            currentScreen = "testResult"
                        },
                        onBackClick = { currentScreen = "trainingLevel" },
                        onHomeClick = { currentScreen = "home" },
                        onGroupsClick = { currentScreen = "groups" },
                        onTestsClick = { currentScreen = "tests" },
                        allowHints = true
                    )
                }
            }

            "examSession" -> currentUser?.let { user ->
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
                            // Сохраняем сессию
                            val sessionId = UUID.randomUUID()
                            
                            // Сохраняем вопросы в общий список (если их там еще нет)
                            val newQuestions = currentTestQuestions.filter { question ->
                                questions.none { it.id == question.id }
                            }
                            questions = questions + newQuestions
                            
                            // Сохраняем ответы пользователя
                            val savedAnswers = currentTestQuestions.mapIndexed { index, question ->
                                val userAnswer = currentTestAnswers[index + 1] ?: question.correctAnswer
                                val isCorrect = userAnswer == question.correctAnswer
                                
                                UserAnswer(
                                    id = UUID.randomUUID(),
                                    testSessionId = sessionId,
                                    questionId = question.id,
                                    selectedAnswer = userAnswer,
                                    isCorrect = isCorrect
                                )
                            }
                            userAnswers = userAnswers + savedAnswers
                            
                            // Подсчитываем результаты
                            val correctAnswers = savedAnswers.count { it.isCorrect }
                            
                            val session = TestSession(
                                id = sessionId,
                                testId = testId,
                                userId = user.id,
                                mode = TestMode.EXAM,
                                startTime = System.currentTimeMillis(),
                                endTime = System.currentTimeMillis(),
                                score = (correctAnswers.toDouble() / currentTestQuestions.size * 100)
                            )
                            testSessions = testSessions + session
                            
                            // Сохраняем сложность для этой сессии
                            val difficulty = testDifficulty ?: "easy"
                            sessionDifficulties = sessionDifficulties + (sessionId to difficulty)
                            
                            // Переходим на экран результатов тестирования
                            currentScreen = "testingResult"
                        },
                        onBackClick = { currentScreen = "testingList" },
                        onHomeClick = { currentScreen = "home" },
                        onGroupsClick = { currentScreen = "groups" },
                        onTestsClick = { currentScreen = "tests" },
                        allowHints = false // В режиме экзамена подсказки запрещены
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

            "testingResult" -> {
                val correctAnswers = currentTestQuestions.mapIndexed { index, question ->
                    val userAnswer = currentTestAnswers[index + 1]
                    userAnswer == question.correctAnswer
                }.count { it }
                val totalQuestions = currentTestQuestions.size
                
                TestResultScreen(
                    correctAnswers = correctAnswers,
                    totalQuestions = totalQuestions,
                    onBackClick = { currentScreen = "testingList" },
                    onHomeClick = { currentScreen = "home" },
                    onGroupsClick = { currentScreen = "groups" },
                    onTestsClick = { currentScreen = "tests" }
                )
            }

            "testStatistics" -> {
                val group = editingGroupId?.let { groups.find { it.id == editingGroupId } }
                val user = currentUser
                if (group != null && user != null) {
                    // Получаем все тесты, созданные создателем группы для этой группы
                    val creatorTests = groupTestings.filter { 
                        it.groupId == group.id && it.creatorId == group.ownerId 
                    }
                    
                    TestStatisticsScreen(
                        groupName = group.name,
                        tests = creatorTests,
                        onTestClick = { test ->
                            selectedTestForStatistics = test
                            currentScreen = "testDetailStatistics"
                        },
                        onBackClick = { currentScreen = "editGroup" },
                        onHomeClick = { currentScreen = "home" },
                        onGroupsClick = { currentScreen = "groups" },
                        onTestsClick = { currentScreen = "tests" }
                    )
                }
            }

            "testDetailStatistics" -> {
                val group = editingGroupId?.let { groups.find { it.id == editingGroupId } }
                val test = selectedTestForStatistics
                if (group != null && test != null) {
                    // Получаем всех участников группы
                    val groupMembers = getGroupMembers(group.id)
                    val memberUsers = users.map { it.toUser() }.filter { it.id in groupMembers }
                    
                    // Получаем все сессии для данного теста
                    val testSessionsForTest = testSessions.filter { it.testId == test.testId }
                    
                    TestDetailStatisticsScreen(
                        test = test,
                        participants = memberUsers,
                        testSessions = testSessionsForTest,
                        onBackClick = { 
                            selectedTestForStatistics = null
                            currentScreen = "testStatistics" 
                        },
                        onHomeClick = { currentScreen = "home" },
                        onGroupsClick = { currentScreen = "groups" },
                        onTestsClick = { currentScreen = "tests" }
                    )
                }
            }
        }

        // Диалог подтверждения начала тестирования
        if (showTestingConfirmation && selectedTesting != null) {
            val testing = selectedTesting!!
            val testName = tests.find { it.id == testing.testId }?.name ?: testing.groupName
            
            AlertDialog(
                onDismissRequest = { 
                    showTestingConfirmation = false
                    selectedTesting = null
                },
                title = { Text("Подтверждение") },
                text = { Text("Начать прохождение $testName теста?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showTestingConfirmation = false
                            // Начинаем тестирование
                            currentTestId = testing.testId
                            testDifficulty = testing.difficulty
                            currentTestQuestions = getTestQuestions(testing.testId, testing.difficulty)
                            currentTestAnswers = emptyMap()
                            currentQuestionIndex = 1
                            selectedTesting = null
                            currentScreen = "examSession"
                        }
                    ) {
                        Text("Да")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showTestingConfirmation = false
                            selectedTesting = null
                        }
                    ) {
                        Text("Нет")
                    }
                }
            )
        }
    }
}