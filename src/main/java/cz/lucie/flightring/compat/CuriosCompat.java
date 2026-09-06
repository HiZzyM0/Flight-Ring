package cz.lucie.flightring.compat;

import cz.lucie.flightring.FlightringMod;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * Only ever classloaded when the "curios" mod is present
 * (guarded by ModList.get().isLoaded("curios") at the call site).
 */
public final class CuriosCompat {

    private CuriosCompat() {
    }

    public static boolean hasRingEquipped(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .map(inv -> inv.isEquipped(FlightringMod.FLIGHT_RING.get()))
                .orElse(false);
    }
}
