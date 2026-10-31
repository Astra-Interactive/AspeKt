package ru.astrainteractive.aspekt.module.auth.api.util

import java.security.MessageDigest
import java.util.Locale

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold(initial = "", operation = { str, byte -> str + "%02x".format(Locale.ROOT, byte) })
}

fun String.sha256(): String {
    return hashString(this, "SHA-256")
}
