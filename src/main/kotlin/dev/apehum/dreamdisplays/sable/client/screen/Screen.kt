package dev.apehum.dreamdisplays.sable.client.screen

import com.dreamdisplays.api.display.model.property.DisplayFacing
import com.dreamdisplays.api.storage.model.FullDisplayData
import com.dreamdisplays.platform.client.displays.DisplayScreen
import com.dreamdisplays.platform.client.render.DisplayGeometry
import com.mojang.blaze3d.vertex.PoseStack
import dev.apehum.dreamdisplays.sable.client.binding.ClientBindings
import dev.apehum.dreamdisplays.sable.companion.subLevelLocalOf
import dev.apehum.dreamdisplays.sable.companion.subLevelTransform
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import java.util.UUID

fun DisplayScreen.boundDistance(position: BlockPos): Double? = boundDistance(uuid, width, height, facing, position)

fun FullDisplayData.boundDistance(position: BlockPos): Double? = boundDistance(uuid, width, height, facing, position)

fun boundDistance(
    displayId: UUID,
    width: Int,
    height: Int,
    facing: DisplayFacing,
    position: BlockPos,
): Double? {
    val binding = ClientBindings[displayId] ?: return null
    val level = Minecraft.getInstance().level ?: return null
    val local = level.subLevelLocalOf(binding.subLevelId, position) ?: return null
    val min = binding.localMin

    return DisplayGeometry.INSTANCE.distanceTo(
        local,
        min.x,
        min.y,
        min.z,
        width,
        height,
        facing,
    )
}

fun PoseStack.applyBoundScreenTransform(
    screen: DisplayScreen,
    camera: Camera,
): Boolean {
    val binding = ClientBindings[screen.uuid] ?: return false
    val minecraft = Minecraft.getInstance()
    val level = minecraft.level ?: return false

    val partialTick = minecraft.timer.getGameTimeDeltaPartialTick(false)
    val transform = level.subLevelTransform(binding.subLevelId, binding.localMin, partialTick) ?: return false

    val cameraPos = camera.position
    translate(
        transform.origin.x - cameraPos.x,
        transform.origin.y - cameraPos.y,
        transform.origin.z - cameraPos.z,
    )
    mulPose(transform.orientation)
    scale(transform.scaleX, transform.scaleY, transform.scaleZ)

    return true
}
