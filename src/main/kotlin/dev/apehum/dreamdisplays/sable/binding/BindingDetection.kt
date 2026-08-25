package dev.apehum.dreamdisplays.sable.binding

import com.dreamdisplays.platform.server.VanillaConfigKt
import com.dreamdisplays.platform.server.VanillaServerState
import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData
import com.dreamdisplays.platform.server.managers.DisplayManager
import com.dreamdisplays.platform.server.utils.RegionUtil
import dev.apehum.dreamdisplays.sable.companion.contains
import dev.apehum.dreamdisplays.sable.companion.subLevelAt
import dev.apehum.dreamdisplays.sable.companion.subLevelById
import dev.apehum.dreamdisplays.sable.companion.subLevelsIntersecting
import dev.apehum.dreamdisplays.sable.companion.toLocalBlock
import dev.ryanhcode.sable.companion.SubLevelAccess
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import java.util.UUID

fun bindDisplay(
    server: MinecraftServer,
    displayId: UUID,
): DisplayBinding? =
    (DisplayManager.getDisplayData(displayId) as? VanillaDisplayData)
        ?.let { detectBinding(server, it) }

fun bindDisplaysInSubLevel(
    server: MinecraftServer,
    known: Map<UUID, DisplayBinding>,
    worldKey: String,
    subLevelId: UUID,
): List<DisplayBinding> {
    val level = RegionUtil.INSTANCE.getLevelByKey(server, worldKey) ?: return emptyList()
    val subLevel = level.subLevelById(subLevelId) ?: return emptyList()

    return displays()
        .filter { it.worldKey == worldKey && it.id !in known }
        .filter { subLevel.boundingBox().contains(it.box) && !isBuiltInWorld(level, it) }
        .map { subLevel.bindingFor(it) }
}

fun reconcileBindings(
    server: MinecraftServer,
    known: Map<UUID, DisplayBinding>,
): List<BindingChange> {
    val displays = displays()
    val bound =
        displays
            .filter { it.id !in known }
            .mapNotNull { detectBinding(server, it) }
    val stale = known.keys - displays.mapTo(HashSet()) { it.id }

    return bound.map(BindingChange::Bound) + stale.map(BindingChange::Unbound)
}

private fun displays(): List<VanillaDisplayData> = DisplayManager.INSTANCE.displays.filterIsInstance<VanillaDisplayData>()

private fun detectBinding(
    server: MinecraftServer,
    display: VanillaDisplayData,
): DisplayBinding? {
    val level = RegionUtil.INSTANCE.getLevelByKey(server, display.worldKey) ?: return null
    return detectBinding(level, display)
}

private fun detectBinding(
    level: ServerLevel,
    display: VanillaDisplayData,
): DisplayBinding? {
    level
        .subLevelAt(display.pos1)
        ?.let { subLevel ->
            return DisplayBinding(display.id, subLevel.uniqueId, display.pos1, display.pos2)
        }

    val subLevel =
        level
            .subLevelsIntersecting(display.box)
            .firstOrNull { it.boundingBox().contains(display.box) }
            ?: return null

    return if (isBuiltInWorld(level, display)) {
        null
    } else {
        subLevel.bindingFor(display)
    }
}

private fun SubLevelAccess.bindingFor(display: VanillaDisplayData): DisplayBinding =
    DisplayBinding(
        displayId = display.id,
        subLevelId = uniqueId,
        local1 = logicalPose().toLocalBlock(display.pos1),
        local2 = logicalPose().toLocalBlock(display.pos2),
    )

private fun isBuiltInWorld(
    level: ServerLevel,
    display: VanillaDisplayData,
): Boolean {
    val baseMaterial =
        ResourceLocation.tryParse(VanillaConfigKt.getBaseMaterialId(VanillaServerState.INSTANCE.config.settings))
            ?: return false

    return BlockPos
        .betweenClosed(display.pos1, display.pos2)
        .any { position ->
            !level.isLoaded(position) || BuiltInRegistries.BLOCK.getKey(level.getBlockState(position).block) == baseMaterial
        }
}
