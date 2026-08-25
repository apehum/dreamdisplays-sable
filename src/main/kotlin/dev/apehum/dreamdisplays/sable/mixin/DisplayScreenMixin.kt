package dev.apehum.dreamdisplays.sable.mixin

import com.dreamdisplays.api.media.audio.model.AcousticEnvironment
import com.dreamdisplays.api.media.audio.model.ListenerPose
import com.dreamdisplays.api.media.audio.model.SourcePlane
import com.dreamdisplays.platform.client.audio.VoxelAcousticsProbe
import com.dreamdisplays.platform.client.displays.DisplayScreen
import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import dev.apehum.dreamdisplays.sable.client.screen.boundContains
import dev.apehum.dreamdisplays.sable.client.screen.boundDistance
import dev.apehum.dreamdisplays.sable.client.screen.boundProbe
import dev.apehum.dreamdisplays.sable.client.screen.boundSourcePlane
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

    @Inject(
        method = ["toSourcePlane()Lcom/dreamdisplays/api/media/audio/model/SourcePlane;"],
        at = [At("HEAD")],
        cancellable = true,
    )
    open fun sable_boundSourcePlane(cir: CallbackInfoReturnable<SourcePlane>) {
        val screen = this as Any as DisplayScreen
        val plane = screen.boundSourcePlane() ?: return

        cir.returnValue = plane
    }

    @WrapOperation(
        method = [
            "probeEnvironment(Lcom/dreamdisplays/api/media/audio/model/SourcePlane;)" +
                "Lcom/dreamdisplays/api/media/audio/model/AcousticEnvironment;",
        ],
        at = [
            At(
                value = "INVOKE",
                target =
                    "Lcom/dreamdisplays/platform/client/audio/VoxelAcousticsProbe;" +
                        "probe(Lcom/dreamdisplays/api/media/audio/model/SourcePlane;" +
                        "Lcom/dreamdisplays/api/media/audio/model/ListenerPose;)" +
                        "Lcom/dreamdisplays/api/media/audio/model/AcousticEnvironment;",
            ),
        ],
    )
    open fun sable_probeInSubLevel(
        probe: VoxelAcousticsProbe,
        plane: SourcePlane,
        listener: ListenerPose,
        original: Operation<AcousticEnvironment>,
    ): AcousticEnvironment {
        val screen = this as Any as DisplayScreen
        val bound = screen.boundProbe(listener) ?: return original.call(probe, plane, listener)

        return original.call(probe, bound.plane, bound.listener)
    }
}
