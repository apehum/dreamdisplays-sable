package dev.apehum.dreamdisplays.sable.companion

import com.dreamdisplays.api.display.placement.PlacementTransform
import dev.ryanhcode.sable.companion.ClientSubLevelAccess
import dev.ryanhcode.sable.companion.SubLevelAccess
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

fun SubLevelAccess.placementAt(
    anchor: BlockPos,
    partialTick: Float,
): PlacementTransform {
    val pose = (this as? ClientSubLevelAccess)?.renderPose(partialTick) ?: logicalPose()
    val origin = pose.transformPosition(Vec3.atLowerCornerOf(anchor))
    val orientation = pose.orientation()
    val scale = pose.scale()

    return PlacementTransform(
        anchor.x,
        anchor.y,
        anchor.z,
        origin.x,
        origin.y,
        origin.z,
        orientation.x(),
        orientation.y(),
        orientation.z(),
        orientation.w(),
        scale.x(),
        scale.y(),
        scale.z(),
    )
}
