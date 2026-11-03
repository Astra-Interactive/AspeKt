@file:Suppress("TooManyFunctions")

package ru.astrainteractive.aspekt.core.plugin.util

import ru.astrainteractive.aspekt.core.plugin.model.PluginTranslation
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.localization.component.PlaceholderReplacement
import ru.astrainteractive.astralibs.localization.component.replace
import ru.astrainteractive.astralibs.localization.component.replaceAll
import java.text.DecimalFormat
import kotlin.time.Duration

fun PluginTranslation.PlaytimeReward.rewarded(amount: Number): LocalizableComponent {
    return rewarded.replace(
        "%amount%",
        DecimalFormat("0.00").format(amount)
    )
}

fun PluginTranslation.Rtp.lowTickTime(tps: Double): LocalizableComponent {
    return lowTickTime.replace("%tps%", tps.toInt().toString())
}

fun PluginTranslation.Tpa.requestDenied(denier: String): LocalizableComponent {
    return requestDenied.replace("%player%", denier)
}

fun PluginTranslation.Tpa.requestTpa(requester: String): LocalizableComponent {
    return requestTpa.replace("%player%", requester)
}

fun PluginTranslation.Tpa.requestTpaHere(requester: String): LocalizableComponent {
    return requestTpaHere.replace("%player%", requester)
}

fun PluginTranslation.Homes.limitReached(limit: Int): LocalizableComponent {
    return limitReached.replace("%limit%", limit.toString())
}

fun PluginTranslation.Jails.list(jails: String): LocalizableComponent {
    return list.replace("%jails%", jails)
}

fun PluginTranslation.Jails.created(name: String): LocalizableComponent {
    return created.replace("%jail%", name)
}

fun PluginTranslation.Jails.deleted(name: String): LocalizableComponent {
    return deleted.replace("%jail%", name)
}

fun PluginTranslation.Jails.inmateJailed(name: String, jail: String): LocalizableComponent {
    return inmateJailed.replaceAll(
        PlaceholderReplacement.plain("%jail%", jail),
        PlaceholderReplacement.plain("%name%", name)
    )
}

fun PluginTranslation.Jails.inmateReleased(name: String): LocalizableComponent {
    return inmateReleased.replace("%name%", name)
}

fun PluginTranslation.Jails.hasInmates(name: String): LocalizableComponent {
    return hasInmates.replace("%jail%", name)
}

fun PluginTranslation.Jails.jailed(time: Duration): LocalizableComponent {
    return jailed.replace("%time%", time.toString())
}

fun PluginTranslation.Economy.balance(amount: Number): LocalizableComponent {
    return balance.replace(
        "%balance%",
        DecimalFormat("0.00").format(amount)
    )
}

fun PluginTranslation.Economy.currencies(value: String): LocalizableComponent {
    return currencies.replace("%currencies%", value)
}

fun PluginTranslation.Economy.topItem(index: Int, name: String, balance: Number): LocalizableComponent {
    return topItem.replaceAll(
        PlaceholderReplacement.plain("%index%", "$index"),
        PlaceholderReplacement.plain("%name%", name),
        PlaceholderReplacement.plain("%balance%", DecimalFormat("0.00").format(balance))
    )
}

fun PluginTranslation.ChatGame.solveRiddle(quiz: LocalizableComponent): LocalizableComponent {
    return solveRiddle.replace("%quiz%", quiz)
}

fun PluginTranslation.ChatGame.solveExample(quiz: String): LocalizableComponent {
    return solveExample.replace("%quiz%", quiz)
}

fun PluginTranslation.ChatGame.solveAnagram(quiz: String): LocalizableComponent {
    return solveAnagram.replace("%quiz%", quiz)
}

fun PluginTranslation.ChatGame.solveQuadratic(quiz: String): LocalizableComponent {
    return solveQuadratic.replace("%quiz%", quiz)
}

fun PluginTranslation.ChatGame.moneyRewarded(player: String, amount: Number): LocalizableComponent {
    return moneyRewarded.replaceAll(
        PlaceholderReplacement.plain("%player%", player),
        PlaceholderReplacement.plain("%amount%", DecimalFormat("0.00").format(amount))
    )
}

fun PluginTranslation.MoneyAdvancement.challengeCompleted(money: Number): LocalizableComponent {
    return challengeCompleted.replace(
        "%money%",
        DecimalFormat("0.00").format(money)
    )
}

fun PluginTranslation.MoneyAdvancement.goalCompleted(money: Number): LocalizableComponent {
    return goalCompleted.replace(
        "%money%",
        DecimalFormat("0.00").format(money)
    )
}

fun PluginTranslation.MoneyAdvancement.taskCompleted(money: Number): LocalizableComponent {
    return taskCompleted.replace(
        "%money%",
        DecimalFormat("0.00").format(money)
    )
}

fun PluginTranslation.MoneyDrop.pickedUp(amount: Number): LocalizableComponent {
    return pickedUp.replace("{AMOUNT}", DecimalFormat("0.00").format(amount))
}

fun PluginTranslation.Claim.actionBlocked(action: String): LocalizableComponent {
    return actionBlocked.replace("%action%", action)
}

fun PluginTranslation.Swear.enabledFor(name: String): LocalizableComponent {
    return enabledFor.replace("{player}", name)
}

fun PluginTranslation.Swear.disabledFor(name: String): LocalizableComponent {
    return disabledFor.replace("{player}", name)
}
