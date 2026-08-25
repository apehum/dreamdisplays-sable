package dev.apehum.dreamdisplays.sable.companion

import dev.ryanhcode.sable.companion.ClientSubLevelAccess
import dev.ryanhcode.sable.companion.math.Pose3dc
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import java.util.UUID

data class SubLevelTransform(
    val origin: Vec3,
    val orientation: Quaternionf,
    val scaleX: Float,
    val scaleY: Float,
    val scaleZ: Float,
)

fun Level.subLevelTransform(
    subLevelId: UUID,
    local: BlockPos,
    partialTick: Float,
): SubLevelTransform? {
    val subLevel = subLevelById(subLevelId) ?: return null
    val pose = (subLevel as? ClientSubLevelAccess)?.renderPose(partialTick) ?: subLevel.logicalPose()

    return pose.transformAt(local)
}

private fun Pose3dc.transformAt(local: BlockPos): SubLevelTransform {
    val orientation = orientation()
    val scale = scale()

    return SubLevelTransform(
        origin = transformPosition(Vec3.atLowerCornerOf(local)),
        orientation = Quaternionf(orientation),
        scaleX = scale.x().toFloat(),
        scaleY = scale.y().toFloat(),
        scaleZ = scale.z().toFloat(),
    )
}
