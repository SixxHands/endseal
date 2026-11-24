package com.retro.endseal.server;

import com.retro.endseal.EndSealException;
import com.retro.endseal.portal.model.FramePositionData;
import com.retro.endseal.portal.model.SealDecisionData;
import com.retro.endseal.portal.rules.EyeInsertionPolicy;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class EndSealEvents
{
    private static final Logger Log = LoggerFactory.getLogger(EndSealEvents.class);

    private final EyeInsertionPolicy _insertionPolicy;

    public EndSealEvents(EyeInsertionPolicy insertionPolicy)
    {
        _insertionPolicy = Objects.requireNonNull(insertionPolicy, "insertionPolicy must not be null");
    }

    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        ItemStack stack = event.getItemStack();
        if (!stack.is(Items.ENDER_EYE))
        {
            return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof EndPortalFrameBlock) || state.getValue(EndPortalFrameBlock.HAS_EYE))
        {
            return;
        }

        SealDecisionData decision = checkInsertionAtEventBoundary(level, pos);
        if (!decision.refused())
        {
            return;
        }

        // RightClickBlock fires before vanilla seats the eye, so cancelling prevents both insertion and portal ignition.
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);

        // deny everyone and everything idc no matteer what you click with and if you find a way to get around it I will patch it you cannot stop me
        Player player = event.getEntity();
        if (!level.isClientSide()
                && player instanceof ServerPlayer serverPlayer
                && event.getHand() == InteractionHand.MAIN_HAND
                && decision.refusalMessage().isPresent())
        {
            serverPlayer.displayClientMessage(Component.literal(decision.refusalMessage().get()), true);
        }
    }

    private SealDecisionData checkInsertionAtEventBoundary(Level level, BlockPos pos)
    {
        FramePositionData framePosition = new FramePositionData(pos.getX(), pos.getY(), pos.getZ());
        LevelFrameLookup frameLookup = new LevelFrameLookup(level);
        try
        {
            return _insertionPolicy.checkInsertion(
                    framePosition,
                    frameLookup,
                    bound -> level.getRandom().nextInt(bound)
            );
        }
        catch (EndSealException ex)
        {
            Log.error("Failed checking EndSeal state at {}", pos, ex);
            return SealDecisionData.allow();
        }
    }
}
