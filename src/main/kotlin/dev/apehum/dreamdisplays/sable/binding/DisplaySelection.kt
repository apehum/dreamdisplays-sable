package dev.apehum.dreamdisplays.sable.binding

import com.dreamdisplays.platform.server.datatypes.selection.VanillaSelectionData
import com.dreamdisplays.platform.server.managers.SelectionManager
import dev.apehum.dreamdisplays.sable.companion.subLevelAt
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer

fun alignSelectionToSubLevel(
    player: ServerPlayer,
    position: BlockPos,
) {
    val selection = SelectionManager.INSTANCE.selectionPoints[player.uuid] as? VanillaSelectionData ?: return
    val subLevel = player.serverLevel().subLevelAt(position) ?: return
    val look = subLevel.logicalPose().transformNormalInverse(player.lookAngle)

    selection.facing = Direction.getNearest(look.x, look.y, look.z).opposite
    selection.horizontalFacing = Direction.getNearest(look.x, 0.0, look.z)
}
