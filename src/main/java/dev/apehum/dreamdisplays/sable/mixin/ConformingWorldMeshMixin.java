package dev.apehum.dreamdisplays.sable.mixin;

import com.dreamdisplays.platform.client.displays.DisplayScreen;
import com.dreamdisplays.platform.client.render.ConformingWorldMesh;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.apehum.dreamdisplays.sable.client.screen.ScreenKt;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ConformingWorldMesh.class)
public class ConformingWorldMeshMixin {
    @WrapOperation(
            method = "quads",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/dreamdisplays/platform/client/displays/DisplayScreen;getPos()Lnet/minecraft/core/BlockPos;"
            )
    )
    private BlockPos sable$readBoundBlocks(final DisplayScreen screen, final Operation<BlockPos> original) {
        final BlockPos origin = ScreenKt.boundBlockOrigin(screen);
        if (origin == null) return original.call(screen);

        return origin;
    }
}
