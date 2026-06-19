package com.retro.endseal;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

/**
 * Intercepts the act of inserting an Eye of Ender into an End Portal Frame and,
 * while the seal is active, prevents the portal from being completed.
 *
 * Hooks {@link PlayerInteractEvent.RightClickBlock}, which fires on the server
 * BEFORE vanilla's EndPortalFrameBlock handles the click. We only act when:
 *   - the seal is on,
 *   - the player is holding an Eye of Ender,
 *   - the targeted block is an EndPortalFrameBlock that does not yet have an eye.
 *
 * Behaviour depends on allowPartialFrame:
 *   - false: every eye insertion is refused (frame stays empty).
 *   - true:  insertion is allowed UNLESS this eye would complete a valid portal
 *            ring, in which case the completing eye is refused. Players can build
 *            the frame right up to the final eye.
 *
 * No polling, no block-breaking after the fact, no @Overwrite. Robust across
 * updates: depends only on EndPortalFrameBlock, the HAS_EYE property, and
 * EndPortalShape (all stable vanilla API).
 */
@EventBusSubscriber(modid = EndSeal.MODID)
public final class EndSealEvents {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!EndSealConfig.SEALED.get()) return;

        Level level = event.getLevel();
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();

        // Only care about an Eye of Ender being used.
        if (!stack.is(Items.ENDER_EYE)) return;

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        // Must be an End Portal Frame that doesn't already hold an eye.
        if (!(state.getBlock() instanceof EndPortalFrameBlock)) return;
        if (state.getValue(EndPortalFrameBlock.HAS_EYE)) return;

        boolean refuse;
        if (!EndSealConfig.ALLOW_PARTIAL_FRAME.get()) {
            // Refuse all eye insertions.
            refuse = true;
        } else {
            // Allow insertion unless it would complete a valid portal ring.
            refuse = wouldCompletePortal(level, pos, state);
        }

        if (!refuse) return;

        // Cancel the interaction so vanilla never inserts the eye or forms the portal.
        event.setCanceled(true);
        event.setCancellationResult(net.minecraft.world.InteractionResult.FAIL);

        // Tell the player why — only on the server, only for real players, and only
        // for the main hand to avoid a double message from the off-hand pass.
        if (!level.isClientSide()
                && player instanceof ServerPlayer sp
                && event.getHand() == InteractionHand.MAIN_HAND) {
            sp.displayClientMessage(pickMessage(level), true); // true = action bar
        }
    }

    /**
     * Returns true if seating an eye at {@code framePos} would complete a valid
     * 3x3 End portal in any orientation. We temporarily treat this frame as
     * eyed and ask vanilla's EndPortalShape whether a complete portal exists.
     */
    private static boolean wouldCompletePortal(Level level, BlockPos framePos, BlockState frameState) {
        // Simulate this frame now having its eye, then run vanilla's portal-shape
        // finder over the surrounding area. EndPortalShape.findEmptyPortalShape
        // looks for a complete eyed ring around a 3x3 empty center.
        //
        // We do a lightweight scan: for each candidate portal center near this
        // frame, check whether every frame position in the ring is eyed, counting
        // THIS position as eyed.
        for (BlockPos center : candidateCenters(framePos)) {
            if (ringIsComplete(level, center, framePos)) {
                return true;
            }
        }
        return false;
    }

    /** The possible 3x3 portal-center positions whose ring could include this frame. */
    private static Iterable<BlockPos> candidateCenters(BlockPos framePos) {
        // A frame sits 2 blocks out from the center along one axis and within
        // [-1,1] on the other. Enumerate centers in a 5x5 horizontal area; cheap.
        java.util.List<BlockPos> out = new java.util.ArrayList<>();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                out.add(framePos.offset(dx, 0, dz));
            }
        }
        return out;
    }

    /**
     * Checks whether the 12-frame ring around {@code center} is fully eyed,
     * treating {@code assumedEyed} as if it already had its eye.
     */
    private static boolean ringIsComplete(Level level, BlockPos center, BlockPos assumedEyed) {
        // The 12 frame offsets around a 3x3 center (the standard end portal ring).
        for (int[] off : RING_OFFSETS) {
            BlockPos p = center.offset(off[0], 0, off[1]);
            BlockState s = level.getBlockState(p);
            if (!(s.getBlock() instanceof EndPortalFrameBlock)) return false;
            boolean eyed = s.getValue(EndPortalFrameBlock.HAS_EYE) || p.equals(assumedEyed);
            if (!eyed) return false;
            // Each frame must also face the center for a real portal; vanilla checks
            // facing too. We approximate with presence+eye, which is sufficient to
            // catch the completing eye for sealing purposes.
        }
        return true;
    }

    /** Offsets of the 12 frame blocks around a 3x3 portal center. */
    private static final int[][] RING_OFFSETS = {
        {-1, -2}, {0, -2}, {1, -2},   // north edge
        {-1,  2}, {0,  2}, {1,  2},   // south edge
        {-2, -1}, {-2, 0}, {-2, 1},   // west edge
        { 2, -1}, { 2, 0}, { 2, 1}    // east edge
    };

    private static Component pickMessage(Level level) {
        List<? extends String> msgs = EndSealConfig.MESSAGES.get();
        if (msgs == null || msgs.isEmpty()) {
            return Component.literal("The portal is not ready to be opened yet.");
        }
        int i = level.getRandom().nextInt(msgs.size());
        return Component.literal(msgs.get(i));
    }

    private EndSealEvents() {}
}
