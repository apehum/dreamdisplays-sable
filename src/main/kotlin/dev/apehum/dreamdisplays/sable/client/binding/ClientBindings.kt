package dev.apehum.dreamdisplays.sable.client.binding

import dev.apehum.dreamdisplays.sable.binding.DisplayBinding
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object ClientBindings {
    private val bindings = ConcurrentHashMap<UUID, DisplayBinding>()

    operator fun get(displayId: UUID): DisplayBinding? = bindings[displayId]

    fun replaceAll(snapshot: Collection<DisplayBinding>) {
        bindings.clear()
        snapshot.forEach { bindings[it.displayId] = it }
    }

    fun update(
        bound: Collection<DisplayBinding>,
        unbound: Collection<UUID>,
    ) {
        bound.forEach { bindings[it.displayId] = it }
        unbound.forEach(bindings::remove)
    }

    fun clear() = bindings.clear()
}
