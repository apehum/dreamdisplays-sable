package dev.apehum.dreamdisplays.sable.client

import com.dreamdisplays.api.DreamDisplays
import com.dreamdisplays.api.addon.Addon
import com.dreamdisplays.api.addon.AddonContext
import com.dreamdisplays.api.addon.AddonLifecycle
import com.dreamdisplays.api.addon.AddonSide
import dev.apehum.dreamdisplays.sable.MOD_ID
import dev.apehum.dreamdisplays.sable.client.placement.SubLevelPlacements

@Addon(id = MOD_ID, side = AddonSide.CLIENT)
class SableClientAddon : AddonLifecycle {
    override fun onEnable(context: AddonContext) {
        context.own(DreamDisplays.getPlacements().register(SubLevelPlacements))
    }
}
