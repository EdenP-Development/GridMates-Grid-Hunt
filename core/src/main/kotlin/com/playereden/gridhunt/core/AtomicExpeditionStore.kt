package com.playereden.gridhunt.core

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Isolated candidate store for a known project, under an app-private directory.
 * One writer instance per project; no cross-process locking or remote imports.
 * Atomic move must be supported: never fall back to destructive replacement.
 * File contents are synced before rename; directory fsync/power-loss durability
 * and Android device behavior require separate platform verification.
 */
class AtomicExpeditionStore(
    directory: File,
    private val contract: ExpeditionProgressContract
) {
    private val root = directory.canonicalFile
    private val active = File(root, "${contract.identity.projectId}.ghex")

    sealed interface ReadResult {
        object Missing : ReadResult
        object Invalid : ReadResult
        data class Ready(val snapshot: ExpeditionProgressContract.Snapshot) : ReadResult
    }

    @Synchronized
    fun read(): ReadResult {
        if (!active.exists()) return ReadResult.Missing
        if (!active.isFile) return ReadResult.Invalid
        return try {
            val bytes = active.inputStream().use { input ->
                val output = java.io.ByteArrayOutputStream()
                val buffer = ByteArray(8192)
                var count = 0
                while (true) {
                    val n = input.read(buffer)
                    if (n < 0) break
                    count += n
                    if (count > ExpeditionSaveCodec.maxBytes) return ReadResult.Invalid
                    output.write(buffer,0,n)
                }
                output.toByteArray()
            }
            val snapshot = ExpeditionSaveCodec.decode(contract,bytes)
                ?: return ReadResult.Invalid
            ReadResult.Ready(snapshot)
        } catch (_: IOException) {
            ReadResult.Invalid
        }
    }

    /** Monotonic save only. Invalid existing content is preserved for recovery. */
    @Synchronized
    fun save(snapshot: ExpeditionProgressContract.Snapshot): Boolean {
        if (contract.restore(snapshot.identity,snapshot.solvedObjectiveIds) == null) return false
        when (val previous = read()) {
            ReadResult.Invalid -> return false
            ReadResult.Missing -> Unit
            is ReadResult.Ready ->
                if (!snapshot.solvedObjectiveIds.containsAll(previous.snapshot.solvedObjectiveIds))
                    return false
        }
        val bytes = try {
            ExpeditionSaveCodec.encode(contract,snapshot)
        } catch (_: IllegalArgumentException) {
            return false
        }
        var pending: File? = null
        return try {
            if (!root.isDirectory && !root.mkdirs()) return false
            val temporary = File.createTempFile("${contract.identity.projectId}-",".pending",root)
            pending = temporary
            FileOutputStream(temporary).use { stream ->
                stream.write(bytes)
                stream.flush()
                stream.fd.sync()
            }
            if (!temporary.readBytes().contentEquals(bytes)) return false
            Files.move(temporary.toPath(),active.toPath(),
                StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING)
            true
        } catch (_: IOException) {
            false
        } finally {
            pending?.delete()
        }
    }
}
