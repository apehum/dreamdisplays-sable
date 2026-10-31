package dev.apehum.dreamdisplays.sable.mixin;

import com.dreamdisplays.platform.client.displays.DisplayScreen;
import dev.apehum.dreamdisplays.sable.client.screen.ScreenKt;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DisplayScreen.class)
public class DisplayScreenMixin {
    @Inject(method = "isInScreen", at = @At("HEAD"), cancellable = true)
    private void sable$boundScreenContains(
            final BlockPos position,
            final CallbackInfoReturnable<Boolean> cir
    ) {
        final Boolean contains = ScreenKt.boundContains((DisplayScreen) (Object) this, position);
        if (contains == null) return;

        cir.setReturnValue(contains);
    }
}
