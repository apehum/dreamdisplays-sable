package dev.apehum.dreamdisplays.sable.binding

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

data class DisplayBinding(
    val displayId: UUID,
    val subLevelId: UUID,
    val local1: BlockPos,
    val local2: BlockPos,
) {
    val box =
        AABB(
            min(local1.x, local2.x).toDouble(),
            min(local1.y, local2.y).toDouble(),
            min(local1.z, local2.z).toDouble(),
            (max(local1.x, local2.x) + 1).toDouble(),
            (max(local1.y, local2.y) + 1).toDouble(),
            (max(local1.z, local2.z) + 1).toDouble(),
        )

    val localMin =
        BlockPos(
            min(local1.x, local2.x),
            min(local1.y, local2.y),
            min(local1.z, local2.z),
        )
}

sealed interface BindingChange {
    val displayId: UUID

    data class Bound(
        val binding: DisplayBinding,
    ) : BindingChange {
        override val displayId: UUID get() = binding.displayId
    }

    data class Unbound(
        override val displayId: UUID,
    ) : BindingChange
}
