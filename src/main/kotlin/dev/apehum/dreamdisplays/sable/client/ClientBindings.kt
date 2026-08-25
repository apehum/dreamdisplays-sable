package dev.apehum.dreamdisplays.sable.client

import dev.apehum.dreamdisplays.sable.binding.DisplayBinding
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object ClientBindings {
    private val logger = LoggerFactory.getLogger("DreamDisplaysSable/ClientBindings")

    private val bindings = ConcurrentHashMap<UUID, DisplayBinding>()

    operator fun get(displayId: UUID): DisplayBinding? = bindings[displayId]

    fun isBound(displayId: UUID): Boolean = bindings.containsKey(displayId)

    fun replaceAll(snapshot: Collection<DisplayBinding>) {
        bindings.clear()
        snapshot.forEach { bindings[it.displayId] = it }

        logger.info("Bindings: {}", bindings)
    }

    fun update(
        bound: Collection<DisplayBinding>,
        unbound: Collection<UUID>,
    ) {
        bound.forEach { bindings[it.displayId] = it }
        unbound.forEach(bindings::remove)

        logger.info("Bindings: {}", bindings)
    }

    fun clear() = bindings.clear()
}
