package com.retro.endseal;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

/**
 * EndSeal — silently refuses End portal activation until configured otherwise.
 *
 * The End Stronghold and its portal frame still generate normally; players can
 * find it, build around it, and (optionally) place all but the final eye. What
 * they cannot do is complete/ignite the portal. When a player tries, they get a
 * configurable action-bar message so it reads as intentional design, not a bug.
 *
 * Flip {@code sealed} to false in the config (or the gameplay command) to "open"
 * the End as a server event later.
 */
@Mod(EndSeal.MODID)
public class EndSeal {
    public static final String MODID = "endseal";

    public EndSeal(IEventBus modEventBus, ModContainer modContainer) {
        // Register the config. The event handler registers itself to the game bus
        // via @EventBusSubscriber (see EndSealEvents).
        modContainer.registerConfig(ModConfig.Type.COMMON, EndSealConfig.SPEC);
    }
}
