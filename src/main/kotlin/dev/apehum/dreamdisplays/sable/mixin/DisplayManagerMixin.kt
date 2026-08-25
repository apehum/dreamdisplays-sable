package dev.apehum.dreamdisplays.sable.mixin

import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData
import com.dreamdisplays.platform.server.managers.DisplayManager
import dev.apehum.dreamdisplays.sable.binding.event.BindingEvent
import dev.apehum.dreamdisplays.sable.binding.event.PendingBindingEvents
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

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
        method = ["delete(Lcom/dreamdisplays/platform/server/datatypes/display/VanillaDisplayData;)V"],
        at = [At("TAIL")],
    )
    open fun sable_onDisplayDeleted(
        data: VanillaDisplayData,
        ci: CallbackInfo,
    ) {
        PendingBindingEvents.push(BindingEvent.DisplayRemoved(data.id))
    }
}
