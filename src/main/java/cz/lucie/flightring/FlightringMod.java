package cz.lucie.flightring;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(FlightringMod.MOD_ID)
public class FlightringMod {
    public static final String MOD_ID = "flightring";

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<Item> FLIGHT_RING = ITEMS.register("flight_ring",
            () -> new FlightRingItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public FlightringMod(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addToCreativeTab);
        modEventBus.addListener(this::onCommonSetup);
        NeoForge.EVENT_BUS.register(new FlightHandler());
        NeoForge.EVENT_BUS.register(new cz.lucie.flightring.compat.EnderiteSetBonusHandler());
    }

    private void onCommonSetup(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
        if (cz.lucie.flightring.compat.EnderiteCompat.isLoaded()) {
            event.enqueueWork(cz.lucie.flightring.compat.EnderiteCompat::disarmArmorSetEffects);
        }
    }

    private static final ResourceKey<CreativeModeTab> TOOLS_TAB = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB, ResourceLocation.withDefaultNamespace("tools_and_utilities"));

    private void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == TOOLS_TAB) {
            event.accept(FLIGHT_RING);
        }
    }
}
