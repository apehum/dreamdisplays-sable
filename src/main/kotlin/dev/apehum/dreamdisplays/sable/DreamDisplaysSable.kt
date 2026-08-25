package dev.apehum.dreamdisplays.sable

import dev.apehum.dreamdisplays.sable.binding.ServerBindingStore
import dev.apehum.dreamdisplays.sable.binding.event.BindingEvent
import dev.apehum.dreamdisplays.sable.binding.event.PendingBindingEvents
import dev.apehum.dreamdisplays.sable.binding.event.handleBindingEvents
import dev.apehum.dreamdisplays.sable.binding.event.observeSubLevelEvents
import dev.apehum.dreamdisplays.sable.client.registerClientBindingListeners
import dev.apehum.dreamdisplays.sable.network.packets.BindingSnapshotPayload
import dev.apehum.dreamdisplays.sable.network.registerBindingChannel
import dev.apehum.dreamdisplays.sable.network.sendPayload
import dev.apehum.dreamdisplays.sable.storage.JsonBindingRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.storage.LevelResource
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import org.slf4j.LoggerFactory
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runWhenOn

const val MOD_ID = "dreamdisplays_sable"

@Mod(MOD_ID)
object DreamDisplaysSable {
    private const val BINDINGS_FILE = "bindings.json"

    private val logger = LoggerFactory.getLogger("DreamDisplaysSable")

    private val scope =
        CoroutineScope(
            SupervisorJob() +
                Dispatchers.IO +
                CoroutineExceptionHandler { _, error -> logger.error("Unhandled coroutine exception", error) },
        )

    var bindingStore: ServerBindingStore? = null
        private set

    init {
        MOD_BUS.addListener(::registerBindingChannel)
        MOD_BUS.addListener(::onCommonSetup)

        FORGE_BUS.addListener(::onServerStarted)
        FORGE_BUS.addListener(::onServerStopping)
        FORGE_BUS.addListener(::onServerTick)
        FORGE_BUS.addListener(::onPlayerLoggedIn)

        runWhenOn(Dist.CLIENT) { registerClientBindingListeners() }
    }

    private fun onCommonSetup(event: FMLCommonSetupEvent) {
        event.enqueueWork { observeSubLevelEvents() }
    }

    private fun onServerStarted(event: ServerStartedEvent) {
        val file =
            event.server
                .getWorldPath(LevelResource.LEVEL_DATA_FILE)
                .parent
                .resolve(MOD_ID)
                .resolve(BINDINGS_FILE)

        val bindings = ServerBindingStore(JsonBindingRepository(file), scope)
        runCatching { runBlocking { bindings.load() } }
            .onFailure { logger.error("Failed to load display bindings from $file", it) }

        bindingStore = bindings
        PendingBindingEvents.push(BindingEvent.Reconcile)
    }

    private fun onServerStopping(event: ServerStoppingEvent) {
        val bindings = bindingStore ?: return
        runCatching { runBlocking { bindings.save() } }
            .onFailure { logger.error("Failed to persist display bindings on shutdown", it) }

        bindingStore = null
        PendingBindingEvents.clear()
    }

    private fun onServerTick(event: ServerTickEvent.Post) {
        val bindings = bindingStore ?: return
        val events = PendingBindingEvents.drain().takeIf { it.isNotEmpty() } ?: return

        handleBindingEvents(event.server, bindings, events)
    }

    private fun onPlayerLoggedIn(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.entity as? ServerPlayer ?: return
        val bindings = bindingStore ?: return

        player.sendPayload(BindingSnapshotPayload.of(bindings.snapshot().values))
    }
}
