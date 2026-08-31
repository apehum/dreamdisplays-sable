package dev.apehum.dreamdisplays.sable.mixin

import com.dreamdisplays.platform.client.displays.DisplayScreen
import com.dreamdisplays.platform.client.render.ScreenRenderer
import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import com.llamalad7.mixinextras.sugar.Local
import com.mojang.blaze3d.vertex.PoseStack
import dev.apehum.dreamdisplays.sable.client.screen.applyBoundScreenTransform
import net.minecraft.client.Camera
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At

@Suppress("NonJavaMixin", "ktlint:standard:function-naming")
@Mixin(ScreenRenderer::class)
open class ScreenRendererMixin {
    @WrapOperation(
        method = [
            "render(" +
                "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                "Lnet/minecraft/client/Camera;" +
                "Z" +
                "Lcom/dreamdisplays/libs/kotlin/jvm/functions/Function2;" +
                ")V",
        ],
        at = [At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V")],
    )
    open fun sable_transformBoundScreen(
        stack: PoseStack,
        x: Double,
        y: Double,
        z: Double,
        original: Operation<Void>,
        @Local screen: DisplayScreen,
        @Local(argsOnly = true) camera: Camera,
    ) {
        if (stack.applyBoundScreenTransform(screen, camera)) return

        original.call(stack, x, y, z)
    }
}
