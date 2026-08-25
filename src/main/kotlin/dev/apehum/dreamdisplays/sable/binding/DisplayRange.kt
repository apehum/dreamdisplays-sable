package dev.apehum.dreamdisplays.sable.binding

import com.dreamdisplays.platform.server.VanillaServerState
import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData
import com.dreamdisplays.platform.server.utils.RegionUtil
import dev.apehum.dreamdisplays.sable.DreamDisplaysSable
import dev.apehum.dreamdisplays.sable.companion.subLevelLocalOf
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

fun VanillaDisplayData.isBoundDisplayInRange(position: BlockPos): Boolean? {
    val binding = DreamDisplaysSable.bindingStore?.get(id) ?: return null
    val server = VanillaServerState.INSTANCE.server ?: return null
    val level = RegionUtil.INSTANCE.getLevelByKey(server, worldKey) ?: return null
    val local = level.subLevelLocalOf(binding.subLevelId, position) ?: return null

    val maxRenderDistance = VanillaServerState.INSTANCE.config.settings.maxRenderDistance

    return binding.box.distanceToSqr(Vec3.atCenterOf(local)) <= maxRenderDistance * maxRenderDistance
}
