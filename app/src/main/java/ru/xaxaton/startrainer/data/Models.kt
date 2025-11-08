package ru.xaxaton.startrainer.data

import java.util.UUID

/**
 * Модель пользователя, соответствующая ApplicationUser из базы данных
 */
data class User(
    val id: UUID,
    val fullName: String, // Полное имя (фамилия имя отчество)
    val email: String
) {
    /**
     * Разбивает FullName на компоненты для обратной совместимости
     */
    fun getFamily(): String {
        return fullName.split(" ").firstOrNull() ?: ""
    }

    fun getName(): String {
        val parts = fullName.split(" ")
        return if (parts.size > 1) parts[1] else ""
    }

    fun getPatronymic(): String {
        val parts = fullName.split(" ")
        return if (parts.size > 2) parts[2] else ""
    }

    /**
     * Форматированное имя для отображения (Фамилия И.О.)
     */
    fun getFormattedName(): String {
        val family = getFamily()
        val name = getName()
        val patronymic = getPatronymic()
        return buildString {
            append(family)
            if (name.isNotEmpty()) append(" ${name.first()}.")
            if (patronymic.isNotEmpty()) append("${patronymic.first()}.")
        }
    }
}

/**
 * Модель пользователя с данными аутентификации для локального хранения
 * (пароль хранится в ASP.NET Identity на бэкенде, здесь только для локального состояния)
 */
data class SimpleUser(
    val id: UUID,
    val family: String,
    val name: String,
    val patronymic: String,
    val email: String,
    val passwordHash: String,
    val salt: String
) {
    /**
     * Преобразует SimpleUser в User (для работы с БД)
     */
    fun toUser(): User {
        val fullName = buildString {
            append(family)
            if (name.isNotEmpty()) append(" $name")
            if (patronymic.isNotEmpty()) append(" $patronymic")
        }.trim()
        return User(id, fullName, email)
    }

    /**
     * Форматированное имя для отображения
     */
    fun getFormattedName(): String {
        return buildString {
            append(family)
            if (name.isNotEmpty()) append(" ${name.first()}.")
            if (patronymic.isNotEmpty()) append("${patronymic.first()}.")
        }
    }
}

/**
 * Модель группы, соответствующая Group из базы данных
 */
data class Group(
    val id: UUID,
    val name: String,
    val ownerId: UUID
) {
    /**
     * Код для вступления в группу (генерируется на основе ID)
     * В реальном приложении это может быть отдельное поле в БД или генерироваться на бэкенде
     */
    fun getJoinCode(): String {
        // Генерируем код из первых символов UUID
        return id.toString().take(6).uppercase().filter { it.isLetterOrDigit() }
    }
}

/**
 * Модель членства в группе, соответствующая GroupMembership из базы данных
 */
data class GroupMembership(
    val userId: UUID,
    val groupId: UUID
)

/**
 * Модель теста, соответствующая Test из базы данных
 */
data class Test(
    val id: UUID,
    val name: String,
    val creatorId: UUID
)

/**
 * Модель вопроса, соответствующая Question из базы данных
 */
data class Question(
    val id: UUID,
    val description: String,
    val imageUrl: String?,
    val correctAnswer: TriageCategory,
    val hint: String?
)

/**
 * Модель связи теста и вопроса, соответствующая TestQuestion из базы данных
 */
data class TestQuestion(
    val testId: UUID,
    val questionId: UUID,
    val order: Int
)

/**
 * Модель сессии теста, соответствующая TestSession из базы данных
 */
data class TestSession(
    val id: UUID,
    val testId: UUID,
    val userId: UUID,
    val mode: TestMode,
    val startTime: Long, // Unix timestamp в миллисекундах
    val endTime: Long?, // Unix timestamp в миллисекундах
    val score: Double? // Процент правильных ответов (0-100)
)

/**
 * Модель ответа пользователя, соответствующая UserAnswer из базы данных
 */
data class UserAnswer(
    val id: UUID,
    val testSessionId: UUID,
    val questionId: UUID,
    val selectedAnswer: TriageCategory,
    val isCorrect: Boolean
)

/**
 * Модель назначения теста группе, соответствующая GroupTestAssignment из базы данных
 */
data class GroupTestAssignment(
    val groupId: UUID,
    val testId: UUID
)

/**
 * Модель тестирования, назначенного группе (для отображения пользователям)
 */
data class GroupTesting(
    val id: UUID,
    val testId: UUID,
    val groupId: UUID,
    val groupName: String,
    val difficulty: String, // "easy", "medium", "hard"
    val publishedDate: Long, // Unix timestamp в миллисекундах
    val creatorId: UUID
)

/**
 * Режим прохождения теста
 */
enum class TestMode {
    TRAINING, // Режим обучения с подсказками
    EXAM      // Экзаменационный режим
}

/**
 * Категория триажа (медицинская сортировка)
 */
enum class TriageCategory {
    RED,    // Первая очередь, неотложная помощь
    YELLOW, // Вторая очередь, срочная помощь
    GREEN,  // Третья очередь, отложенная помощь
    BLACK   // Погибшие или агонизирующие
}

/**
 * Вспомогательная модель для отображения группы с дополнительной информацией
 */
data class GroupWithDetails(
    val group: Group,
    val owner: User?,
    val members: List<User>,
    val assignedTests: List<Test>
)

/**
 * Вспомогательная модель для отображения теста с вопросами
 */
data class TestWithQuestions(
    val test: Test,
    val questions: List<QuestionWithOrder>
)

/**
 * Вопрос с порядковым номером в тесте
 */
data class QuestionWithOrder(
    val question: Question,
    val order: Int
)

/**
 * Константа для базового URL API (будет настраиваться через конфигурацию)
 */
object ApiConfig {
    // TODO: В реальном приложении это должно быть в конфигурации или получаться из buildConfig
    const val BASE_URL = "http://localhost:8080" // или ваш URL API
    
    /**
     * Формирует полный URL для изображения из относительного пути
     */
    fun getImageUrl(relativePath: String?): String? {
        return relativePath?.let { 
            if (it.startsWith("http://") || it.startsWith("https://")) {
                it // Уже полный URL
            } else {
                "$BASE_URL$it" // Добавляем базовый URL
            }
        }
    }
}

/**
 * Результат сессии теста для отображения
 */
data class TestSessionResult(
    val session: TestSession,
    val test: Test,
    val answers: List<UserAnswerWithQuestion>
)

/**
 * Ответ пользователя с информацией о вопросе
 */
data class UserAnswerWithQuestion(
    val answer: UserAnswer,
    val question: Question
)

