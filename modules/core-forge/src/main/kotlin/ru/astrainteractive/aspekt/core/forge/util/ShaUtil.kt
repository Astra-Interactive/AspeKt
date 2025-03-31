package ru.astrainteractive.aspekt.core.forge.util

import java.security.MessageDigest

fun String.sha256(): String {
    return hashString(this, "SHA-256")
}

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold(initial = "", operation = { str, byte -> str + "%02x".format(byte) })
}
