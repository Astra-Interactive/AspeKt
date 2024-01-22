package ru.astrainteractive.aspekt.module.autocrop.presentation

import org.bukkit.Location
import org.jetbrains.kotlin.com.google.common.cache.Cache
import org.jetbrains.kotlin.com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit

internal class CropDupeController {
    private val dropCache: Cache<String, Unit> = CacheBuilder
        .newBuilder()
        .maximumSize(64)
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build()

    private fun Location.toKeyLocation() = "${x.toInt()}${y.toInt()}${z.toInt()}"

    fun isDupingAtLocation(location: Location): Boolean {
        val isPresent = dropCache.getIfPresent(location.toKeyLocation()) != null
        dropCache.put(location.toKeyLocation(), Unit)
        return isPresent
    }
}
