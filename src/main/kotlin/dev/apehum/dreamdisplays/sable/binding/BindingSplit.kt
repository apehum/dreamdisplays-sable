package dev.apehum.dreamdisplays.sable.binding

import com.dreamdisplays.platform.server.utils.RegionUtil
import dev.apehum.dreamdisplays.sable.companion.subLevelLocalOf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import java.util.UUID

fun rebindSplitDisplays(
    server: MinecraftServer,
    known: Map<UUID, DisplayBinding>,
    worldKey: String,
    subLevelId: UUID,
): List<DisplayBinding> {
    val level = RegionUtil.INSTANCE.getLevelByKey(server, worldKey) ?: return emptyList()

    return known.values
        .filter { it.subLevelId != subLevelId }
        .mapNotNull { it.movedInto(level, subLevelId) }
}

private fun DisplayBinding.movedInto(
    level: ServerLevel,
    subLevelId: UUID,
): DisplayBinding? {
    if (level.hasBaseMaterial(local1, local2)) return null

    val moved1 = level.subLevelLocalOf(this.subLevelId, subLevelId, local1) ?: return null
    val moved2 = level.subLevelLocalOf(this.subLevelId, subLevelId, local2) ?: return null
    if (!level.hasBaseMaterial(moved1, moved2)) return null

    return DisplayBinding(displayId, subLevelId, moved1, moved2)
}
