package com.retro.endseal.portal.rules;

import com.retro.endseal.EndSealException;
import com.retro.endseal.portal.IFrameLookup;
import com.retro.endseal.portal.IPortalRingChecker;
import com.retro.endseal.portal.PortalRingBlueprint;
import com.retro.endseal.portal.model.FramePositionData;
import com.retro.endseal.portal.model.RingOffsetData;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class PortalRingCheckerTest
{
    private final IPortalRingChecker _checker = PortalRingFactory.createVanillaDefault();

    @Test
    void detectsCompletingTwelfthEyeOnAnyEdgeOfRing()
    {
        FramePositionData center = new FramePositionData(100, 32, -40);
        for (RingOffsetData missingOffset : PortalRingBlueprint.VanillaRingOffsets)
        {
            FramePositionData missingFrame = center.shifted(missingOffset.deltaX(), missingOffset.deltaZ());
            IFrameLookup lookup = buildRingLookup(center, missingFrame);

            assertTrue(_checker.wouldCompletePortal(missingFrame, lookup));
        }
    }

    @Test
    void allowsEleventhEyeWhenAnotherFrameIsStillEmpty()
    {
        FramePositionData center = new FramePositionData(0, 64, 0);
        FramePositionData firstEmpty = center.shifted(-1, -2);
        FramePositionData secondEmpty = center.shifted(1, 2);

        Map<FramePositionData, Boolean> frames = new HashMap<>();
        for (RingOffsetData offset : PortalRingBlueprint.VanillaRingOffsets)
        {
            FramePositionData pos = center.shifted(offset.deltaX(), offset.deltaZ());
            boolean eyed = !pos.equals(firstEmpty) && !pos.equals(secondEmpty);
            frames.put(pos, eyed);
        }

        IFrameLookup lookup = (x, y, z) -> Optional.ofNullable(frames.get(new FramePositionData(x, y, z)));
        assertFalse(_checker.wouldCompletePortal(firstEmpty, lookup));
    }

    @Test
    void returnsFalseWhenPortalRingIsMissingAFrameBlock()
    {
        FramePositionData center = new FramePositionData(10, 40, 10);
        FramePositionData target = center.shifted(0, -2);
        FramePositionData brokenBlock = center.shifted(2, 1);

        Map<FramePositionData, Boolean> frames = new HashMap<>();
        for (RingOffsetData offset : PortalRingBlueprint.VanillaRingOffsets)
        {
            FramePositionData pos = center.shifted(offset.deltaX(), offset.deltaZ());
            if (!pos.equals(brokenBlock))
            {
                frames.put(pos, !pos.equals(target));
            }
        }

        IFrameLookup lookup = (x, y, z) -> Optional.ofNullable(frames.get(new FramePositionData(x, y, z)));
        assertFalse(_checker.wouldCompletePortal(target, lookup));
    }

    @Test
    void rejectsEmptyOrDuplicateRingOffsetsInFactory()
    {
        PortalRingBlueprint emptyBlueprint = new PortalRingBlueprint().ringOffsets(List.of());
        assertThrows(EndSealException.class, () -> PortalRingFactory.create(emptyBlueprint));

        PortalRingBlueprint duplicateBlueprint = new PortalRingBlueprint().ringOffsets(
                List.of(new RingOffsetData(0, 2), new RingOffsetData(0, 2))
        );
        assertThrows(EndSealException.class, () -> PortalRingFactory.create(duplicateBlueprint));

        PortalRingBlueprint negativeRadius = new PortalRingBlueprint().searchRadius(-1);
        assertThrows(EndSealException.class, () -> PortalRingFactory.create(negativeRadius));
    }

    private static IFrameLookup buildRingLookup(FramePositionData center, FramePositionData emptyFrame)
    {
        Map<FramePositionData, Boolean> frames = new HashMap<>();
        for (RingOffsetData offset : PortalRingBlueprint.VanillaRingOffsets)
        {
            FramePositionData pos = center.shifted(offset.deltaX(), offset.deltaZ());
            frames.put(pos, !pos.equals(emptyFrame));
        }
        return (x, y, z) -> Optional.ofNullable(frames.get(new FramePositionData(x, y, z)));
    }
}
