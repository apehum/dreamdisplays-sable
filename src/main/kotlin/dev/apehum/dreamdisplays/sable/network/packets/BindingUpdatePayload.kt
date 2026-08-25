package dev.apehum.dreamdisplays.sable.network.packets

import dev.apehum.dreamdisplays.sable.MOD_ID
import dev.apehum.dreamdisplays.sable.binding.BindingChange
import dev.apehum.dreamdisplays.sable.binding.DisplayBinding
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import java.util.UUID

data class BindingUpdatePayload(
    val bound: List<DisplayBinding>,
    val unbound: List<UUID>,
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        val TYPE =
            CustomPacketPayload.Type<BindingUpdatePayload>(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "binding_update"),
            )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, BindingUpdatePayload> =
            StreamCodec.of(
                { buffer, payload ->
                    buffer.writeBindings(payload.bound)
                    buffer.writeDisplayIds(payload.unbound)
                },
                { buffer -> BindingUpdatePayload(buffer.readBindings(), buffer.readDisplayIds()) },
            )

        fun of(changes: List<BindingChange>): BindingUpdatePayload =
            BindingUpdatePayload(
                bound = changes.filterIsInstance<BindingChange.Bound>().map(BindingChange.Bound::binding),
                unbound = changes.filterIsInstance<BindingChange.Unbound>().map(BindingChange.Unbound::displayId),
            )
    }
}
