package dev.apehum.dreamdisplays.sable.mixin;

import dev.apehum.dreamdisplays.sable.binding.BindingMoveKt;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SubLevelAssemblyHelper.class, remap = false)
public class SubLevelAssemblyHelperMixin {
    @Inject(method = "moveBlocks", at = @At("HEAD"))
    private static void sable$onBlocksMoved(
        final ServerLevel level,
        final SubLevelAssemblyHelper.AssemblyTransform transform,
        final Iterable<BlockPos> blocks,
        final CallbackInfo ci
    ) {
        BindingMoveKt.captureMovedDisplays(level, transform, blocks);
    }
}
