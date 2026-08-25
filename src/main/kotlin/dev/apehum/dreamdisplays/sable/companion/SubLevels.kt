package dev.apehum.dreamdisplays.sable.companion

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer
import dev.ryanhcode.sable.companion.SableCompanion
import dev.ryanhcode.sable.companion.SubLevelAccess
import dev.ryanhcode.sable.companion.math.BoundingBox3d
import dev.ryanhcode.sable.companion.math.BoundingBox3dc
import dev.ryanhcode.sable.companion.math.Pose3dc
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.UUID

fun Level.subLevelAt(pos: BlockPos): SubLevelAccess? = SableCompanion.INSTANCE.getContaining(this, pos)

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

fun Pose3dc.toLocalBlock(pos: BlockPos): BlockPos {
    val local = transformPositionInverse(Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5))

    return BlockPos(Mth.floor(local.x), Mth.floor(local.y), Mth.floor(local.z))
}
