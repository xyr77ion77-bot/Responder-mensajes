package com.replyflow.app.util

class DuplicateCache(private val ttlMs: Long = 5 * 60_000L, private val maxSize: Int = 128) {
    private val entries = object : LinkedHashMap<Int, Long>(maxSize, .75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, Long>?) = size > maxSize
    }

    @Synchronized fun seen(key: Int, now: Long = System.currentTimeMillis()): Boolean {
        entries.entries.removeAll { now - it.value > ttlMs }
        val previous = entries.put(key, now)
        return previous != null && now - previous <= ttlMs
    }
}
