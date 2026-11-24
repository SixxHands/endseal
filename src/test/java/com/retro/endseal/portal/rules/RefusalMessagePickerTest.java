package com.retro.endseal.portal.rules;

import com.retro.endseal.config.EndSealConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class RefusalMessagePickerTest
{
    private final RefusalMessagePicker _picker = RefusalMessagePicker.createDefault();

    @Test
    void picksConfiguredMessageAndSkipsBlankEntries()
    {
        List<String> lines = List.of("   ", "First line", "", "Second line");

        assertEquals("First line", _picker.pick(lines, bound -> 0));
        assertEquals("Second line", _picker.pick(lines, bound -> 1));
    }

    @Test
    void fallsBackToDefaultWhenConfiguredListIsEmptyOrBlank()
    {
        assertEquals(EndSealConfig.DefaultRefusalMessage, _picker.pick(List.of(), bound -> 0));
        assertEquals(EndSealConfig.DefaultRefusalMessage, _picker.pick(List.of("  ", ""), bound -> 0));
    }

    @Test
    void throwsWhenRandomIndexIsOutOfBounds()
    {
        List<String> lines = List.of("Only line");

        assertThrows(IllegalArgumentException.class, () -> _picker.pick(lines, bound -> 2));
        assertThrows(IllegalArgumentException.class, () -> _picker.pick(lines, bound -> -1));
    }
}
