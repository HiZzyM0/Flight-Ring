package cz.lucie.flightring.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Compatibility with the "Enderite" mod.
 * <p>
 * Enderite's ArmorSetEffects handler force-disables {@code mayfly} and
 * {@code flying} every tick for any survival player not wearing the full
 * enderite armor set, which breaks every other flight source (including our
 * ring). We unregister that handler and re-implement all of its set bonuses
 * faithfully in {@link EnderiteSetBonusHandler}, minus the hostile behavior.
 */
public final class EnderiteCompat {

    private static final String ARMOR_SET_EFFECTS = "com.nightmare.enderite.content.event.ArmorSetEffects";

    private static final ResourceLocation HELMET = ResourceLocation.fromNamespaceAndPath("enderite", "enderite_helmet");
    private static final ResourceLocation CHESTPLATE = ResourceLocation.fromNamespaceAndPath("enderite", "enderite_chestplate");
    private static final ResourceLocation LEGGINGS = ResourceLocation.fromNamespaceAndPath("enderite", "enderite_leggings");
    private static final ResourceLocation BOOTS = ResourceLocation.fromNamespaceAndPath("enderite", "enderite_boots");

    private EnderiteCompat() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded("enderite");
    }

    /** Unregisters Enderite's flight-clobbering event handler class. */
    public static void disarmArmorSetEffects() {
        try {
            Class<?> clazz = Class.forName(ARMOR_SET_EFFECTS);
            NeoForge.EVENT_BUS.unregister(clazz);
            System.out.println("[Flightring] Enderite ArmorSetEffects handler unregistered;"
                    + " set bonuses are now provided by Flight Ring (flight-friendly).");
        } catch (Throwable t) {
            System.out.println("[Flightring] Could not unregister Enderite ArmorSetEffects: " + t);
        }
    }

    /** True when the player wears the full enderite armor set (same check as Enderite). */
    public static boolean hasFullSet(Player player) {
        Inventory inv = player.getInventory();
        return is(inv.armor.get(3), HELMET)
                && is(inv.armor.get(2), CHESTPLATE)
                && is(inv.armor.get(1), LEGGINGS)
                && is(inv.armor.get(0), BOOTS);
    }

    private static boolean is(net.minecraft.world.item.ItemStack stack, ResourceLocation id) {
        return !stack.isEmpty() && BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(id);
    }
}
