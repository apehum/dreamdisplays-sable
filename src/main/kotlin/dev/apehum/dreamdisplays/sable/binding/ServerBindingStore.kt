package dev.apehum.dreamdisplays.sable.binding

import dev.apehum.dreamdisplays.sable.storage.BindingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class ServerBindingStore(
    private val repository: BindingRepository,
    private val scope: CoroutineScope,
) {
    private val bindings = ConcurrentHashMap<UUID, DisplayBinding>()
    private val writeLock = Mutex()

    operator fun get(displayId: UUID): DisplayBinding? = bindings[displayId]

    fun isBound(displayId: UUID): Boolean = bindings.containsKey(displayId)

    fun snapshot(): Map<UUID, DisplayBinding> = bindings.toMap()

    suspend fun load() {
        val loaded = repository.loadAll()
        bindings.clear()
        loaded.forEach { bindings[it.displayId] = it }
    }

    fun apply(changes: List<BindingChange>) {
        if (changes.isEmpty()) return

        changes.forEach { change ->
            when (change) {
                is BindingChange.Bound -> bindings[change.displayId] = change.binding
                is BindingChange.Unbound -> bindings.remove(change.displayId)
            }
        }

        scope.launch { save() }
    }

    suspend fun save() = writeLock.withLock { repository.save(bindings.values.toList()) }
}
