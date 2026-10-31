package dev.apehum.dreamdisplays.sable

import com.dreamdisplays.api.DreamDisplays
import com.dreamdisplays.api.addon.Addon
import com.dreamdisplays.api.addon.AddonContext
import com.dreamdisplays.api.addon.AddonLifecycle
import com.dreamdisplays.api.addon.AddonSide
import com.dreamdisplays.api.server.DreamDisplaysServer
import dev.apehum.dreamdisplays.sable.binding.deleteDisplaysOnRemovedSubLevel
import dev.apehum.dreamdisplays.sable.client.SableClientAddon
import dev.apehum.dreamdisplays.sable.companion.observeRemovedSubLevels
import dev.apehum.dreamdisplays.sable.storage.migrateLegacyBindings
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.server.ServerLifecycleHooks
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runWhenOn

const val MOD_ID = "dreamdisplays_sable"

@Mod(MOD_ID)
object DreamDisplaysSable {
    init {
        MOD_BUS.addListener(::onCommonSetup)

        DreamDisplaysServer.register(ServerAddon())

        runWhenOn(Dist.CLIENT) {
            DreamDisplays.register(SableClientAddon())
        }
    }

    private fun onCommonSetup(event: FMLCommonSetupEvent) {
        event.enqueueWork { observeRemovedSubLevels(::deleteDisplaysOnRemovedSubLevel) }
    }

    @Addon(id = MOD_ID, side = AddonSide.SERVER)
    private class ServerAddon : AddonLifecycle {
        override fun onEnable(context: AddonContext) {
            val server = ServerLifecycleHooks.getCurrentServer() ?: return

            migrateLegacyBindings(server)
        }
    }
}
