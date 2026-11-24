package com.retro.endseal.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class EndSealConfig
{
    public static final String DefaultRefusalMessage = "The portal is not ready to be opened yet.";
    public static final List<String> DefaultMessages = List.of(
            DefaultRefusalMessage,
            "Something still holds the gateway shut.",
            "The eye will not seat. The way is not yet open.",
            "The portal sleeps. It is not yet time."
    );

    public static final ModConfigSpec ConfigDefinition;
    public static final ModConfigSpec.BooleanValue Sealed;
    public static final ModConfigSpec.BooleanValue AllowPartialFrame;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> Messages;

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("EndSeal: controls whether the End portal can be activated.").push("general");

        Sealed = builder
                .comment(
                        "Master switch. While true, the End portal cannot be completed or ignited.",
                        "Set to false (or use /endseal open) to open the End as a server event."
                )
                .define("sealed", true);

        AllowPartialFrame = builder
                .comment(
                        "If true: players may insert eyes up to the LAST one; only the completing",
                        "eye is refused, so the frame can look almost-ready.",
                        "If false: no eye of ender can be inserted into the frame at all."
                )
                .define("allowPartialFrame", true);

        Messages = builder
                .comment(
                        "Action-bar lines shown when a player is refused. One is picked at random.",
                        "Edit freely. Keep them short because the action bar truncates long text."
                )
                .defineListAllowEmpty(
                        "messages",
                        DefaultMessages,
                        () -> "",
                        value -> value instanceof String text && !text.isEmpty()
                );

        builder.pop();
        ConfigDefinition = builder.build();
    }

    private EndSealConfig()
    {
    }
}
