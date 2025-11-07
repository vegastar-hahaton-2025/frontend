package ru.xaxaton.startrainer.ui.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.xaxaton.startrainer.ui.components.BottomCreamWave
import ru.xaxaton.startrainer.ui.components.TopCreamWave
import ru.xaxaton.startrainer.ui.theme.CreamWhite
import ru.xaxaton.startrainer.ui.theme.STARTrainerTheme
import ru.xaxaton.startrainer.utils.hashPasswordWithSalt
import androidx.compose.material.icons.filled.ArrowBack

data class SimpleUser(
    val family: String,
    val name: String,
    val patronymic: String,
    val email: String,
    val passwordHash: String,
    val salt: String
)

data class Group(
    val id: String,
    val name: String,
    val creatorEmail: String,
    val joinCode: String,
    val members: List<String> = emptyList()
)

@Composable
fun RegistrationScreen(
    onBackClick: () -> Unit,
    onRegister: (SimpleUser) -> Unit,
    existingUsers: List<SimpleUser> = emptyList()
) {
    val colorScheme = MaterialTheme.colorScheme

    var surname by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var patronymic by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primary)
    ) {
        TopCreamWave(modifier = Modifier.align(Alignment.TopCenter))
        BottomCreamWave(modifier = Modifier.align(Alignment.BottomCenter))

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = Color.Black,
                modifier = Modifier.size(48.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CreamWhite
            )

            Spacer(modifier = Modifier.height(20.dp))

            val fieldColors = TextFieldDefaults.colors(
                focusedContainerColor = CreamWhite,
                unfocusedContainerColor = CreamWhite,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )

            fun validateEmail(email: String): Boolean {
                if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                    return false
                }

                val parts = email.trim().split("@")
                if (parts.size != 2) return false

                val domain = parts[1]
                val domainParts = domain.split(".")
                if (domainParts.size < 2) return false

                val tld = domainParts.last()
                if (tld.length < 2) return false

                if (!domain.matches(Regex("^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))) {
                    return false
                }

                return true
            }

            fun validateAndRegister() {
                // Проверка  полей
                when {
                    surname.trim().isEmpty() -> {
                        errorMessage = "Фамилия обязательна для заполнения"
                        return
                    }
                    name.trim().isEmpty() -> {
                        errorMessage = "Имя обязательно для заполнения"
                        return
                    }
                    email.trim().isEmpty() -> {
                        errorMessage = "Почта обязательна для заполнения"
                        return
                    }
                    password.isEmpty() -> {
                        errorMessage = "Пароль обязателен для заполнения"
                        return
                    }
                    !validateEmail(email) -> {
                        errorMessage = "Введите корректный адрес почты"
                        return
                    }
                    existingUsers.any { it.email.equals(email.trim(), ignoreCase = true) } -> {
                        errorMessage = "Эта почта уже зарегистрирована"
                        return
                    }
                    password.length < 6 -> {
                        errorMessage = "Пароль должен содержать минимум 6 символов"
                        return
                    }
                    else -> {
                        val (hashHex, saltHex) = hashPasswordWithSalt(password)
                        val user = SimpleUser(
                            family = surname.trim(),
                            name = name.trim(),
                            patronymic = patronymic.trim(),
                            email = email.trim(),
                            passwordHash = hashHex,
                            salt = saltHex
                        )
                        errorMessage = null
                        onRegister(user)
                    }
                }
            }

            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                label = {
                    Text(
                        text = "Фамилия *",
                        color = Color.Black
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
                isError = errorMessage == "Фамилия обязательна для заполнения"
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(
                        text = "Имя *",
                        color = Color.Black
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
                isError = errorMessage == "Имя обязательно для заполнения"
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = patronymic,
                onValueChange = { patronymic = it },
                label = {
                    Text(
                        text = "Отчество",
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        text = "Почта *",
                        color = Color.Black
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
                isError = errorMessage == "Почта обязательна для заполнения" ||
                        errorMessage == "Введите корректный адрес почты" ||
                        errorMessage == "Эта почта уже зарегистрирована"
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (errorMessage == "Пароль обязателен для заполнения" ||
                        errorMessage == "Пароль должен содержать минимум 6 символов") {
                        errorMessage = null
                    }
                },
                label = { Text("Пароль *") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
                isError = errorMessage == "Пароль обязателен для заполнения" ||
                        errorMessage == "Пароль должен содержать минимум 6 символов"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { validateAndRegister() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CreamWhite,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Зарегистрироваться", style = MaterialTheme.typography.titleMedium)
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "* обязательные поля",
                color = CreamWhite.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    STARTrainerTheme {
        RegistrationScreen(onBackClick = {}, onRegister = {})
    }
}