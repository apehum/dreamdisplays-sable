package dev.apehum.dreamdisplays.sable.mixin

import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData
import com.dreamdisplays.platform.server.managers.DisplayManager
import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import com.llamalad7.mixinextras.sugar.Local
import dev.apehum.dreamdisplays.sable.binding.boundBox
import dev.apehum.dreamdisplays.sable.binding.boundDisplayAt
import dev.apehum.dreamdisplays.sable.binding.event.BindingEvent
import dev.apehum.dreamdisplays.sable.binding.event.PendingBindingEvents
import dev.apehum.dreamdisplays.sable.binding.isBoundDisplayInRange
import dev.apehum.dreamdisplays.sable.binding.isBoundDisplayIntact
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.world.phys.AABB
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.util.UUID

@Suppress("NonJavaMixin", "ktlint:standard:function-naming")
@Mixin(DisplayManager::class)
open class DisplayManagerMixin {
    @Inject(
        method = ["register(Lcom/dreamdisplays/platform/server/datatypes/display/VanillaDisplayData;)V"],
        at = [At("TAIL")],
    )
    open fun sable_onDisplayRegistered(
        data: VanillaDisplayData,
        ci: CallbackInfo,
    ) {
        PendingBindingEvents.push(BindingEvent.DisplayCreated(data.id))
    }

    @Inject(
        method = ["delete"],
        at = [At("TAIL")],
    )
    open fun sable_onDisplayDeleted(
        data: VanillaDisplayData,
        ci: CallbackInfo,
    ) {
        PendingBindingEvents.push(BindingEvent.DisplayRemoved(data.id))
    }

    @WrapOperation(
        method = ["validateDisplaysAndCleanup"],
        at = [At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z")],
    )
    open fun sable_keepBoundDisplays(
        invalid: MutableList<Any>,
        display: Any,
        original: Operation<Boolean>,
        @Local(argsOnly = true) server: MinecraftServer,
    ): Boolean {
        val data = display as? VanillaDisplayData
        if (data != null && isBoundDisplayIntact(server, data)) return false

        return original.call(invalid, display)
    }

    @Inject(
        method = ["validateDisplaysAndCleanup"],
        at = [At("RETURN")],
    )
    open fun sable_onDisplaysCleanedUp(
        server: MinecraftServer,
        cir: CallbackInfoReturnable<List<UUID>>,
    ) {
        cir.returnValue.forEach { PendingBindingEvents.push(BindingEvent.DisplayRemoved(it)) }
    }

    @Inject(
        method = ["isInRange"],
        at = [At("HEAD")],
        cancellable = true,
    )
    open fun sable_boundDisplayInRange(
        position: BlockPos,
        data: VanillaDisplayData,
        cir: CallbackInfoReturnable<Boolean>,
    ) {
        val inRange = data.isBoundDisplayInRange(position) ?: return

        cir.returnValue = inRange
    }

    @Inject(
        method = ["isContains"],
        at = [At("HEAD")],
        cancellable = true,
    )
    open fun sable_boundDisplayContains(
        worldKey: String,
        position: BlockPos,
        cir: CallbackInfoReturnable<VanillaDisplayData>,
    ) {
        val display = boundDisplayAt(worldKey, position) ?: return

        cir.returnValue = display
    }

    @WrapOperation(
        method = ["isOverlaps"],
        at = [At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;intersects(Lnet/minecraft/world/phys/AABB;)Z")],
    )
    open fun sable_boundDisplayOverlaps(
        box: AABB,
        selection: AABB,
        original: Operation<Boolean>,
        @Local data: VanillaDisplayData,
    ): Boolean {
        val bound = data.boundBox() ?: return original.call(box, selection)

        return bound.intersects(selection)
    }
}
