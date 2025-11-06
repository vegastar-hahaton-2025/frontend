package ru.xaxaton.startrainer.utils

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom

fun hashPasswordWithSalt(password: String): Pair<String, String> {
    val random = SecureRandom()
    val salt = ByteArray(16)
    random.nextBytes(salt)
    val md = MessageDigest.getInstance("SHA-256")
    md.update(salt)
    val digest = md.digest(password.toByteArray(StandardCharsets.UTF_8))
    return bytesToHex(digest) to bytesToHex(salt)
}

fun verifyPassword(password: String, storedHash: String, storedSalt: String): Boolean {
    val md = MessageDigest.getInstance("SHA-256")
    val saltBytes = hexToBytes(storedSalt)
    md.update(saltBytes)
    val digest = md.digest(password.toByteArray(StandardCharsets.UTF_8))
    return bytesToHex(digest) == storedHash
}

fun hexToBytes(hex: String): ByteArray {
    val result = ByteArray(hex.length / 2)
    for (i in hex.indices step 2) {
        val byte = hex.substring(i, i + 2).toInt(16).toByte()
        result[i / 2] = byte
    }
    return result
}

fun bytesToHex(bytes: ByteArray): String {
    val sb = StringBuilder(bytes.size * 2)
    bytes.forEach { b ->
        val hex = ((b.toInt() and 0xFF) + 0x100).toString(16).substring(1)
        sb.append(hex)
    }
    return sb.toString()
}
