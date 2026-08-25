package dev.apehum.dreamdisplays.sable.mixin

import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData
import com.dreamdisplays.platform.server.utils.RegionUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.AABB
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Mutable
import org.spongepowered.asm.mixin.gen.Accessor

@Suppress("NonJavaMixin", "ktlint:standard:function-naming")
@Mixin(VanillaDisplayData::class)
interface VanillaDisplayDataAccessor {
    @Mutable
    @Accessor("pos1")
    fun sable_setPos1(pos: BlockPos)

    @Mutable
    @Accessor("pos2")
    fun sable_setPos2(pos: BlockPos)

    @Mutable
    @Accessor("facing")
    fun sable_setFacing(facing: Direction)

    @Mutable
    @Accessor("region")
    fun sable_setRegion(region: RegionUtil.RegionData)

    @Mutable
    @Accessor("minX")
    fun sable_setMinX(value: Int)

    @Mutable
    @Accessor("minY")
    fun sable_setMinY(value: Int)

    @Mutable
    @Accessor("minZ")
    fun sable_setMinZ(value: Int)

    @Mutable
    @Accessor("maxX")
    fun sable_setMaxX(value: Int)

    @Mutable
    @Accessor("maxY")
    fun sable_setMaxY(value: Int)

    @Mutable
    @Accessor("maxZ")
    fun sable_setMaxZ(value: Int)

    @Mutable
    @Accessor("box")
    fun sable_setBox(box: AABB)
}
