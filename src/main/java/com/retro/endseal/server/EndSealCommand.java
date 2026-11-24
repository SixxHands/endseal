package com.retro.endseal.server;

import com.mojang.brigadier.CommandDispatcher;
import com.retro.endseal.EndSealException;
import com.retro.endseal.config.ISealSettings;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Objects;

public final class EndSealCommand
{
    private static final int OperatorPermissionLevel = 2;

    private final ISealSettings _settings;

    public EndSealCommand(ISealSettings settings)
    {
        _settings = Objects.requireNonNull(settings, "settings must not be null");
    }

    public void onRegisterCommands(RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSourceStack> commands = event.getDispatcher();
        commands.register(Commands.literal("endseal")
                .requires(source -> source.hasPermission(OperatorPermissionLevel))
                .then(Commands.literal("open").executes(ctx -> setSealState(ctx.getSource(), false)))
                .then(Commands.literal("close").executes(ctx -> setSealState(ctx.getSource(), true)))
                .then(Commands.literal("status").executes(ctx -> reportSealState(ctx.getSource())))
        );
    }

    private int setSealState(CommandSourceStack source, boolean sealed)
    {
        try
        {
            // Updates the in-memory config value and writes it to disk so the seal state survives restarts.
            _settings.setSealed(sealed);
            String feedback = sealed
                    ? "The End is now SEALED. The portal cannot be completed."
                    : "The End is now OPEN. The portal may be completed.";
            source.sendSuccess(() -> Component.literal(feedback), true);
            return 1;
        }
        catch (EndSealException ex)
        {
            source.sendFailure(Component.literal(ex.getMessage()));
            return 0;
        }
    }

    private int reportSealState(CommandSourceStack source)
    {
        try
        {
            boolean sealed = _settings.isSealed();
            source.sendSuccess(
                    () -> Component.literal("End portal is currently " + (sealed ? "SEALED" : "OPEN") + "."),
                    false
            );
            return 1;
        }
        catch (EndSealException ex)
        {
            source.sendFailure(Component.literal(ex.getMessage()));
            return 0;
        }
    }
}
