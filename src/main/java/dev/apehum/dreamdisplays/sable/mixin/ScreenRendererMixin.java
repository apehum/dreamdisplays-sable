package dev.apehum.dreamdisplays.sable.mixin;

import com.dreamdisplays.platform.client.displays.DisplayScreen;
import com.dreamdisplays.platform.client.render.ScreenRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.apehum.dreamdisplays.sable.client.screen.ScreenKt;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenRenderer.class)
public class ScreenRendererMixin {
    @WrapOperation(
        method = "render("
            + "Lcom/mojang/blaze3d/vertex/PoseStack;"
            + "Lnet/minecraft/client/Camera;"
            + "Z"
            + "Lcom/dreamdisplays/libs/kotlin/jvm/functions/Function2;"
            + ")V",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V")
    )
    private void sable$transformBoundScreen(
        final PoseStack stack,
        final double x,
        final double y,
        final double z,
        final Operation<Void> original,
        @Local final DisplayScreen screen,
        @Local(argsOnly = true) final Camera camera
    ) {
        if (ScreenKt.applyBoundScreenTransform(stack, screen, camera)) return;

        original.call(stack, x, y, z);
    }
}
