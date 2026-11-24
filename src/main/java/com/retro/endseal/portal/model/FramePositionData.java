package com.retro.endseal.portal.model;

public record FramePositionData(int x, int y, int z)
{
    public FramePositionData shifted(int deltaX, int deltaZ)
    {
        return new FramePositionData(x + deltaX, y, z + deltaZ);
    }
}
