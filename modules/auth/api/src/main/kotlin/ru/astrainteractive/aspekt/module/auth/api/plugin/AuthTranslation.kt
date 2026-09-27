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
            translation(MinecraftLocales.RU_RU, "Вы не авторизованы! /login ПАРОЛЬ")
            translation(MinecraftLocales.EN_US, "You are not logged in! /login PASSWORD")
        }
    ),
    @SerialName("not_registered")
    val notRegistered: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "Вы не зарегистрированы! /register ПАРОЛЬ ПАРОЛЬ")
            translation(MinecraftLocales.EN_US, "You are not registered! /register PASSWORD PASSWORD")
        }
    ),
    @SerialName("only_player_command")
    val onlyPlayerCommand: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "Команда только для игроков!")
            translation(MinecraftLocales.EN_US, "This command is for players only!")
        }
    ),
    @SerialName("auth_success")
    val authSuccess: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "Вы успешно авторизованы!")
            translation(MinecraftLocales.EN_US, "You have logged in!")
        }
    ),
    @SerialName("wrong_password")
    val wrongPassword: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "Пароль неверный!")
            translation(MinecraftLocales.EN_US, "Wrong password!")
        }
    ),
    @SerialName("user_not_found")
    val userNotFound: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "Пользователь не найден!")
            translation(MinecraftLocales.EN_US, "User not found!")
        }
    ),
    @SerialName("user_deleted")
    val userDeleted: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "Пользователь удален!")
            translation(MinecraftLocales.EN_US, "User deleted!")
        }
    ),
    @SerialName("user_could_not_be_deleted")
    val userCouldNotBeDeleted: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "Не удалось удалить пользователя!")
            translation(MinecraftLocales.EN_US, "Could not delete the user!")
        }
    ),
    @SerialName("already_registered")
    val alreadyRegistered: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "Вы уже зарегистрированы!")
            translation(MinecraftLocales.EN_US, "You are already registered!")
        }
    ),
    @SerialName("account_created")
    val accountCreated: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "Аккаунт создан успешно!")
            translation(MinecraftLocales.EN_US, "Account created!")
        }
    ),
    @SerialName("could_not_create_account")
    val couldNotCreateAccount: LocalizedText = prefix.concat(
        LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "Не удалось создать аккаунт!")
            translation(MinecraftLocales.EN_US, "Could not create the account!")
        }
    ),
)
