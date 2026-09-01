package dev.apehum.dreamdisplays.sable.mixin;

import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData;
import com.dreamdisplays.platform.server.managers.DisplayManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.apehum.dreamdisplays.sable.binding.DisplayContainmentKt;
import dev.apehum.dreamdisplays.sable.binding.DisplayRangeKt;
import dev.apehum.dreamdisplays.sable.binding.DisplayValidationKt;
import dev.apehum.dreamdisplays.sable.binding.event.BindingEvent;
import dev.apehum.dreamdisplays.sable.binding.event.PendingBindingEvents;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DisplayManager.class)
public class DisplayManagerMixin {
    @Inject(
        method = "register(Lcom/dreamdisplays/platform/server/datatypes/display/VanillaDisplayData;)V",
        at = @At("TAIL")
    )
    private void sable$onDisplayRegistered(final VanillaDisplayData data, final CallbackInfo ci) {
        PendingBindingEvents.INSTANCE.push(new BindingEvent.DisplayCreated(data.getId()));
    }

    @Inject(method = "delete", at = @At("TAIL"))
    private void sable$onDisplayDeleted(final VanillaDisplayData data, final CallbackInfo ci) {
        PendingBindingEvents.INSTANCE.push(new BindingEvent.DisplayRemoved(data.getId()));
    }

    @WrapOperation(
        method = "validateDisplaysAndCleanup",
        at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z")
    )
    private boolean sable$keepBoundDisplays(
        final List<Object> invalid,
        final Object display,
        final Operation<Boolean> original,
        @Local(argsOnly = true) final MinecraftServer server
    ) {
        if (display instanceof VanillaDisplayData data && DisplayValidationKt.isBoundDisplayIntact(server, data)) {
            return false;
        }

        return original.call(invalid, display);
    }

    @Inject(method = "validateDisplaysAndCleanup", at = @At("RETURN"))
    private void sable$onDisplaysCleanedUp(
        final MinecraftServer server,
        final CallbackInfoReturnable<List<UUID>> cir
    ) {
        for (final UUID displayId : cir.getReturnValue()) {
            PendingBindingEvents.INSTANCE.push(new BindingEvent.DisplayRemoved(displayId));
        }
    }

    @Inject(method = "isInRange", at = @At("HEAD"), cancellable = true)
    private void sable$boundDisplayInRange(
        final BlockPos position,
        final VanillaDisplayData data,
        final CallbackInfoReturnable<Boolean> cir
    ) {
        final Boolean inRange = DisplayRangeKt.isBoundDisplayInRange(data, position);
        if (inRange == null) return;

        cir.setReturnValue(inRange);
    }

    @Inject(method = "isContains", at = @At("HEAD"), cancellable = true)
    private void sable$boundDisplayContains(
        final String worldKey,
        final BlockPos position,
        final CallbackInfoReturnable<VanillaDisplayData> cir
    ) {
        final VanillaDisplayData display = DisplayContainmentKt.boundDisplayAt(worldKey, position);
        if (display == null) return;

        cir.setReturnValue(display);
    }

    @WrapOperation(
        method = "isOverlaps",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;intersects(Lnet/minecraft/world/phys/AABB;)Z")
    )
    private boolean sable$boundDisplayOverlaps(
        final AABB box,
        final AABB selection,
        final Operation<Boolean> original,
        @Local final VanillaDisplayData data
    ) {
        final AABB bound = DisplayContainmentKt.boundBox(data);
        if (bound == null) return original.call(box, selection);

        return bound.intersects(selection);
    }
}
