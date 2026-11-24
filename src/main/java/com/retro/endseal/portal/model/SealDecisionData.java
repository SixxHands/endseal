package com.retro.endseal.portal.model;

import java.util.Objects;
import java.util.Optional;

public record SealDecisionData(boolean refused, Optional<String> refusalMessage)
{
    public SealDecisionData
    {
        Objects.requireNonNull(refusalMessage, "refusalMessage must not be null");
    }

    public static SealDecisionData allow()
    {
        return new SealDecisionData(false, Optional.empty());
    }

    public static SealDecisionData refuse(String message)
    {
        if (message == null || message.isBlank())
        {
            throw new IllegalArgumentException("Refusal message must not be blank");
        }
        return new SealDecisionData(true, Optional.of(message));
    }
}
