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
 * 
 * Соответствие с БД:
 * - startTime/endTime: в БД это DateTime (timestamp with time zone),
 *   здесь Long (Unix timestamp в миллисекундах) для удобства работы на Android
 * - score: в БД это decimal? (numeric(5,2)), здесь Double? (0-100)
 * - mode: в БД это smallint (1=Training, 2=Exam), здесь enum TestMode
 */
data class TestSession(
    val id: UUID,
    val testId: UUID,
    val userId: UUID,
    val mode: TestMode,
    val startTime: Long, // Unix timestamp в миллисекундах (в БД: DateTime)
    val endTime: Long?, // Unix timestamp в миллисекундах (в БД: DateTime?)
    val score: Double? // Процент правильных ответов (0-100), в БД: decimal? (numeric(5,2))
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
 * 
 * ВАЖНО: Это DTO/вспомогательная модель для отображения, НЕ сущность БД.
 * В БД нет таблицы GroupTesting. Эта модель представляет комбинацию:
 * - Test (тест)
 * - GroupTestAssignment (назначение теста группе)
 * - Group (группа) - для получения groupName
 * 
 * Поле difficulty не хранится в БД, используется только на клиенте
 * для определения количества вопросов (easy=10, medium=15, hard=20).
 */
data class GroupTesting(
    val id: UUID, // ID для локального использования (может быть testId или UUID.randomUUID())
    val testId: UUID, // Ссылка на Test в БД
    val groupId: UUID, // Ссылка на Group в БД
    val groupName: String, // Название группы (для отображения)
    val difficulty: String, // "easy", "medium", "hard" - локальное поле, не хранится в БД
    val publishedDate: Long, // Unix timestamp в миллисекундах - локальное поле
    val creatorId: UUID // ID создателя теста
)

/**
 * Режим прохождения теста
 * Значения соответствуют enum TestMode в БД (Training = 1, Exam = 2)
 */
enum class TestMode(val value: Int) {
    TRAINING(1), // Режим обучения с подсказками
    EXAM(2)      // Экзаменационный режим
}

/**
 * Категория триажа (медицинская сортировка)
 * Значения соответствуют enum TriageCategory в БД (Red = 1, Yellow = 2, Green = 3, Black = 4)
 */
enum class TriageCategory(val value: Int) {
    RED(1),    // Первая очередь, неотложная помощь
    YELLOW(2), // Вторая очередь, срочная помощь
    GREEN(3),  // Третья очередь, отложенная помощь
    BLACK(4)   // Погибшие или агонизирующие
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

