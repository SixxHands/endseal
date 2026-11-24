package com.retro.endseal;

import com.retro.endseal.config.EndSealConfig;
import com.retro.endseal.config.ISealSettings;
import com.retro.endseal.config.SealSettingsFactory;
import com.retro.endseal.portal.IPortalRingChecker;
import com.retro.endseal.portal.rules.EyeInsertionPolicy;
import com.retro.endseal.portal.rules.PortalRingFactory;
import com.retro.endseal.portal.rules.RefusalMessagePicker;
import com.retro.endseal.server.EndSealCommand;
import com.retro.endseal.server.EndSealEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(EndSeal.ModId)
public final class EndSeal
{
    public static final String ModId = "endseal";

    public EndSeal(IEventBus modEventBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.COMMON, EndSealConfig.ConfigDefinition);

        ISealSettings settings = SealSettingsFactory.createNeoForgeSettings();
        IPortalRingChecker ringChecker = PortalRingFactory.createVanillaDefault();
        RefusalMessagePicker messagePicker = RefusalMessagePicker.createDefault();
        EyeInsertionPolicy insertionPolicy = new EyeInsertionPolicy(settings, ringChecker, messagePicker);

        EndSealEvents events = new EndSealEvents(insertionPolicy);
        EndSealCommand command = new EndSealCommand(settings);

        NeoForge.EVENT_BUS.addListener(events::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(command::onRegisterCommands);
    }
}
