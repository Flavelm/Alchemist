package net.tixan.alchemist.inventory.eventhandler;

import net.tixan.alchemist.inventory.AlchemistInventoryContext;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;

public abstract class AbstractInventoryHandler<T extends InventoryEvent> extends AbstractHandler<T, AlchemistInventoryContext> {

    public AbstractInventoryHandler(Class<T> clazz, EventPriority eventPriority) {
        super(clazz, eventPriority);
    }

    @Override
    protected AlchemistInventoryContext useCtx(T event) {
        if (event.getInventory().getHolder() instanceof AlchemistInventoryContext dest)
            return dest;
        return null;
    }

    @Override
    public boolean predicate(T event) {
        return validateInventory(event.getInventory());
    }

    protected boolean validateInventory(@Nullable Inventory inv) {
        return inv != null
                && inv.getHolder() instanceof AlchemistInventoryContext;
    }
}
