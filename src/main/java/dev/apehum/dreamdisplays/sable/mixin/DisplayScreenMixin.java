package dev.apehum.dreamdisplays.sable.mixin;

import com.dreamdisplays.api.media.audio.model.AcousticEnvironment;
import com.dreamdisplays.api.media.audio.model.ListenerPose;
import com.dreamdisplays.api.media.audio.model.SourcePlane;
import com.dreamdisplays.platform.client.audio.VoxelAcousticsProbe;
import com.dreamdisplays.platform.client.displays.DisplayScreen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.apehum.dreamdisplays.sable.client.screen.BoundProbe;
import dev.apehum.dreamdisplays.sable.client.screen.ScreenAcousticsKt;
import dev.apehum.dreamdisplays.sable.client.screen.ScreenKt;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DisplayScreen.class)
public class DisplayScreenMixin {
    @Inject(method = "getDistanceToScreen", at = @At("HEAD"), cancellable = true)
    private void sable$boundDistanceToScreen(
        final BlockPos position,
        final CallbackInfoReturnable<Double> cir
    ) {
        final Double distance = ScreenKt.boundDistance((DisplayScreen) (Object) this, position);
        if (distance == null) return;

        cir.setReturnValue(distance);
    }

    @Inject(method = "isInScreen", at = @At("HEAD"), cancellable = true)
    private void sable$boundScreenContains(
        final BlockPos position,
        final CallbackInfoReturnable<Boolean> cir
    ) {
        final Boolean contains = ScreenKt.boundContains((DisplayScreen) (Object) this, position);
        if (contains == null) return;

        cir.setReturnValue(contains);
    }

    @Inject(method = "toSourcePlane", at = @At("HEAD"), cancellable = true)
    private void sable$boundSourcePlane(final CallbackInfoReturnable<SourcePlane> cir) {
        final SourcePlane plane = ScreenAcousticsKt.boundSourcePlane((DisplayScreen) (Object) this);
        if (plane == null) return;

        cir.setReturnValue(plane);
    }

    @WrapOperation(
        method = "probeEnvironment",
        at = @At(
            value = "INVOKE",
            target = "Lcom/dreamdisplays/platform/client/audio/VoxelAcousticsProbe;probe(Lcom/dreamdisplays/api/media/audio/model/SourcePlane;Lcom/dreamdisplays/api/media/audio/model/ListenerPose;)Lcom/dreamdisplays/api/media/audio/model/AcousticEnvironment;"
        )
    )
    private AcousticEnvironment sable$probeInSubLevel(
        final VoxelAcousticsProbe probe,
        final SourcePlane plane,
        final ListenerPose listener,
        final Operation<AcousticEnvironment> original
    ) {
        final BoundProbe bound = ScreenAcousticsKt.boundProbe((DisplayScreen) (Object) this, listener);
        if (bound == null) return original.call(probe, plane, listener);

        return original.call(probe, bound.getPlane(), bound.getListener());
    }
}
