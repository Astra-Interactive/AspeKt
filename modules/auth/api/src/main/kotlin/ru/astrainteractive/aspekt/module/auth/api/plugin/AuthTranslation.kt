package ru.astrainteractive.aspekt.module.auth.api.plugin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.StringDescExt.plus

@Serializable
data class AuthTranslation(
    @SerialName("prefix")
    val prefix: StringDesc.Raw = StringDesc.Raw("&7[&#DBB72BAUTH&7] "),
    @SerialName("not_authorized")
    val notAuthorized: StringDesc.Raw = prefix
        .plus("Вы не авторизованы! /login ПАРОЛЬ")
        .toRaw(),
    @SerialName("not_registered")
    val notRegistered: StringDesc.Raw = prefix
        .plus("Вы не зарегистрированы! /register ПАРОЛЬ ПАРОЛЬ")
        .toRaw(),
    @SerialName("only_player_command")
    val onlyPlayerCommand: StringDesc.Raw = prefix
        .plus("Команда только для игроков!")
        .toRaw(),
    @SerialName("auth_success")
    val authSuccess: StringDesc.Raw = prefix
        .plus("Вы успешно авторизованы!")
        .toRaw(),
    @SerialName("wrong_password")
    val wrongPassword: StringDesc.Raw = prefix
        .plus("Пароль неверный!")
        .toRaw(),
    @SerialName("user_not_found")
    val userNotFound: StringDesc.Raw = prefix
        .plus("Пользователь не найден!")
        .toRaw(),
    @SerialName("user_deleted")
    val userDeleted: StringDesc.Raw = prefix
        .plus("Пользователь удален!")
        .toRaw(),
    @SerialName("user_could_not_be_deleted")
    val userCouldNotBeDeleted: StringDesc.Raw = prefix
        .plus("Не удалось удалить пользователя!")
        .toRaw(),
    @SerialName("already_registered")
    val alreadyRegistered: StringDesc.Raw = prefix
        .plus("Вы уже зарегистрированы!")
        .toRaw(),
    @SerialName("account_created")
    val accountCreated: StringDesc.Raw = prefix
        .plus("Аккаунт создан успешно!")
        .toRaw(),
    @SerialName("could_not_create_account")
    val couldNotCreateAccount: StringDesc.Raw = prefix
        .plus("Не удалось создать аккаунт!")
        .toRaw(),
)

fun StringDesc.toRaw(): StringDesc.Raw {
    return StringDesc.Raw(this.raw)
}
