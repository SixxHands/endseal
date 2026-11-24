package com.retro.endseal.portal;

import com.retro.endseal.portal.model.RingOffsetData;

import java.util.List;
import java.util.Objects;

public final class PortalRingBlueprint
{
    public static final int DefaultSearchRadius = 2;

    public static final List<RingOffsetData> VanillaRingOffsets = List.of(
            new RingOffsetData(-1, -2),
            new RingOffsetData(0, -2),
            new RingOffsetData(1, -2),
            new RingOffsetData(-1, 2),
            new RingOffsetData(0, 2),
            new RingOffsetData(1, 2),
            new RingOffsetData(-2, -1),
            new RingOffsetData(-2, 0),
            new RingOffsetData(-2, 1),
            new RingOffsetData(2, -1),
            new RingOffsetData(2, 0),
            new RingOffsetData(2, 1)
    );

    private List<RingOffsetData> _ringOffsets;
    private int _searchRadius;

    public PortalRingBlueprint()
    {
        _ringOffsets = VanillaRingOffsets;
        _searchRadius = DefaultSearchRadius;
    }

    public List<RingOffsetData> ringOffsets()
    {
        return _ringOffsets;
    }

    public int searchRadius()
    {
        return _searchRadius;
    }

    public PortalRingBlueprint ringOffsets(List<RingOffsetData> ringOffsets)
    {
        _ringOffsets = List.copyOf(Objects.requireNonNull(ringOffsets, "ringOffsets must not be null"));
        return this;
    }

    public PortalRingBlueprint searchRadius(int searchRadius)
    {
        _searchRadius = searchRadius;
        return this;
    }
}
