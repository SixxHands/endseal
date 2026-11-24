package com.retro.endseal.portal.rules;

import com.retro.endseal.EndSealException;
import com.retro.endseal.config.ISealSettings;
import com.retro.endseal.portal.IFrameLookup;
import com.retro.endseal.portal.IPortalRingChecker;
import com.retro.endseal.portal.model.FramePositionData;
import com.retro.endseal.portal.model.SealDecisionData;

import java.util.Objects;
import java.util.function.IntUnaryOperator;

public final class EyeInsertionPolicy
{
    private final ISealSettings _settings;
    private final IPortalRingChecker _ringChecker;
    private final RefusalMessagePicker _messagePicker;

    public EyeInsertionPolicy(
            ISealSettings settings,
            IPortalRingChecker ringChecker,
            RefusalMessagePicker messagePicker
    )
    {
        _settings = Objects.requireNonNull(settings, "settings must not be null");
        _ringChecker = Objects.requireNonNull(ringChecker, "ringChecker must not be null");
        _messagePicker = Objects.requireNonNull(messagePicker, "messagePicker must not be null");
    }

    public SealDecisionData checkInsertion(
            FramePositionData targetFrame,
            IFrameLookup frameLookup,
            IntUnaryOperator randomIndex
    ) throws EndSealException
    {
        Objects.requireNonNull(targetFrame, "targetFrame must not be null");
        Objects.requireNonNull(frameLookup, "frameLookup must not be null");
        Objects.requireNonNull(randomIndex, "randomIndex must not be null");

        if (!_settings.isSealed())
        {
            return SealDecisionData.allow();
        }

        boolean shouldRefuse = !_settings.allowsPartialFrame()
                || _ringChecker.wouldCompletePortal(targetFrame, frameLookup);
        if (!shouldRefuse)
        {
            return SealDecisionData.allow();
        }

        String message = _messagePicker.pick(_settings.refusalMessages(), randomIndex);
        return SealDecisionData.refuse(message);
    }
}
