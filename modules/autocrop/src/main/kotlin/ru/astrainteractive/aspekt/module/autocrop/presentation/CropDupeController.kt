package ru.astrainteractive.aspekt.module.autocrop.presentation

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import org.bukkit.Location
import java.util.concurrent.TimeUnit

internal class CropDupeController {
    // Worst size for 1 item is 2b(1 char)*5(Max map size is 10000)*3(X,Y,Z) = 30
    // Then for 2048 -> 2048*30 = 61440 Bytes = 0.05859375 MB
    private val dropCache: Cache<String, Unit> = CacheBuilder
        .newBuilder()
        .maximumSize(2048)
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build()

    private fun Location.toKeyLocation() = "${x.toInt()}${y.toInt()}${z.toInt()}"

    fun isDupingAtLocation(location: Location): Boolean {
        val isPresent = dropCache.getIfPresent(location.toKeyLocation()) != null
        dropCache.put(location.toKeyLocation(), Unit)
        return isPresent
    }
}
