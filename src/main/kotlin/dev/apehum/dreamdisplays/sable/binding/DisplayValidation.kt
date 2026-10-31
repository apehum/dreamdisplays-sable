package dev.apehum.dreamdisplays.sable.binding

import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData
import com.dreamdisplays.platform.server.utils.RegionUtil
import dev.apehum.dreamdisplays.sable.companion.isInPlotGrid
import dev.apehum.dreamdisplays.sable.companion.subLevelAt
import net.minecraft.server.MinecraftServer

fun isOnUnloadedSubLevel(
    server: MinecraftServer,
    display: VanillaDisplayData,
): Boolean {
    val level = RegionUtil.INSTANCE.getLevelByKey(server, display.worldKey) ?: return false

    return level.isInPlotGrid(display.pos1) && level.subLevelAt(display.pos1) == null
}
