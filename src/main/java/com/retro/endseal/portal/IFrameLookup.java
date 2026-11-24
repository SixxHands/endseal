package com.retro.endseal.portal;

import java.util.Optional;

@FunctionalInterface
public interface IFrameLookup
{
    Optional<Boolean> readFrameEyeState(int x, int y, int z);
}
