package cz.lucie.flightring;

import cz.lucie.flightring.compat.CuriosCompat;
import cz.lucie.flightring.compat.EnderiteCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Grants creative-style flight while the Ring of Flight is anywhere in the
 * player's inventory (main inventory, hotbar, armor, offhand), equipped in a
 * Curios slot, or while wearing the full Enderite armor set (we take over that
 * set bonus from the Enderite mod - see EnderiteCompat).
 * <p>
 * Two mechanisms are used together for maximum compatibility:
 * 1. NeoForge's CREATIVE_FLIGHT attribute (clean, modern way).
 * 2. Direct {@link Abilities#mayfly} toggling (the classic "angel ring" way).
 * Flight is revoked only if this mod granted it, so creative mode and other
 * flight sources are left alone.
 */
public class FlightHandler {

    private static final ResourceLocation MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(FlightringMod.MOD_ID, "ring_flight");
    private static final String GRANTED_KEY = "flightring_granted";

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        boolean hasFlight = hasFlightSource(player);

        // Mechanism 1: CREATIVE_FLIGHT attribute
        AttributeInstance flight = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        if (flight != null) {
            boolean active = flight.getModifier(MODIFIER_ID) != null;
            if (hasFlight && !active) {
                flight.addTransientModifier(new AttributeModifier(
                        MODIFIER_ID, 1.0D, AttributeModifier.Operation.ADD_VALUE));
            } else if (!hasFlight && active) {
                flight.removeModifier(MODIFIER_ID);
            }
        }

        // Mechanism 2: direct abilities (classic angel-ring approach)
        Abilities abilities = player.getAbilities();
        boolean granted = player.getPersistentData().getBoolean(GRANTED_KEY);
        if (hasFlight) {
            if (!abilities.mayfly) {
                abilities.mayfly = true;
                player.onUpdateAbilities();
            }
            if (!granted) {
                player.getPersistentData().putBoolean(GRANTED_KEY, true);
            }
        } else if (granted) {
            player.getPersistentData().putBoolean(GRANTED_KEY, false);
            if (!player.isCreative() && !player.isSpectator()
                    && (abilities.mayfly || abilities.flying)) {
                abilities.mayfly = false;
                abilities.flying = false;
                player.onUpdateAbilities();
            }
        }
    }

    private boolean hasFlightSource(Player player) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).is(FlightringMod.FLIGHT_RING.get())) {
                return true;
            }
        }
        if (ModList.get().isLoaded("curios") && CuriosCompat.hasRingEquipped(player)) {
            return true;
        }
        return EnderiteCompat.isLoaded() && EnderiteCompat.hasFullSet(player);
    }
}
