package com.github.kittinunf.fuse.core.cache

import com.jakewharton.disklrucache.DiskLruCache
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File

class DiskCache private constructor(val cache: DiskLruCache) {

    companion object {
        fun open(cacheDir: String, uniqueName: String, capacity: Long): DiskCache {
            val f = File(cacheDir)
            val disk = DiskLruCache.open(f.resolve(uniqueName), 1, 1, capacity)
            return DiskCache(disk)
        }
    }

    operator fun set(key: Any, value: ByteArray) {
        val editor = cache.edit(key.toString())
        BufferedOutputStream(editor.newOutputStream(0)).use {
            it.write(value)
        }
        editor.commit()
    }

    operator fun get(key: Any?): ByteArray? {
        return key?.let {
            val snapshot = cache.get(key.toString())
            snapshot?.let {
                BufferedInputStream(it.getInputStream(0)).use {
                    it.readBytes()
                }
            }
        }
    }

}