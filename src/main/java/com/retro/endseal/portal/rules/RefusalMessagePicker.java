package com.retro.endseal.portal.rules;

import com.retro.endseal.config.EndSealConfig;

import java.util.List;
import java.util.Objects;
import java.util.function.IntUnaryOperator;

public final class RefusalMessagePicker
{
    private final String _fallbackMessage;

    public RefusalMessagePicker(String fallbackMessage)
    {
        if (fallbackMessage == null || fallbackMessage.isBlank())
        {
            throw new IllegalArgumentException("fallbackMessage must not be blank");
        }
        _fallbackMessage = fallbackMessage;
    }

    public static RefusalMessagePicker createDefault()
    {
        return new RefusalMessagePicker(EndSealConfig.DefaultRefusalMessage);
    }

    public String pick(List<String> configuredMessages, IntUnaryOperator randomIndex)
    {
        Objects.requireNonNull(randomIndex, "randomIndex must not be null");
        if (configuredMessages == null || configuredMessages.isEmpty())
        {
            return _fallbackMessage;
        }

        List<String> validMessages = configuredMessages.stream()
                .filter(line -> line != null && !line.isBlank())
                .toList();
        if (validMessages.isEmpty())
        {
            return _fallbackMessage;
        }

        int chosenIndex = randomIndex.applyAsInt(validMessages.size());
        if (chosenIndex < 0 || chosenIndex >= validMessages.size())
        {
            throw new IllegalArgumentException(
                    "Random index out of bounds: " + chosenIndex + " for size " + validMessages.size()
            );
        }
        return validMessages.get(chosenIndex);
    }
}
