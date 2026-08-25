package dev.apehum.dreamdisplays.sable.client.screen

import com.dreamdisplays.api.media.audio.model.ListenerPose
import com.dreamdisplays.api.media.audio.model.SourcePlane
import com.dreamdisplays.platform.client.displays.DisplayScreen
import com.dreamdisplays.platform.client.render.DisplayGeometry
import dev.apehum.dreamdisplays.sable.client.binding.ClientBindings
import dev.apehum.dreamdisplays.sable.companion.SubLevelTransform
import dev.apehum.dreamdisplays.sable.companion.subLevelTransform
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f

fun DisplayScreen.boundSourcePlane(): SourcePlane? {
    val binding = ClientBindings[uuid] ?: return null
    val minecraft = Minecraft.getInstance()
    val level = minecraft.level ?: return null

    val partialTick = minecraft.timer.getGameTimeDeltaPartialTick(false)
    val min = binding.localMin
    val transform = level.subLevelTransform(binding.subLevelId, min, partialTick) ?: return null
    val local = DisplayGeometry.INSTANCE.worldPose(min.x, min.y, min.z, width, height, facing)

    val center =
        transform.origin.add(
            transform.rotate(
                (local.centerX - min.x) * transform.scaleX,
                (local.centerY - min.y) * transform.scaleY,
                (local.centerZ - min.z) * transform.scaleZ,
            ),
        )
    val normal = transform.rotate(local.normalX, local.normalY, local.normalZ)
    val uAxis = transform.rotate(local.uAxisX, local.uAxisY, local.uAxisZ)
    val vAxis = transform.rotate(local.vAxisX, local.vAxisY, local.vAxisZ)

    return SourcePlane(
        center.x,
        center.y,
        center.z,
        normal.x,
        normal.y,
        normal.z,
        uAxis.x,
        uAxis.y,
        uAxis.z,
        vAxis.x,
        vAxis.y,
        vAxis.z,
        width.toDouble(),
        height.toDouble(),
    )
}

private fun SubLevelTransform.rotate(
    x: Double,
    y: Double,
    z: Double,
): Vec3 {
    val direction = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
    orientation.transform(direction)

    return Vec3(direction.x.toDouble(), direction.y.toDouble(), direction.z.toDouble())
}

data class BoundProbe(
    val plane: SourcePlane,
    val listener: ListenerPose,
)

fun DisplayScreen.boundProbe(listener: ListenerPose): BoundProbe? {
    val binding = ClientBindings[uuid] ?: return null
    val minecraft = Minecraft.getInstance()
    val level = minecraft.level ?: return null

    val partialTick = minecraft.timer.getGameTimeDeltaPartialTick(false)
    val min = binding.localMin
    val transform = level.subLevelTransform(binding.subLevelId, min, partialTick) ?: return null
    val local = DisplayGeometry.INSTANCE.worldPose(min.x, min.y, min.z, width, height, facing)

    val ear = transform.toLocal(min, Vec3(listener.x, listener.y, listener.z))
    val forward = transform.rotateInverse(listener.forwardX, listener.forwardY, listener.forwardZ)
    val up = transform.rotateInverse(listener.upX, listener.upY, listener.upZ)

    return BoundProbe(
        plane =
            SourcePlane(
                local.centerX,
                local.centerY,
                local.centerZ,
                local.normalX,
                local.normalY,
                local.normalZ,
                local.uAxisX,
                local.uAxisY,
                local.uAxisZ,
                local.vAxisX,
                local.vAxisY,
                local.vAxisZ,
                width.toDouble(),
                height.toDouble(),
            ),
        listener =
            ListenerPose(
                ear.x,
                ear.y,
                ear.z,
                forward.x,
                forward.y,
                forward.z,
                up.x,
                up.y,
                up.z,
            ),
    )
}

private fun SubLevelTransform.toLocal(
    anchor: BlockPos,
    world: Vec3,
): Vec3 {
    val delta =
        Vector3f(
            (world.x - origin.x).toFloat(),
            (world.y - origin.y).toFloat(),
            (world.z - origin.z).toFloat(),
        )
    orientation.transformInverse(delta)

    return Vec3(
        anchor.x + delta.x.toDouble() / scaleX,
        anchor.y + delta.y.toDouble() / scaleY,
        anchor.z + delta.z.toDouble() / scaleZ,
    )
}

private fun SubLevelTransform.rotateInverse(
    x: Double,
    y: Double,
    z: Double,
): Vec3 {
    val direction = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
    orientation.transformInverse(direction)

    return Vec3(direction.x.toDouble(), direction.y.toDouble(), direction.z.toDouble())
}
