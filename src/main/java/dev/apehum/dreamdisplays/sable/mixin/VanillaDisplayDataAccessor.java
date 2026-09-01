package dev.apehum.dreamdisplays.sable.mixin;

import com.dreamdisplays.platform.server.datatypes.display.VanillaDisplayData;
import com.dreamdisplays.platform.server.utils.RegionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VanillaDisplayData.class)
public interface VanillaDisplayDataAccessor {
    @Mutable
    @Accessor("pos1")
    void sable$setPos1(BlockPos pos);

    @Mutable
    @Accessor("pos2")
    void sable$setPos2(BlockPos pos);

    @Mutable
    @Accessor("facing")
    void sable$setFacing(Direction facing);

    @Mutable
    @Accessor("region")
    void sable$setRegion(RegionUtil.RegionData region);

    @Mutable
    @Accessor("minX")
    void sable$setMinX(int value);

    @Mutable
    @Accessor("minY")
    void sable$setMinY(int value);

    @Mutable
    @Accessor("minZ")
    void sable$setMinZ(int value);

    @Mutable
    @Accessor("maxX")
    void sable$setMaxX(int value);

    @Mutable
    @Accessor("maxY")
    void sable$setMaxY(int value);

    @Mutable
    @Accessor("maxZ")
    void sable$setMaxZ(int value);

    @Mutable
    @Accessor("box")
    void sable$setBox(AABB box);
}
