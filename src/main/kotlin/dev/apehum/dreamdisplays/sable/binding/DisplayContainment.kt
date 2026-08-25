package dev.apehum.dreamdisplays.sable.binding

import com.dreamdisplays.platform.server.VanillaServerState
import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData
import com.dreamdisplays.platform.server.managers.DisplayManager
import com.dreamdisplays.platform.server.utils.RegionUtil
import dev.apehum.dreamdisplays.sable.DreamDisplaysSable
import dev.apehum.dreamdisplays.sable.companion.subLevelAt
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

fun boundDisplayAt(
    worldKey: String,
    position: BlockPos,
): VanillaDisplayData? {
    val bindings = DreamDisplaysSable.bindingStore?.snapshot()?.takeIf { it.isNotEmpty() } ?: return null
    val server = VanillaServerState.INSTANCE.server ?: return null
    val level = RegionUtil.INSTANCE.getLevelByKey(server, worldKey) ?: return null
    val subLevel = level.subLevelAt(position) ?: return null

    val center = Vec3.atCenterOf(position)

    return bindings.values
        .asSequence()
        .filter { it.subLevelId == subLevel.uniqueId && it.box.contains(center) }
        .mapNotNull { DisplayManager.getDisplayData(it.displayId) as? VanillaDisplayData }
        .firstOrNull { it.worldKey == worldKey }
}
