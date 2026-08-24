package dev.apehum.dreamdisplays.sable

import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import org.slf4j.LoggerFactory
import thedarkcolour.kotlinforforge.neoforge.forge.DIST
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(DreamDisplaysSable.MOD_ID)
object DreamDisplaysSable {
    const val MOD_ID = "dreamdisplays_sable"

    private val logger = LoggerFactory.getLogger(MOD_ID)

    init {
        MOD_BUS.addListener(::onCommonSetup)

        if (DIST == Dist.CLIENT) {
            MOD_BUS.addListener(::onClientSetup)
        }
    }

    private fun onCommonSetup(event: FMLCommonSetupEvent) {
        event.enqueueWork {
            logger.info("Dream Displays Sable loaded")
        }
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            logger.info("Dream Displays Sable client setup")
        }
    }
}
