package com.retro.endseal.portal;

import com.retro.endseal.portal.model.FramePositionData;

public interface IPortalRingChecker
{
    boolean wouldCompletePortal(FramePositionData targetFrame, IFrameLookup frameLookup);
}
