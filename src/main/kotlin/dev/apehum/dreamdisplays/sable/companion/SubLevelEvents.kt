package dev.apehum.dreamdisplays.sable.companion

import dev.ryanhcode.sable.api.sublevel.SubLevelObserver
import dev.ryanhcode.sable.platform.SableEventPlatform
import dev.ryanhcode.sable.sublevel.SubLevel
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason
import net.minecraft.server.level.ServerLevel
import java.util.UUID

fun observeSubLevels(
    onAdded: (ServerLevel, UUID) -> Unit,
    onRemoved: (UUID, SubLevelPose) -> Unit,
) {
    SableEventPlatform.INSTANCE.onSubLevelContainerReady { level, container ->
        if (level !is ServerLevel) return@onSubLevelContainerReady

        container.addObserver(
            object : SubLevelObserver {
                override fun onSubLevelAdded(subLevel: SubLevel) = onAdded(level, subLevel.uniqueId)

                override fun onSubLevelRemoved(
                    subLevel: SubLevel,
                    reason: SubLevelRemovalReason,
                ) {
                    if (reason == SubLevelRemovalReason.REMOVED) {
                        onRemoved(subLevel.uniqueId, subLevel.logicalPose().snapshot())
                    }
                }
            },
        )
    }
}
