package dev.apehum.dreamdisplays.sable.companion

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer
import dev.ryanhcode.sable.companion.SableCompanion
import dev.ryanhcode.sable.companion.SubLevelAccess
import dev.ryanhcode.sable.companion.math.BoundingBox3d
import dev.ryanhcode.sable.companion.math.BoundingBox3dc
import dev.ryanhcode.sable.companion.math.Pose3dc
import net.minecraft.core.BlockPos
import net.minecraft.core.Position
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.UUID

fun Level.subLevelAt(position: BlockPos): SubLevelAccess? = SableCompanion.INSTANCE.getContaining(this, position)

fun Level.subLevelById(subLevelId: UUID): SubLevelAccess? =
    SubLevelContainer
        .getContainer(this)
        ?.getSubLevel(subLevelId)

fun Level.subLevelsIntersecting(box: AABB): List<SubLevelAccess> =
    SableCompanion.INSTANCE
        .getAllIntersecting(this, BoundingBox3d(box))
        .toList()

fun BoundingBox3dc.contains(box: AABB): Boolean =
    contains(box.minX, box.minY, box.minZ) &&
        contains(box.maxX, box.maxY, box.maxZ)

fun Pose3dc.toLocalBlock(position: BlockPos): BlockPos = toLocalBlock(position.center)

fun Level.subLevelLocalOf(
    subLevelId: UUID,
    position: BlockPos,
): BlockPos? {
    val subLevel = subLevelById(subLevelId) ?: return null
    val world = SableCompanion.INSTANCE.projectOutOfSubLevel(this, position.center as Position)

    return subLevel.logicalPose().toLocalBlock(world)
}

fun Pose3dc.toLocalBlock(position: Vec3): BlockPos {
    val local = transformPositionInverse(position)

    return BlockPos(Mth.floor(local.x), Mth.floor(local.y), Mth.floor(local.z))
}

fun Level.subLevelLocalOf(
    sourceId: UUID,
    targetId: UUID,
    local: BlockPos,
): BlockPos? {
    val source = subLevelById(sourceId)?.logicalPose() ?: return null
    val target = subLevelById(targetId)?.logicalPose() ?: return null

    return target.toLocalBlock(source.toWorld(local))
}

fun Pose3dc.toWorld(local: BlockPos): Vec3 = transformPosition(Vec3(local.x + 0.5, local.y + 0.5, local.z + 0.5))
