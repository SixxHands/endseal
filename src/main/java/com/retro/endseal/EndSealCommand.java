package com.retro.endseal;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * /endseal open|close|status — flips the seal at runtime so you can "open the End"
 * as a live server event without editing the config file. Requires op (level 2).
 *
 * Note: this updates the in-memory config value and saves it. If you prefer the
 * config file to be the single source of truth, you can remove this class; the
 * mod works fine on the config value alone.
 */
@EventBusSubscriber(modid = EndSeal.MODID)
public final class EndSealCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("endseal")
            .requires(src -> src.hasPermission(2))
            .then(Commands.literal("open").executes(ctx -> {
                EndSealConfig.SEALED.set(false);
                EndSealConfig.SEALED.save();
                ctx.getSource().sendSuccess(
                    () -> Component.literal("The End is now OPEN. The portal may be completed."), true);
                return 1;
            }))
            .then(Commands.literal("close").executes(ctx -> {
                EndSealConfig.SEALED.set(true);
                EndSealConfig.SEALED.save();
                ctx.getSource().sendSuccess(
                    () -> Component.literal("The End is now SEALED. The portal cannot be completed."), true);
                return 1;
            }))
            .then(Commands.literal("status").executes(ctx -> {
                boolean sealed = EndSealConfig.SEALED.get();
                ctx.getSource().sendSuccess(
                    () -> Component.literal("End portal is currently " + (sealed ? "SEALED" : "OPEN") + "."), false);
                return 1;
            }))
        );
    }

    private EndSealCommand() {}
}
