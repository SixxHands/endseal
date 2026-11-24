package com.retro.endseal.portal.rules;

import com.retro.endseal.portal.IFrameLookup;
import com.retro.endseal.portal.IPortalRingChecker;
import com.retro.endseal.portal.model.FramePositionData;
import com.retro.endseal.portal.model.RingOffsetData;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class PortalRingChecker implements IPortalRingChecker
{
    private final List<RingOffsetData> _ringOffsets;
    private final int _searchRadius;

    public PortalRingChecker(List<RingOffsetData> ringOffsets, int searchRadius)
    {
        _ringOffsets = List.copyOf(Objects.requireNonNull(ringOffsets, "ringOffsets must not be null"));
        _searchRadius = searchRadius;
    }

    @Override
    public boolean wouldCompletePortal(FramePositionData targetFrame, IFrameLookup frameLookup)
    {
        Objects.requireNonNull(targetFrame, "targetFrame must not be null");
        Objects.requireNonNull(frameLookup, "frameLookup must not be null");

        // for some reason the portal  geomoetry makes this messd up so we check for both the frame, clicking or whatever so if its full it cannot be opened anyways
        for (int deltaX = -_searchRadius; deltaX <= _searchRadius; deltaX++)
        {
            for (int deltaZ = -_searchRadius; deltaZ <= _searchRadius; deltaZ++)
            {
                FramePositionData center = targetFrame.shifted(deltaX, deltaZ);
                if (isRingComplete(center, targetFrame, frameLookup))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isRingComplete(
            FramePositionData center,
            FramePositionData assumedEyed,
            IFrameLookup frameLookup
    )
    {
        // vanillla looks for the frame facing the center for whatever reason, eye state is enough to catch completing it
        for (RingOffsetData offset : _ringOffsets)
        {
            FramePositionData position = center.shifted(offset.deltaX(), offset.deltaZ());
            Optional<Boolean> eyeState = frameLookup.readFrameEyeState(position.x(), position.y(), position.z());
            if (eyeState.isEmpty())
            {
                return false;
            }

            boolean hasEye = eyeState.get() || position.equals(assumedEyed);
            if (!hasEye)
            {
                return false;
            }
        }
        return true;
    }
}
