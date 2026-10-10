package dev.apehum.dreamdisplays.sable.binding

import com.dreamdisplays.platform.server.VanillaConfigKt
import com.dreamdisplays.platform.server.VanillaServerState
import com.dreamdisplays.platform.server.VanillaSurfaces
import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData
import com.dreamdisplays.platform.server.utils.RegionUtil
import dev.apehum.dreamdisplays.sable.DreamDisplaysSable
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel

fun isBoundDisplayIntact(
    server: MinecraftServer,
    display: VanillaDisplayData,
): Boolean {
    val binding = DreamDisplaysSable.bindingStore?.get(display.id) ?: return false
    val level = RegionUtil.INSTANCE.getLevelByKey(server, display.worldKey) ?: return true

    return level.hasBaseMaterial(binding.local1, binding.local2)
}

internal fun ServerLevel.hasBaseMaterial(
    from: BlockPos,
    to: BlockPos,
): Boolean {
    val surface =
        VanillaSurfaces.INSTANCE.of(VanillaConfigKt.getBaseMaterialId(VanillaServerState.INSTANCE.config.settings))

    return BlockPos
        .betweenClosed(from, to)
        .any { position ->
            if (!isLoaded(position)) return@any true

            val state = getBlockState(position)
            !state.isAir && surface.accepts(BuiltInRegistries.BLOCK.getKey(state.block).toString())
        }
}
