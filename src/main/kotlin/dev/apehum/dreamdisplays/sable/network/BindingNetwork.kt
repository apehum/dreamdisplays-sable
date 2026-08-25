package dev.apehum.dreamdisplays.sable.network

import dev.apehum.dreamdisplays.sable.client.binding.ClientBindings
import dev.apehum.dreamdisplays.sable.network.packets.BindingSnapshotPayload
import dev.apehum.dreamdisplays.sable.network.packets.BindingUpdatePayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

private const val PROTOCOL_VERSION = "1"

fun registerBindingChannel(event: RegisterPayloadHandlersEvent) {
    event
        .registrar(PROTOCOL_VERSION)
        .optional()
        .playToClient(BindingSnapshotPayload.TYPE, BindingSnapshotPayload.STREAM_CODEC) { payload, context ->
            context.enqueueWork { ClientBindings.replaceAll(payload.bindings) }
        }.playToClient(BindingUpdatePayload.TYPE, BindingUpdatePayload.STREAM_CODEC) { payload, context ->
            context.enqueueWork { ClientBindings.update(payload.bound, payload.unbound) }
        }
}

fun ServerPlayer.sendPayload(payload: CustomPacketPayload) {
    PacketDistributor.sendToPlayer(this, payload)
}

fun broadcastPayload(payload: CustomPacketPayload) {
    PacketDistributor.sendToAllPlayers(payload)
}
