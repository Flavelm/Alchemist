package net.tixan.alchemist.inventory.eventhandler;

import com.google.inject.Inject;
import net.tixan.alchemist.Utils;
import net.tixan.alchemist.inventory.AlchemistInventoryContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class InventoryClickHandler extends AbstractInventoryHandler<InventoryClickEvent> {

    @Inject
    private JavaPlugin plugin;

    public InventoryClickHandler() {
        super(InventoryClickEvent.class, EventPriority.HIGHEST);
    }

    @Override
    public boolean predicate(InventoryClickEvent event) {
        return super.predicate(event)
                && event.getSlot() != -999;
    }

    @Override
    public void handle(InventoryClickEvent event) {
        Player whoClicked = (Player) event.getWhoClicked();
        ItemStack stack = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if (getCtx().isFiller(stack)) {
            event.setCancelled(true);
            AlchemistInventoryContext ctx = getCtx();
            getServer().getScheduler().runTaskLater(plugin, () -> ctx.updateTransaction(whoClicked), 1);
            return;
        }

        if (validateInventory(event.getClickedInventory())) {
            if (event.getSlot() == getCtx().getResultSlot() && stack == null) { //Disallow add to slot item
                event.setCancelled(true);
                return;
            }

            if (getCtx().getCraftSlots().contains(event.getSlot())) {
                if (!getCtx().canBeInInventory(cursor) && !Utils.isEmpty(cursor)
                        || !getCtx().canBeInInventory(stack) && !Utils.isEmpty(stack)) { //Disallow add items to craftslots
                    event.setCancelled(true);
                    return;
                }
            }

            if (!Utils.isEmpty(cursor) && !Utils.isEmpty(stack)) { //Disallow add items to craftslots
                event.setCancelled(true);
                return;
            }
        } else {
            ClickType click = event.getClick();
            if (!getCtx().canBeInInventory(stack) && click.isShiftClick()) { //Disallow add items to craftslots
                event.setCancelled(true);
                return;
            }
        }

        if (validateInventory(event.getClickedInventory())
                && event.getSlot() == getCtx().getResultSlot()
                && getCtx().isResult(stack)) {
            getCtx().endTransaction(whoClicked);
        } else {
            AlchemistInventoryContext ctx = getCtx();
            getServer().getScheduler().runTaskLater(plugin, () -> ctx.updateTransaction(whoClicked), 1);
        }
    }
}
