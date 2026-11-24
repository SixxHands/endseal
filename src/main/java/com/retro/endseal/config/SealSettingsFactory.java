package com.retro.endseal.config;

public final class SealSettingsFactory
{
    private SealSettingsFactory()
    {
    }

    public static ISealSettings createNeoForgeSettings()
    {
        return new NeoForgeSealSettings();
    }
}
