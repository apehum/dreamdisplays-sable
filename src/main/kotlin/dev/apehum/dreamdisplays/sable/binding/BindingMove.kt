package dev.apehum.dreamdisplays.sable.binding

import dev.apehum.dreamdisplays.sable.DreamDisplaysSable
import dev.apehum.dreamdisplays.sable.binding.event.BindingEvent
import dev.apehum.dreamdisplays.sable.binding.event.PendingBindingEvents
import dev.apehum.dreamdisplays.sable.companion.subLevelAt
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper.AssemblyTransform
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import java.util.UUID

fun captureMovedDisplays(
    level: ServerLevel,
    transform: AssemblyTransform,
    blocks: Iterable<BlockPos>,
) {
    val bindings = DreamDisplaysSable.bindingStore?.snapshot()?.takeIf { it.isNotEmpty() } ?: return
    val anchor = blocks.firstOrNull() ?: return
    val source = level.subLevelAt(anchor)?.uniqueId ?: return
    val bound = bindings.values.filter { it.subLevelId == source }.takeIf { it.isNotEmpty() } ?: return

    val target = transform.level.subLevelAt(transform.apply(anchor))?.uniqueId ?: return
    if (target == source) return

    val positions = blocks.mapTo(LongOpenHashSet(), BlockPos::asLong)

    val moved =
        bound
            .filter { it.movedWith(positions) }
            .map { it.movedBy(transform, target) }
            .takeIf { it.isNotEmpty() } ?: return

    PendingBindingEvents.push(BindingEvent.DisplaysMoved(source, moved, transform.rotation))
}

private fun DisplayBinding.movedWith(positions: LongSet): Boolean =
    BlockPos
        .betweenClosed(local1, local2)
        .any { positions.contains(it.asLong()) }

private fun DisplayBinding.movedBy(
    transform: AssemblyTransform,
    subLevelId: UUID,
): DisplayBinding =
    DisplayBinding(
        displayId = displayId,
        subLevelId = subLevelId,
        local1 = transform.apply(local1),
        local2 = transform.apply(local2),
    )
