package com.replyflow.app.data

import android.content.Context
import java.io.File
import java.io.FileOutputStream

/** Escritura JSON atómica: fsync del temporal y rename dentro del mismo directorio. */
internal class AtomicJsonStore(context: Context, private val fileName: String) {
    private val file = File(context.filesDir, fileName)

    fun readOrNull(): String? = runCatching { if (file.exists()) file.readText() else null }.getOrNull()

    @Synchronized
    fun write(value: String) {
        val tmp = File(file.parentFile, "$fileName.tmp")
        FileOutputStream(tmp).use { stream ->
            stream.write(value.toByteArray(Charsets.UTF_8))
            stream.fd.sync()
        }
        if (!tmp.renameTo(file)) {
            tmp.copyTo(file, overwrite = true)
            tmp.delete()
        }
    }

    fun rawFile(): File = file
}
