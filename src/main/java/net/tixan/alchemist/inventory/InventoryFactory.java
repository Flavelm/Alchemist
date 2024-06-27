package net.tixan.alchemist.inventory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tixan.alchemist.Utils;
import net.tixan.alchemist.configuration.Config;
import net.tixan.alchemist.configuration.ConfigLoader;
import org.apache.commons.lang3.Range;
import org.bukkit.Server;
import org.bukkit.inventory.Inventory;

public class InventoryFactory {

    @Inject
    private ConfigLoader configLoader;
    @Inject
    private Server server;
    @Inject
    private Injector injector;
    @Inject
    private MiniMessage miniMessage;

    public Inventory createInventory() {
        Config config = configLoader.get();
        throwIfInvalidConfig(config);
        AlchemistInventoryContext holder = new AlchemistInventoryContext(
                invHolder -> server.createInventory(invHolder, config.getSize(), Utils.format(config.getTitle(), null, null)),
                config
        );
        injector.injectMembers(holder);
        Inventory inv = holder.getInventory();
        for (var confItem : config.getFillers()) {
            inv.setItem(confItem.getSlot(), holder.createFiller(confItem.toItemStack(miniMessage)));
        }
        inv.setItem(config.getResultSlot(), holder.startFiller());
        return holder.getInventory();
    }

    private void throwIfInvalidConfig(Config config) {
        if (config.getSize() % 9 != 0)
            throw new IllegalArgumentException("Inventory size can be 9, 18, 27, 36, 45, 54");
        Range<Integer> range = Range.between(0, (int) config.getSize());
        if (!range.contains(config.getResultSlot()))
            throw new IllegalArgumentException("Result slot out of range");
        if (config.getFilledSlotSet().contains(config.getResultSlot()))
            throw new IllegalArgumentException("Result in filled slot!");
        for (int fillerSlot : config.getFilledSlotSet())
            if (!range.contains(fillerSlot))
                throw new IllegalArgumentException("Filler slot out of range");
    }
}
