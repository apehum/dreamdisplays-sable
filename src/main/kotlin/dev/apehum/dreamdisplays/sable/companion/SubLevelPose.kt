package dev.apehum.dreamdisplays.sable.companion

import dev.ryanhcode.sable.companion.math.Pose3dc
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3dc

data class SubLevelPose(
    val position: Vec3,
    val rotationPoint: Vec3,
    val orientation: Quaterniond,
    val scale: Vec3,
) {
    fun transformBlock(local: BlockPos): BlockPos = BlockPos.containing(transform(Vec3(local.x + 0.5, local.y + 0.5, local.z + 0.5)))

    fun transformDirection(direction: Vec3): Vec3 {
        val rotated = Vector3d(direction.x, direction.y, direction.z)
        orientation.transform(rotated)

        return Vec3(rotated.x, rotated.y, rotated.z)
    }

    private fun transform(local: Vec3): Vec3 {
        val offset =
            Vector3d(
                (local.x - rotationPoint.x) * scale.x,
                (local.y - rotationPoint.y) * scale.y,
                (local.z - rotationPoint.z) * scale.z,
            )
        orientation.transform(offset)

        return Vec3(position.x + offset.x, position.y + offset.y, position.z + offset.z)
    }
}

fun Pose3dc.snapshot(): SubLevelPose =
    SubLevelPose(
        position = position().toVec3(),
        rotationPoint = rotationPoint().toVec3(),
        orientation = Quaterniond(orientation()),
        scale = scale().toVec3(),
    )

private fun Vector3dc.toVec3(): Vec3 = Vec3(x(), y(), z())
