package dev.apehum.dreamdisplays.sable.mixin;

import com.dreamdisplays.api.display.model.property.DisplayFacing;
import com.dreamdisplays.api.storage.model.FullDisplayData;
import com.dreamdisplays.core.protocol.common.packets.DisplayInfo;
import com.dreamdisplays.platform.client.managers.DisplayLifecycleManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.apehum.dreamdisplays.sable.client.screen.ScreenKt;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DisplayLifecycleManager.class)
public class DisplayLifecycleManagerMixin {
    @WrapOperation(
        method = "handleInfoPacket",
        at = @At(
            value = "INVOKE",
            target = "Lcom/dreamdisplays/platform/client/managers/DisplayLifecycleManager;distanceToScreen(IIIIILcom/dreamdisplays/api/display/model/property/DisplayFacing;Lnet/minecraft/core/BlockPos;)D"
        )
    )
    private double sable$boundPacketDistance(
        final DisplayLifecycleManager manager,
        final int x,
        final int y,
        final int z,
        final int width,
        final int height,
        final DisplayFacing facing,
        final BlockPos playerPosition,
        final Operation<Double> original,
        @Local(argsOnly = true) final DisplayInfo packet
    ) {
        final Double distance = ScreenKt.boundDistance(packet.getId(), width, height, facing, playerPosition);
        if (distance == null) {
            return original.call(manager, x, y, z, width, height, facing, playerPosition);
        }

        return distance;
    }

    @Inject(method = "distanceToData", at = @At("HEAD"), cancellable = true)
    private void sable$boundCachedDistance(
        final FullDisplayData data,
        final BlockPos playerPosition,
        final CallbackInfoReturnable<Double> cir
    ) {
        final Double distance = ScreenKt.boundDistance(data, playerPosition);
        if (distance == null) return;

        cir.setReturnValue(distance);
    }
}
