package dev.apehum.dreamdisplays.sable.client.screen

import com.dreamdisplays.api.display.geometry.DisplayGeometry
import com.dreamdisplays.platform.client.displays.DisplayScreen
import dev.apehum.dreamdisplays.sable.companion.isInPlotGrid
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos

fun DisplayScreen.boundContains(position: BlockPos): Boolean? {
    if (Minecraft.getInstance().level?.isInPlotGrid(pos) != true) return null

    return DisplayGeometry.isInBounds(
        position.x,
        position.y,
        position.z,
        pos.x,
        pos.y,
        pos.z,
        width,
        height,
        facing,
        depth.coerceAtLeast(1),
    )
}
