package dev.apehum.dreamdisplays.sable.binding.event

import dev.apehum.dreamdisplays.sable.binding.BindingChange
import dev.apehum.dreamdisplays.sable.binding.ServerBindingStore
import dev.apehum.dreamdisplays.sable.binding.bindDisplay
import dev.apehum.dreamdisplays.sable.binding.bindDisplaysInSubLevel
import dev.apehum.dreamdisplays.sable.binding.reconcileBindings
import dev.apehum.dreamdisplays.sable.binding.relocateDisassembled
import dev.apehum.dreamdisplays.sable.network.broadcastPayload
import dev.apehum.dreamdisplays.sable.network.packets.BindingUpdatePayload
import net.minecraft.server.MinecraftServer

fun handleBindingEvents(
    server: MinecraftServer,
    store: ServerBindingStore,
    events: List<BindingEvent>,
) {
    val changes = events.flatMap { changesFor(server, store, it) }
    if (changes.isEmpty()) return

    store.apply(changes)
    broadcastPayload(BindingUpdatePayload.of(changes))
}

private fun changesFor(
    server: MinecraftServer,
    store: ServerBindingStore,
    event: BindingEvent,
): List<BindingChange> =
    when (event) {
        BindingEvent.Reconcile -> {
            reconcileBindings(server, store.snapshot())
        }

        is BindingEvent.DisplayCreated -> {
            listOfNotNull(bindDisplay(server, event.displayId)?.let(BindingChange::Bound))
        }

        is BindingEvent.DisplayRemoved -> {
            if (store.isBound(event.displayId)) listOf(BindingChange.Unbound(event.displayId)) else emptyList()
        }

        is BindingEvent.SubLevelAdded -> {
            bindDisplaysInSubLevel(server, store.snapshot(), event.worldKey, event.subLevelId)
                .map(BindingChange::Bound)
        }

        is BindingEvent.SubLevelRemoved -> {
            val disassembled = store.snapshot().values.filter { it.subLevelId == event.subLevelId }
            disassembled.forEach { relocateDisassembled(server, it, event.pose) }

            disassembled.map { BindingChange.Unbound(it.displayId) }
        }
    }
