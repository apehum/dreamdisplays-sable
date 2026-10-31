package dev.apehum.dreamdisplays.sable.client.placement

import com.dreamdisplays.api.DreamDisplays
import com.dreamdisplays.api.display.model.property.DisplayId
import com.dreamdisplays.api.display.placement.DisplayPlacement
import com.dreamdisplays.api.display.placement.DisplayPlacementProvider
import com.dreamdisplays.platform.client.displays.DisplayRegistry
import dev.apehum.dreamdisplays.sable.companion.placementAt
import dev.apehum.dreamdisplays.sable.companion.subLevelAt
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos

object SubLevelPlacements : DisplayPlacementProvider {
    override fun placementOf(displayId: DisplayId): DisplayPlacement? {
        val anchor = anchorOf(displayId) ?: return null
        val subLevel = Minecraft.getInstance().level?.subLevelAt(anchor) ?: return null

        return DisplayPlacement { partialTick -> subLevel.placementAt(anchor, partialTick) }
    }

    private fun anchorOf(displayId: DisplayId): BlockPos? {
        DreamDisplays
            .display(displayId)
            ?.info
            ?.bounds
            ?.let { return BlockPos.containing(it.x, it.y, it.z) }

        return DisplayRegistry.INSTANCE.unloadedScreens[displayId.uuid]?.let { BlockPos(it.x, it.y, it.z) }
    }
}
