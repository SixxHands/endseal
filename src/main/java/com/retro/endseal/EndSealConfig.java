package com.retro.endseal;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * Config for EndSeal. Common config (server-authoritative for gameplay).
 *
 * - sealed: master switch. true = portal cannot be completed. Set false to open the End.
 * - allowPartialFrame: if true, players may place eyes up to (but not including) the
 *   completing eye, so the frame looks almost-ready; only the final ignition is refused.
 *   if false, no eye can be inserted at all.
 * - messages: action-bar lines shown when a player is refused. One is chosen at random.
 */
public final class EndSealConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue SEALED;
    public static final ModConfigSpec.BooleanValue ALLOW_PARTIAL_FRAME;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MESSAGES;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();

        b.comment("EndSeal — controls whether the End portal can be activated.").push("general");

        SEALED = b
            .comment("Master switch. While true, the End portal cannot be completed/ignited.",
                     "Set to false (or use /endseal open) to open the End as a server event.")
            .define("sealed", true);

        ALLOW_PARTIAL_FRAME = b
            .comment("If true: players may insert eyes up to the LAST one; only the completing",
                     "eye is refused, so the frame can look almost-ready.",
                     "If false: no eye of ender can be inserted into the frame at all.")
            .define("allowPartialFrame", true);

        MESSAGES = b
            .comment("Action-bar lines shown when a player is refused. One is picked at random.",
                     "Edit freely. Keep them short — action bar truncates long text.")
            .defineListAllowEmpty("messages",
                List.of(
                    "The portal is not ready to be opened yet.",
                    "Something still holds the gateway shut.",
                    "The eye will not seat. The way is not yet open.",
                    "The portal sleeps. It is not yet time."
                ),
                () -> "",
                o -> o instanceof String s && !s.isEmpty());

        b.pop();
        SPEC = b.build();
    }

    private EndSealConfig() {}
}
