package dev.apehum.dreamdisplays.sable.mixin

import com.dreamdisplays.platform.server.datatypes.selection.VanillaSelectionData
import com.dreamdisplays.platform.server.managers.SelectionManager
import dev.apehum.dreamdisplays.sable.companion.subLevelAt
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Suppress("NonJavaMixin", "ktlint:standard:function-naming")
@Mixin(SelectionManager::class)
open class SelectionManagerMixin {
    @Inject(
        method = ["setFirstPoint"],
        at = [At("TAIL")],
    )
    open fun sable_alignFirstPointToSubLevel(
        player: ServerPlayer,
        position: BlockPos,
        worldKey: String,
        face: Direction,
        ci: CallbackInfo,
    ) {
        val selection = SelectionManager.INSTANCE.selectionPoints[player.uuid] as? VanillaSelectionData ?: return
        val subLevel = player.serverLevel().subLevelAt(position) ?: return
        val look = subLevel.logicalPose().transformNormalInverse(player.lookAngle)

        selection.facing = Direction.getNearest(look.x, look.y, look.z).opposite
        selection.horizontalFacing = Direction.getNearest(look.x, 0.0, look.z)
    }
}
