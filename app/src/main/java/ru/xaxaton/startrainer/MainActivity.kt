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
                    currentScreen = "home" // ✅ Автоматический вход после регистрации
                }
            )

            "login" -> LoginScreen(
                onBackClick = { currentScreen = "start" },
                users = users,
                onLoginSuccess = { loggedInUser ->
                    currentUser = loggedInUser
                    currentScreen = "home"
                },
                onUsersUpdate = { updatedList ->  // ✅ добавлено, чтобы сохранять новые пароли
                    users = updatedList
                }
            )

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
