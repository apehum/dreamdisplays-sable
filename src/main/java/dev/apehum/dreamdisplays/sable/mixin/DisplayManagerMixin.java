package dev.apehum.dreamdisplays.sable.mixin;

import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData;
import com.dreamdisplays.platform.server.managers.DisplayManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.apehum.dreamdisplays.sable.binding.DisplayRangeKt;
import dev.apehum.dreamdisplays.sable.binding.DisplayValidationKt;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DisplayManager.class)
public class DisplayManagerMixin {
    @WrapOperation(
            method = "validateDisplaysAndCleanup",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z")
    )
    private boolean sable$keepUnloadedSubLevelDisplays(
            final List<Object> invalid,
            final Object display,
            final Operation<Boolean> original,
            @Local(argsOnly = true) final MinecraftServer server
    ) {
        if (display instanceof VanillaDisplayData data && DisplayValidationKt.isOnUnloadedSubLevel(server, data)) {
            return false;
        }

        return original.call(invalid, display);
    }

    @Inject(method = "isInRange", at = @At("HEAD"), cancellable = true)
    private void sable$plotDisplayInRange(
            final BlockPos position,
            final VanillaDisplayData data,
            final CallbackInfoReturnable<Boolean> cir
    ) {
        final Boolean inRange = DisplayRangeKt.isPlotDisplayInRange(data, position);
        if (inRange == null) return;

        cir.setReturnValue(inRange);
    }
}
