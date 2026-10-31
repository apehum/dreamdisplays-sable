package dev.apehum.dreamdisplays.sable.binding

import com.dreamdisplays.api.server.DreamDisplaysServer
import com.dreamdisplays.platform.server.utils.RegionUtil
import dev.apehum.dreamdisplays.sable.companion.subLevelAt
import dev.ryanhcode.sable.sublevel.SubLevel
import net.minecraft.server.level.ServerLevel
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("DreamDisplaysSable/OrphanedDisplays")

fun deleteDisplaysOnRemovedSubLevel(
    level: ServerLevel,
    subLevel: SubLevel,
) {
    val worldKey = RegionUtil.INSTANCE.getLevelKey(level)

    vanillaDisplays()
        .filter { it.worldKey == worldKey && level.subLevelAt(it.pos1)?.uniqueId == subLevel.uniqueId }
        .forEach { display ->
            logger.info("Deleting display {}: its sub-level {} was removed", display.id, subLevel.uniqueId)
            DreamDisplaysServer.display(display.id)?.delete()
        }
}
