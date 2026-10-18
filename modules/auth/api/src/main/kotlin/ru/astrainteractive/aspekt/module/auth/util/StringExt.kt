package ru.astrainteractive.aspekt.module.auth.util

import java.security.MessageDigest

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold(initial = "", operation = { str, byte -> str + "%02x".format(byte) })
}

fun String.sha256(): String {
    return hashString(this, "SHA-256")
}
