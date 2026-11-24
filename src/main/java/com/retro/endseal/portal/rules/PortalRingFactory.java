package com.retro.endseal.portal.rules;

import com.retro.endseal.EndSealException;
import com.retro.endseal.portal.IPortalRingChecker;
import com.retro.endseal.portal.PortalRingBlueprint;
import com.retro.endseal.portal.model.RingOffsetData;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class PortalRingFactory
{
    private PortalRingFactory()
    {
    }

    public static IPortalRingChecker create(PortalRingBlueprint blueprint) throws EndSealException
    {
        Objects.requireNonNull(blueprint, "blueprint must not be null");
        List<RingOffsetData> offsets = blueprint.ringOffsets();
        if (offsets.isEmpty())
        {
            throw new EndSealException("Portal ring offsets must not be empty.");
        }
        if (blueprint.searchRadius() < 0)
        {
            throw new EndSealException("Portal search radius must not be negative.");
        }

        Set<RingOffsetData> uniqueOffsets = new HashSet<>(offsets);
        if (uniqueOffsets.size() != offsets.size())
        {
            throw new EndSealException("Portal ring offsets must not contain duplicates.");
        }
        return new PortalRingChecker(offsets, blueprint.searchRadius());
    }

    public static IPortalRingChecker createVanillaDefault()
    {
        return new PortalRingChecker(
                PortalRingBlueprint.VanillaRingOffsets,
                PortalRingBlueprint.DefaultSearchRadius
        );
    }
}
