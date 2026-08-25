package dev.apehum.dreamdisplays.sable.binding

import net.minecraft.core.BlockPos
import java.util.UUID

data class DisplayBinding(
    val displayId: UUID,
    val subLevelId: UUID,
    val local1: BlockPos,
    val local2: BlockPos,
)

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
