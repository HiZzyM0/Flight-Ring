package cz.lucie.flightring.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.EnderManAngerEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Faithful re-implementation of Enderite's full-armor-set bonuses (the
 * original handler is unregistered by {@link EnderiteCompat} because it
 * force-disabled all other flight). Flight for the full set is granted by
 * {@link cz.lucie.flightring.FlightHandler} through the polite mechanism.
 * <p>
 * Bonuses (all require the full enderite set):
 * - endermen do not get angry when looked at,
 * - void damage teleports the player to the nearest surface instead,
 * - immunity to magic, dragon breath, wither and fire damage,
 * - 25% chance to ignore any other damage,
 * - no fall damage.
 */
public class EnderiteSetBonusHandler {

    @SubscribeEvent
    public void onEnderManAnger(EnderManAngerEvent event) {
        Player player = event.getPlayer();
        if (player != null && EnderiteCompat.isLoaded() && EnderiteCompat.hasFullSet(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!EnderiteCompat.isLoaded() || !EnderiteCompat.hasFullSet(player)) {
            return;
        }

        // Void damage: teleport to the nearest surface instead of dying.
        if (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)) {
            if (player.level() instanceof ServerLevel level && player instanceof ServerPlayer serverPlayer) {
                BlockPos top = findNearestTop(level, serverPlayer.blockPosition(), 24);
                if (top != null) {
                    serverPlayer.teleportTo(level, top.getX() + 0.5D, top.getY() + 0.1D, top.getZ() + 0.5D,
                            java.util.Set.of(), serverPlayer.getYRot(), serverPlayer.getXRot());
                    serverPlayer.fallDistance = 0.0F;
                    event.setCanceled(true);
                }
            }
            return;
        }

        if (event.getSource().is(DamageTypes.MAGIC)
                || event.getSource().is(DamageTypes.INDIRECT_MAGIC)
                || event.getSource().is(DamageTypes.DRAGON_BREATH)
                || event.getSource().is(DamageTypes.WITHER)) {
            event.setCanceled(true);
            return;
        }

        if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
            event.setCanceled(true);
            return;
        }

        if (player.getRandom().nextFloat() < 0.25F) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player
                && EnderiteCompat.isLoaded() && EnderiteCompat.hasFullSet(player)) {
            event.setCanceled(true);
        }
    }

    /** Finds the closest column within {@code radius} that has a solid surface. */
    private static BlockPos findNearestTop(ServerLevel level, BlockPos origin, int radius) {
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (int dx = -radius; dx <= radius; dx += 4) {
            for (int dz = -radius; dz <= radius; dz += 4) {
                BlockPos column = origin.offset(dx, 0, dz);
                BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, column);
                if (top.getY() > level.getMinBuildHeight()) {
                    double dist = dx * dx + dz * dz;
                    if (dist < bestDist) {
                        bestDist = dist;
                        best = top;
                    }
                }
            }
        }
        return best;
    }
}
