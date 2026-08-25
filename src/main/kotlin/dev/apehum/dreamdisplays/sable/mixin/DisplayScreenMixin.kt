package dev.apehum.dreamdisplays.sable.mixin

import com.dreamdisplays.platform.client.displays.DisplayScreen
import dev.apehum.dreamdisplays.sable.client.screen.boundContains
import dev.apehum.dreamdisplays.sable.client.screen.boundDistance
import net.minecraft.core.BlockPos
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Suppress("NonJavaMixin", "ktlint:standard:function-naming")
@Mixin(DisplayScreen::class)
open class DisplayScreenMixin {
    @Inject(
        method = ["getDistanceToScreen(Lnet/minecraft/core/BlockPos;)D"],
        at = [At("HEAD")],
        cancellable = true,
    )
    open fun sable_boundDistanceToScreen(
        position: BlockPos,
        cir: CallbackInfoReturnable<Double>,
    ) {
        val screen = this as Any as DisplayScreen
        val distance = screen.boundDistance(position) ?: return

        cir.returnValue = distance
    }

    @Inject(
        method = ["isInScreen(Lnet/minecraft/core/BlockPos;)Z"],
        at = [At("HEAD")],
        cancellable = true,
    )
    open fun sable_boundScreenContains(
        position: BlockPos,
        cir: CallbackInfoReturnable<Boolean>,
    ) {
        val screen = this as Any as DisplayScreen
        val contains = screen.boundContains(position) ?: return

        cir.returnValue = contains
    }
}
