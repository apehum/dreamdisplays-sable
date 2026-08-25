package dev.apehum.dreamdisplays.sable.network.packets

import dev.apehum.dreamdisplays.sable.binding.DisplayBinding
import net.minecraft.network.FriendlyByteBuf
import java.util.UUID

internal fun FriendlyByteBuf.writeBindings(bindings: Collection<DisplayBinding>) {
    writeVarInt(bindings.size)
    bindings.forEach(::writeBinding)
}

internal fun FriendlyByteBuf.readBindings(): List<DisplayBinding> = List(readVarInt()) { readBinding() }

internal fun FriendlyByteBuf.writeDisplayIds(displayIds: Collection<UUID>) {
    writeVarInt(displayIds.size)
    displayIds.forEach(::writeUUID)
}

internal fun FriendlyByteBuf.readDisplayIds(): List<UUID> = List(readVarInt()) { readUUID() }

private fun FriendlyByteBuf.writeBinding(binding: DisplayBinding) {
    writeUUID(binding.displayId)
    writeUUID(binding.subLevelId)
    writeBlockPos(binding.local1)
    writeBlockPos(binding.local2)
}

private fun FriendlyByteBuf.readBinding(): DisplayBinding =
    DisplayBinding(
        displayId = readUUID(),
        subLevelId = readUUID(),
        local1 = readBlockPos(),
        local2 = readBlockPos(),
    )
