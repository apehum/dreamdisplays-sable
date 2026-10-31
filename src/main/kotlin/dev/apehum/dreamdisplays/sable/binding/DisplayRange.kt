package dev.apehum.dreamdisplays.sable.binding

import com.dreamdisplays.platform.server.VanillaServerState
import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData
import com.dreamdisplays.platform.server.utils.RegionUtil
import dev.apehum.dreamdisplays.sable.companion.isInPlotGrid
import dev.apehum.dreamdisplays.sable.companion.subLevelAt
import dev.apehum.dreamdisplays.sable.companion.toLocalBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

fun VanillaDisplayData.isPlotDisplayInRange(position: BlockPos): Boolean? {
    val server = VanillaServerState.INSTANCE.server ?: return null
    val level = RegionUtil.INSTANCE.getLevelByKey(server, worldKey) ?: return null
    if (!level.isInPlotGrid(pos1)) return null

    val subLevel = level.subLevelAt(pos1) ?: return false
    val local = subLevel.logicalPose().toLocalBlock(position.center)

    val maxRenderDistance = minOf(server.playerList.viewDistance, server.playerList.simulationDistance) * 16.0

    return box.distanceToSqr(Vec3.atCenterOf(local)) <= maxRenderDistance * maxRenderDistance
}
