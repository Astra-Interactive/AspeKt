@file:Suppress("ImplementationVisibility")

package ru.astrainteractive.aspekt.module.claims.api.debounce.memory

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import ru.astrainteractive.aspekt.module.claims.api.debounce.api.RetractKey
import ru.astrainteractive.aspekt.module.claims.api.debounce.api.SharedCancellableEvent
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

/*
 * WorldGuard, a suite of tools for Minecraft
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldGuard team and contributors
 */
class EventDebounce<K : RetractKey>(debounceTime: Duration) {
    sealed class Entry(val isCancelled: Boolean) {
        data object Pending : Entry(false)
        class Loaded(isCancelled: Boolean) : Entry(isCancelled)
    }

    class EntryHolder {
        var entry: Entry = Entry.Pending

        val isCancelled: Boolean
            get() = entry.isCancelled
    }

    @Suppress("MagicNumber")
    private val cache: LoadingCache<K, EntryHolder> by lazy {
        CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(debounceTime.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .concurrencyLevel(2)
            .build(EntryHolderLoader<K>())
    }

    fun <T : SharedCancellableEvent> debounceEvent(
        key: K,
        originalEvent: T,
        shouldBeCancelled: () -> Boolean
    ): Boolean {
        val entryHolder = cache.getUnchecked(key)
        if (entryHolder.entry is Entry.Pending) entryHolder.entry = shouldBeCancelled.invoke().let(Entry::Loaded)
        val isCancelled = entryHolder.isCancelled
        if (isCancelled) originalEvent.isCancelled = isCancelled
        return isCancelled
    }

    private class EntryHolderLoader<K : Any> : CacheLoader<K, EntryHolder>() {
        override fun load(key: K): EntryHolder = EntryHolder()
    }
}
