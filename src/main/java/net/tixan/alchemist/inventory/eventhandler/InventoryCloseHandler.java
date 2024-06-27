package net.tixan.alchemist.inventory.eventhandler;

import net.tixan.alchemist.inventory.AlchemistInventoryContext;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.stream.Collectors;

public final class InventoryCloseHandler extends AbstractInventoryHandler<InventoryCloseEvent> {

    public InventoryCloseHandler() {
        super(InventoryCloseEvent.class, EventPriority.NORMAL);
    }

    @Override
    public AlchemistInventoryContext getCtx() {
        return super.getCtx();
    }

    @Override
    public void handle(InventoryCloseEvent event) {
        List<ItemStack> cr = getCtx().getCraftItems()
                .stream()
                .filter(getCtx()::canBeInInventory)
                .collect(Collectors.toList());
        ItemStack result = getCtx().getInventory().getItem(getCtx().getResultSlot());
        if (cr.isEmpty())
            if (getCtx().isResult(result))
                cr.add(result);
        PlayerInventory pInv = event.getPlayer().getInventory();
        for (ItemStack st : cr)
            if (st != null)
                pInv.addItem(st);
    }
}
