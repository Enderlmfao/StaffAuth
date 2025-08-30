package com.ender.staffAuth

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.security.MessageDigest

object PasswordHashing {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun hashPassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    fun checkPassword(password: String, hashedPassword: String): Boolean {
        return passwordEncoder.matches(password, hashedPassword)
    }

    fun hashIp(ip: String, salt: String): String {
        val bytes = (ip + salt).toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}