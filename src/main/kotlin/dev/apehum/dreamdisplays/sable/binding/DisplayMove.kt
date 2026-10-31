package dev.apehum.dreamdisplays.sable.binding

import com.dreamdisplays.api.CommandResult
import com.dreamdisplays.api.display.model.property.DisplayFacing
import com.dreamdisplays.api.server.DreamDisplaysServer
import com.dreamdisplays.api.server.ServerDisplay
import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData
import com.dreamdisplays.platform.server.utils.RegionUtil
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper.AssemblyTransform
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("DreamDisplaysSable/DisplayMove")

fun moveDisplaysWithBlocks(
    level: ServerLevel,
    transform: AssemblyTransform,
    blocks: Iterable<BlockPos>,
) {
    val worldKey = RegionUtil.INSTANCE.getLevelKey(level)
    val displays = vanillaDisplays().filter { it.worldKey == worldKey }.takeIf { it.isNotEmpty() } ?: return

    val positions = blocks.mapTo(LongOpenHashSet(), BlockPos::asLong)
    val moved = displays.filter { it.movedWith(positions) }.takeIf { it.isNotEmpty() } ?: return
    val targetWorldKey = RegionUtil.INSTANCE.getLevelKey(transform.level)

    moved.forEach { display ->
        DreamDisplaysServer.display(display.id)?.moveTo(
            targetWorldKey,
            transform.apply(display.pos1),
            transform.apply(display.pos2),
            transform.rotation.rotate(display.facing),
        )
    }
}

fun ServerDisplay.moveTo(
    worldKey: String,
    pos1: BlockPos,
    pos2: BlockPos,
    facing: Direction,
) {
    val result = moveTo(worldKey, pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z, DisplayFacing.valueOf(facing.name))
    if (result == CommandResult.REFUSED) {
        logger.warn("Dream Displays refused to move display {} to {} .. {}", id, pos1, pos2)
    }
}

private fun VanillaDisplayData.movedWith(positions: LongSet): Boolean =
    BlockPos
        .betweenClosed(pos1, pos2)
        .any { positions.contains(it.asLong()) }
