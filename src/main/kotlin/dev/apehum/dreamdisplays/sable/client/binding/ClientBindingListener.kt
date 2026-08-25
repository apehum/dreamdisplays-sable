package dev.apehum.dreamdisplays.sable.client.binding

import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS

fun registerClientBindingListeners() {
    FORGE_BUS.addListener<ClientPlayerNetworkEvent.LoggingIn> { ClientBindings.clear() }
    FORGE_BUS.addListener<ClientPlayerNetworkEvent.LoggingOut> { ClientBindings.clear() }
}
