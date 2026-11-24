package com.retro.endseal.portal.rules;

import com.retro.endseal.EndSealException;
import com.retro.endseal.config.ISealSettings;
import com.retro.endseal.portal.IFrameLookup;
import com.retro.endseal.portal.PortalRingBlueprint;
import com.retro.endseal.portal.model.FramePositionData;
import com.retro.endseal.portal.model.RingOffsetData;
import com.retro.endseal.portal.model.SealDecisionData;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class EyeInsertionPolicyTest
{
    @Test
    void allowsInsertionWhenUnsealed() throws EndSealException
    {
        ISealSettings settings = fixedSettings(false, false, List.of("Sealed"));
        EyeInsertionPolicy policy = createPolicy(settings);

        SealDecisionData decision = policy.checkInsertion(
                new FramePositionData(0, 64, 0),
                (x, y, z) -> Optional.of(true),
                bound -> 0
        );

        assertFalse(decision.refused());
        assertTrue(decision.refusalMessage().isEmpty());
    }

    @Test
    void refusesAllInsertionsWhenPartialFramesAreDisabled() throws EndSealException
    {
        ISealSettings settings = fixedSettings(true, false, List.of("No eyes allowed."));
        EyeInsertionPolicy policy = createPolicy(settings);

        SealDecisionData decision = policy.checkInsertion(
                new FramePositionData(0, 64, 0),
                (x, y, z) -> Optional.empty(),
                bound -> 0
        );

        assertTrue(decision.refused());
        assertEquals(Optional.of("No eyes allowed."), decision.refusalMessage());
    }

    @Test
    void allowsNonCompletingEyeAndRefusesTwelfthEyeWhenPartialFramesEnabled() throws EndSealException
    {
        ISealSettings settings = fixedSettings(true, true, List.of("Not ready yet."));
        EyeInsertionPolicy policy = createPolicy(settings);
        FramePositionData center = new FramePositionData(0, 64, 0);
        FramePositionData eleventh = center.shifted(-1, -2);
        FramePositionData twelfth = center.shifted(0, -2);

        Map<FramePositionData, Boolean> frames = new HashMap<>();
        for (RingOffsetData offset : PortalRingBlueprint.VanillaRingOffsets)
        {
            FramePositionData pos = center.shifted(offset.deltaX(), offset.deltaZ());
            frames.put(pos, !pos.equals(eleventh) && !pos.equals(twelfth));
        }
        IFrameLookup lookup = (x, y, z) -> Optional.ofNullable(frames.get(new FramePositionData(x, y, z)));

        SealDecisionData eleventhDecision = policy.checkInsertion(eleventh, lookup, bound -> 0);
        assertFalse(eleventhDecision.refused());

        frames.put(eleventh, true);
        SealDecisionData twelfthDecision = policy.checkInsertion(twelfth, lookup, bound -> 0);
        assertTrue(twelfthDecision.refused());
        assertEquals(Optional.of("Not ready yet."), twelfthDecision.refusalMessage());
    }

    @Test
    void propagatesConfigErrorsFromSettings()
    {
        ISealSettings failingSettings = new ISealSettings()
        {
            @Override
            public boolean isSealed() throws EndSealException
            {
                throw new EndSealException("EndSeal config is not loaded yet.");
            }

            @Override
            public boolean allowsPartialFrame() throws EndSealException
            {
                return true;
            }

            @Override
            public List<String> refusalMessages() throws EndSealException
            {
                return List.of();
            }

            @Override
            public void setSealed(boolean sealed) throws EndSealException
            {
                throw new EndSealException("EndSeal config is not loaded yet.");
            }
        };

        EyeInsertionPolicy policy = createPolicy(failingSettings);
        assertThrows(
                EndSealException.class,
                () -> policy.checkInsertion(new FramePositionData(0, 0, 0), (x, y, z) -> Optional.empty(), bound -> 0)
        );
    }

    private static EyeInsertionPolicy createPolicy(ISealSettings settings)
    {
        return new EyeInsertionPolicy(
                settings,
                PortalRingFactory.createVanillaDefault(),
                RefusalMessagePicker.createDefault()
        );
    }

    private static ISealSettings fixedSettings(boolean sealed, boolean allowPartial, List<String> messages)
    {
        return new ISealSettings()
        {
            private boolean _sealed = sealed;

            @Override
            public boolean isSealed()
            {
                return _sealed;
            }

            @Override
            public boolean allowsPartialFrame()
            {
                return allowPartial;
            }

            @Override
            public List<String> refusalMessages()
            {
                return messages;
            }

            @Override
            public void setSealed(boolean nextSealed)
            {
                _sealed = nextSealed;
            }
        };
    }
}
