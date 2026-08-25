package dev.apehum.dreamdisplays.sable.mixin

import com.dreamdisplays.api.display.model.property.DisplayFacing
import com.dreamdisplays.api.storage.model.FullDisplayData
import com.dreamdisplays.core.protocol.common.packets.DisplayInfo
import com.dreamdisplays.platform.client.managers.DisplayLifecycleManager
import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import com.llamalad7.mixinextras.sugar.Local
import dev.apehum.dreamdisplays.sable.client.screen.boundDistance
import net.minecraft.core.BlockPos
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Suppress("NonJavaMixin", "ktlint:standard:function-naming")
@Mixin(DisplayLifecycleManager::class)
open class DisplayLifecycleManagerMixin {
    @WrapOperation(
        method = ["handleInfoPacket(Lcom/dreamdisplays/core/protocol/common/packets/DisplayInfo;)V"],
        at = [
            At(
                value = "INVOKE",
                target =
                    "Lcom/dreamdisplays/platform/client/managers/DisplayLifecycleManager;" +
                        "distanceToScreen(IIIIILcom/dreamdisplays/api/display/model/property/DisplayFacing;" +
                        "Lnet/minecraft/core/BlockPos;)D",
            ),
        ],
    )
    open fun sable_boundPacketDistance(
        manager: DisplayLifecycleManager,
        x: Int,
        y: Int,
        z: Int,
        width: Int,
        height: Int,
        facing: DisplayFacing,
        playerPosition: BlockPos,
        original: Operation<Double>,
        @Local(argsOnly = true) packet: DisplayInfo,
    ): Double =
        boundDistance(packet.id, width, height, facing, playerPosition)
            ?: original.call(manager, x, y, z, width, height, facing, playerPosition)

    @Inject(
        method = [
            "distanceToData(Lcom/dreamdisplays/api/storage/model/FullDisplayData;" +
                "Lnet/minecraft/core/BlockPos;)D",
        ],
        at = [At("HEAD")],
        cancellable = true,
    )
    open fun sable_boundCachedDistance(
        data: FullDisplayData,
        playerPosition: BlockPos,
        cir: CallbackInfoReturnable<Double>,
    ) {
        val distance = data.boundDistance(playerPosition) ?: return

        cir.returnValue = distance
    }
}
