package dev.apehum.dreamdisplays.sable.companion

import dev.ryanhcode.sable.companion.SableCompanion
import dev.ryanhcode.sable.companion.SubLevelAccess
import dev.ryanhcode.sable.companion.math.Pose3dc
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

fun Level.subLevelAt(position: BlockPos): SubLevelAccess? = SableCompanion.INSTANCE.getContaining(this, position)

fun Level.isInPlotGrid(position: BlockPos): Boolean = SableCompanion.INSTANCE.isInPlotGrid(this, position)

fun Pose3dc.toLocalBlock(position: Vec3): BlockPos {
    val local = transformPositionInverse(position)

    return BlockPos(Mth.floor(local.x), Mth.floor(local.y), Mth.floor(local.z))
}
