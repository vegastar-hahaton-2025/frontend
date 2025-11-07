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
    var editingGroupId by remember { mutableStateOf<String?>(null) }

    // Генерация кода группы
    fun generateGroupCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars.random() }.joinToString("")
    }

    // Создание группы
    fun createGroup(name: String, creatorEmail: String) {
        val newGroup = Group(
            id = java.util.UUID.randomUUID().toString(),
            name = name,
            creatorEmail = creatorEmail,
            joinCode = generateGroupCode(),
            members = listOf(creatorEmail)
        )
        groups = groups + newGroup
    }

    // Вступление в группу по коду
    fun joinGroupByCode(code: String, userEmail: String): JoinResult {
        val group = groups.find { it.joinCode == code } ?: return JoinResult.InvalidCode
        if (group.members.contains(userEmail) || group.creatorEmail == userEmail) return JoinResult.AlreadyMember
        val updatedGroup = group.copy(members = group.members + userEmail)
        groups = groups.map { if (it.id == group.id) updatedGroup else it }
        return JoinResult.Success
    }

    // Выход из группы
    fun leaveGroup(groupId: String, userEmail: String) {
        val group = groups.find { it.id == groupId }
        if (group != null && group.members.contains(userEmail) && group.creatorEmail != userEmail) {
            val updatedGroup = group.copy(members = group.members.filter { it != userEmail })
            groups = groups.map { if (it.id == groupId) updatedGroup else it }
        }
    }

    // Удаление участника (для создателя)
    fun removeMemberFromGroup(groupId: String, memberEmail: String) {
        val group = groups.find { it.id == groupId }
        if (group != null && group.members.contains(memberEmail) && memberEmail != group.creatorEmail) {
            val updatedGroup = group.copy(members = group.members.filter { it != memberEmail })
            groups = groups.map { if (it.id == groupId) updatedGroup else it }
        }
    }

    // Редактирование названия группы
    fun updateGroupName(groupId: String, newName: String) {
        val group = groups.find { it.id == groupId }
        if (group != null) {
            val updatedGroup = group.copy(name = newName)
            groups = groups.map { if (it.id == groupId) updatedGroup else it }
        }
    }

    // Удаление группы
    fun deleteGroup(groupId: String) {
        groups = groups.filter { it.id != groupId }
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

            "editProfile" -> EditProfileScreen(
                user = currentUser,
                onBackClick = { currentScreen = "home" },
                onHomeClick = { currentScreen = "home" },
                onTestsClick = { currentScreen = "tests" },
                onSaveChanges = { updatedUser ->
                    users = users.map { if (it.email == currentUser?.email) updatedUser else it }
                    currentUser = updatedUser
                    currentScreen = "home"
                },
                onChangePasswordClick = { currentScreen = "changePassword" }
            )

            "changePassword" -> currentUser?.let { user ->
                ChangePasswordScreen(
                    user = user,
                    onBackClick = { currentScreen = "editProfile" },
                    onHomeClick = { currentScreen = "home" },
                    onTestsClick = { currentScreen = "tests" },
                    onPasswordChanged = { updated ->
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
                onResultsClick = { currentScreen = "home" },
                onEditProfileClick = { currentScreen = "editProfile" },
                onTestsClick = { currentScreen = "tests" },
                onGroupsClick = { currentScreen = "groups" }
            )

            "tests" -> TestsChooseScreen(
                onHomeClick = { currentScreen = "home" },
                onGroupsClick = { currentScreen = "groups" },
                onTrainingClick = { currentScreen = "trainingLevel" },
                onTestingClick = { /* TODO */ }
            )

            "groups" -> currentUser?.let { user ->
                GroupsScreen(
                    onBackClick = { currentScreen = "home" },
                    onHomeClick = { currentScreen = "home" },
                    onTestsClick = { currentScreen = "tests" },
                    onCreateGroupClick = { currentScreen = "createGroup" },
                    onJoinGroupClick = { currentScreen = "joinGroup" },
                    onEditGroupClick = { groupId ->
                        editingGroupId = groupId
                        currentScreen = "editGroup"
                    },
                    onLeaveGroup = { groupId -> leaveGroup(groupId, user.email) },
                    groups = groups.filter {
                        it.creatorEmail == user.email || it.members.contains(user.email)
                    },
                    userEmail = user.email
                )
            }

            "joinGroup" -> currentUser?.let { user ->
                JoinGroupScreen(
                    onBackClick = { currentScreen = "groups" },
                    onHomeClick = { currentScreen = "home" },
                    onTestsClick = { currentScreen = "tests" },
                    onJoinGroup = { code -> joinGroupByCode(code, user.email) },
                    userEmail = user.email,
                    onSuccess = { currentScreen = "groups" }
                )
            }

            "createGroup" -> currentUser?.let { user ->
                CreateGroupScreen(
                    onBackClick = { currentScreen = "groups" },
                    onHomeClick = { currentScreen = "home" },
                    onTestsClick = { currentScreen = "tests" },
                    onCreateGroup = { groupName ->
                        createGroup(groupName, user.email)
                        currentScreen = "groups"
                    }
                )
            }

            "editGroup" -> {
                val group = groups.find { it.id == editingGroupId }
                if (group != null && currentUser != null) {
                    EditGroupScreen(
                        group = group,
                        users = users,
                        onBackClick = { currentScreen = "groups" },
                        onHomeClick = { currentScreen = "home" },
                        onTestsClick = { currentScreen = "tests" },
                        onEditClick = { currentScreen = "editGroupName" },
                        onRemoveMember = { memberEmail -> removeMemberFromGroup(group.id, memberEmail) },
                        onCopyLink = { joinCode -> copyTextToClipboard(joinCode) }
                    )
                }
            }

            "editGroupName" -> {
                val group = groups.find { it.id == editingGroupId }
                if (group != null && currentUser != null) {
                    EditGroupNameScreen(
                        group = group,
                        onBackClick = { currentScreen = "editGroup" },
                        onHomeClick = { currentScreen = "home" },
                        onTestsClick = { currentScreen = "tests" },
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

            "trainingLevel" -> TrainingLevelScreen(
                onBackClick = { currentScreen = "tests" },
                onHomeClick = { currentScreen = "home" },
                onLevelSelected = { level -> println("Выбран уровень: $level") }
            )
        }
    }
}
