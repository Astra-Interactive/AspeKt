package ru.astrainteractive.aspekt.module.auth.api.plugin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.localization.locale.MinecraftLocales
import ru.astrainteractive.astralibs.localization.text.LocalizedText

@Serializable
data class AuthTranslation(
    @SerialName("prefix")
    val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BAUTH&7] "),
    @SerialName("not_authorized")
    val notAuthorized: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.EN_US, "You are not logged in! /login PASSWORD")
            translation(MinecraftLocales.RU_RU, "Вы не авторизованы! /login ПАРОЛЬ")
        }
    ),
    @SerialName("not_registered")
    val notRegistered: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.EN_US, "You are not registered! /register PASSWORD PASSWORD")
            translation(MinecraftLocales.RU_RU, "Вы не зарегистрированы! /register ПАРОЛЬ ПАРОЛЬ")
        }
    ),
    @SerialName("auth_success")
    val authSuccess: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.EN_US, "You have logged in!")
            translation(MinecraftLocales.RU_RU, "Вы успешно авторизованы!")
        }
    ),
    @SerialName("wrong_password")
    val wrongPassword: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.EN_US, "Wrong password!")
            translation(MinecraftLocales.RU_RU, "Пароль неверный!")
        }
    ),
    @SerialName("user_not_found")
    val userNotFound: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.EN_US, "User not found!")
            translation(MinecraftLocales.RU_RU, "Пользователь не найден!")
        }
    ),
    @SerialName("user_deleted")
    val userDeleted: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.EN_US, "User deleted!")
            translation(MinecraftLocales.RU_RU, "Пользователь удален!")
        }
    ),
    @SerialName("user_could_not_be_deleted")
    val userCouldNotBeDeleted: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.EN_US, "Could not delete the user!")
            translation(MinecraftLocales.RU_RU, "Не удалось удалить пользователя!")
        }
    ),
    @SerialName("already_registered")
    val alreadyRegistered: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.EN_US, "You are already registered!")
            translation(MinecraftLocales.RU_RU, "Вы уже зарегистрированы!")
        }
    ),
    @SerialName("account_created")
    val accountCreated: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.EN_US, "Account created!")
            translation(MinecraftLocales.RU_RU, "Аккаунт создан успешно!")
        }
    ),
    @SerialName("could_not_create_account")
    val couldNotCreateAccount: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.EN_US, "Could not create the account!")
            translation(MinecraftLocales.RU_RU, "Не удалось создать аккаунт!")
        }
    ),
)
