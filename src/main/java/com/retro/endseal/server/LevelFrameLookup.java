package com.retro.endseal.server;

import com.retro.endseal.portal.IFrameLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Optional;

public final class LevelFrameLookup implements IFrameLookup
{
    private final Level _level;

    public LevelFrameLookup(Level level)
    {
        _level = Objects.requireNonNull(level, "level must not be null");
    }

    @Override
    public Optional<Boolean> readFrameEyeState(int x, int y, int z)
    {
        BlockState state = _level.getBlockState(new BlockPos(x, y, z));
        if (!(state.getBlock() instanceof EndPortalFrameBlock))
        {
            return Optional.empty();
        }
        return Optional.of(state.getValue(EndPortalFrameBlock.HAS_EYE));
    }
}
