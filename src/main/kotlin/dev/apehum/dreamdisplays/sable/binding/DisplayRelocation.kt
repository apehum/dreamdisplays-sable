package dev.apehum.dreamdisplays.sable.binding

import com.dreamdisplays.platform.server.VanillaServerState
import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData
import com.dreamdisplays.platform.server.managers.DisplayManager
import com.dreamdisplays.platform.server.utils.RegionUtil
import dev.apehum.dreamdisplays.sable.companion.SubLevelPose
import dev.apehum.dreamdisplays.sable.mixin.VanillaDisplayDataAccessor
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.MinecraftServer
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("DreamDisplaysSable/DisplayRelocation")

fun relocateDisassembled(
    server: MinecraftServer,
    binding: DisplayBinding,
    pose: SubLevelPose,
) {
    val display = DisplayManager.getDisplayData(binding.displayId) as? VanillaDisplayData ?: return
    val level = RegionUtil.INSTANCE.getLevelByKey(server, display.worldKey) ?: return

    val pos1 = pose.transformBlock(binding.local1)
    val pos2 = pose.transformBlock(binding.local2)
    val facing = pose.rotate(display.facing)

    if (!level.hasBaseMaterial(pos1, pos2)) {
        logger.warn("Leaving display {} at its stored position: no base material at {} .. {}", display.id, pos1, pos2)
        return
    }

    display.moveTo(pos1, pos2, facing)
    runCatching { VanillaServerState.INSTANCE.storage?.saveDisplay(display) }
        .onFailure { logger.error("Failed to persist relocated display ${display.id}", it) }

    DisplayManager.INSTANCE.sendUpdate(display, DisplayManager.INSTANCE.getReceivers(display, server))
}

private fun SubLevelPose.rotate(facing: Direction): Direction {
    val rotated = transformDirection(Vec3(facing.stepX.toDouble(), facing.stepY.toDouble(), facing.stepZ.toDouble()))

    return Direction.getNearest(rotated.x, rotated.y, rotated.z)
}

private fun VanillaDisplayData.moveTo(
    pos1: BlockPos,
    pos2: BlockPos,
    facing: Direction,
) {
    val accessor = this as VanillaDisplayDataAccessor
    val region = RegionUtil.INSTANCE.calculateRegion(pos1, pos2)

    accessor.sable_setPos1(pos1)
    accessor.sable_setPos2(pos2)
    accessor.sable_setFacing(facing)
    accessor.sable_setRegion(region)
    accessor.sable_setMinX(region.minX)
    accessor.sable_setMinY(region.minY)
    accessor.sable_setMinZ(region.minZ)
    accessor.sable_setMaxX(region.maxX)
    accessor.sable_setMaxY(region.maxY)
    accessor.sable_setMaxZ(region.maxZ)
    accessor.sable_setBox(
        AABB(
            region.minX.toDouble(),
            region.minY.toDouble(),
            region.minZ.toDouble(),
            (region.maxX + 1).toDouble(),
            (region.maxY + 1).toDouble(),
            (region.maxZ + 1).toDouble(),
        ),
    )
}
