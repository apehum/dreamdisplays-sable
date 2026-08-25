package dev.apehum.dreamdisplays.sable.network.packets

import dev.apehum.dreamdisplays.sable.MOD_ID
import dev.apehum.dreamdisplays.sable.binding.DisplayBinding
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

data class BindingSnapshotPayload(
    val bindings: List<DisplayBinding>,
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        val TYPE =
            CustomPacketPayload.Type<BindingSnapshotPayload>(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "binding_snapshot"),
            )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, BindingSnapshotPayload> =
            StreamCodec.of(
                { buffer, payload -> buffer.writeBindings(payload.bindings) },
                { buffer -> BindingSnapshotPayload(buffer.readBindings()) },
            )

        fun of(bindings: Collection<DisplayBinding>): BindingSnapshotPayload = BindingSnapshotPayload(bindings.toList())
    }
}
