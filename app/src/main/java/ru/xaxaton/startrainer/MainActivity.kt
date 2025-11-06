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
    var currentScreen by remember { mutableStateOf("start") }
    var users by remember { mutableStateOf(listOf<SimpleUser>()) }
    var currentUser by remember { mutableStateOf<SimpleUser?>(null) }

    // Для восстановления пароля
    var recoveryEmail by remember { mutableStateOf("") }
    var generatedCode by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (currentScreen) {

            // 🟡 Стартовый экран
            "start" -> StartScreen(
                onRegisterClick = { currentScreen = "register" },
                onLoginClick = { currentScreen = "login" },
                modifier = Modifier.padding(innerPadding)
            )

            // 🟢 Регистрация
            "register" -> RegistrationScreen(
                onBackClick = { currentScreen = "start" },
                existingUsers = users,
                onRegister = { newUser ->
                    users = users + newUser
                    currentUser = newUser
                    currentScreen = "home" // автоматический вход
                }
            )

            // 🔵 Вход
            "login" -> LoginScreen(
                onBackClick = { currentScreen = "start" },
                users = users,
                onLoginSuccess = { loggedInUser ->
                    currentUser = loggedInUser
                    currentScreen = "home"
                },
                onUsersUpdate = { updatedList ->
                    users = updatedList
                },
                onPasswordRecoveryClick = {
                    currentScreen = "password_recovery"
                }
            )

            // 🟣 Ввод почты для восстановления
            "password_recovery" -> PasswordRecoveryScreen(
                onBackClick = { currentScreen = "login" },
                users = users,
                onCodeSent = { email, code ->
                    recoveryEmail = email
                    generatedCode = code
                    currentScreen = "password_recovery_code"
                }
            )

            // 🟠 Ввод кода подтверждения
            "password_recovery_code" -> PasswordRecoveryCodeScreen(
                email = recoveryEmail,
                sentCode = generatedCode,
                onBackClick = { currentScreen = "password_recovery" },
                onCodeVerified = {
                    currentScreen = "password_recovery_new_password"
                }
            )

            // 🔴 Ввод нового пароля
            "password_recovery_new_password" -> PasswordRecoveryNewPasswordScreen(
                onBackClick = { currentScreen = "login" },
                users = users,
                onPasswordChanged = { updatedUsers ->
                    users = updatedUsers
                    currentScreen = "start" // возвращаемся на главный экран
                }
            )

            // 🟢 Домашняя страница после входа
            "home" -> HomeScreen(
                user = currentUser,
                onLogout = {
                    currentUser = null
                    currentScreen = "start"
                }
            )
        }
    }
}
