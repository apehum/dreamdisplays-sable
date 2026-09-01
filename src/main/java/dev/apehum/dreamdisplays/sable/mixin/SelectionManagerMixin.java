package dev.apehum.dreamdisplays.sable.mixin;

import com.dreamdisplays.platform.server.managers.SelectionManager;
import dev.apehum.dreamdisplays.sable.binding.DisplaySelectionKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SelectionManager.class)
public class SelectionManagerMixin {
    @Inject(method = "setFirstPoint", at = @At("TAIL"))
    private void sable$alignFirstPointToSubLevel(
        final ServerPlayer player,
        final BlockPos position,
        final String worldKey,
        final Direction face,
        final CallbackInfo ci
    ) {
        DisplaySelectionKt.alignSelectionToSubLevel(player, position);
    }
}
