package dev.apehum.dreamdisplays.sable.binding.event

import dev.apehum.dreamdisplays.sable.binding.BindingChange
import dev.apehum.dreamdisplays.sable.binding.DisplayBinding
import dev.apehum.dreamdisplays.sable.binding.ServerBindingStore
import dev.apehum.dreamdisplays.sable.binding.bindDisplay
import dev.apehum.dreamdisplays.sable.binding.bindDisplaysInSubLevel
import dev.apehum.dreamdisplays.sable.binding.rebindSplitDisplays
import dev.apehum.dreamdisplays.sable.binding.reconcileBindings
import dev.apehum.dreamdisplays.sable.binding.relocateDisassembled
import dev.apehum.dreamdisplays.sable.binding.rotateMoved
import dev.apehum.dreamdisplays.sable.network.broadcastPayload
import dev.apehum.dreamdisplays.sable.network.packets.BindingUpdatePayload
import net.minecraft.server.MinecraftServer
import java.util.UUID

fun handleBindingEvents(
    server: MinecraftServer,
    store: ServerBindingStore,
    events: List<BindingEvent>,
) {
    val known = store.snapshot().toMutableMap()
    val changes = events.flatMap { changesFor(server, known, it).onEach(known::update) }
    if (changes.isEmpty()) return

    store.apply(changes)
    broadcastPayload(BindingUpdatePayload.of(changes))
}

private fun MutableMap<UUID, DisplayBinding>.update(change: BindingChange) {
    when (change) {
        is BindingChange.Bound -> put(change.displayId, change.binding)
        is BindingChange.Unbound -> remove(change.displayId)
    }
}

private fun changesFor(
    server: MinecraftServer,
    known: Map<UUID, DisplayBinding>,
    event: BindingEvent,
): List<BindingChange> =
    when (event) {
        BindingEvent.Reconcile -> {
            reconcileBindings(server, known)
        }

        is BindingEvent.DisplayCreated -> {
            listOfNotNull(bindDisplay(server, event.displayId)?.let(BindingChange::Bound))
        }

        is BindingEvent.DisplayRemoved -> {
            if (event.displayId in known) listOf(BindingChange.Unbound(event.displayId)) else emptyList()
        }

        is BindingEvent.SubLevelAdded -> {
            val split = rebindSplitDisplays(server, known, event.worldKey, event.subLevelId)
            val fresh = bindDisplaysInSubLevel(server, known, event.worldKey, event.subLevelId)

            (split + fresh).map(BindingChange::Bound)
        }

        is BindingEvent.DisplaysMoved -> {
            val moved = event.bindings.filter { known[it.displayId]?.subLevelId == event.subLevelId }
            moved.forEach { rotateMoved(server, it, event.rotation) }

            moved.map(BindingChange::Bound)
        }

        is BindingEvent.SubLevelRemoved -> {
            val disassembled = known.values.filter { it.subLevelId == event.subLevelId }
            disassembled.forEach { relocateDisassembled(server, it, event.pose) }

            disassembled.map { BindingChange.Unbound(it.displayId) }
        }
    }
