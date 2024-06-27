package net.tixan.alchemist.inventory.eventhandler;

import net.tixan.alchemist.Utils;
import net.tixan.alchemist.inventory.AlchemistInventoryContext;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PlayerItemConsumeHandler extends AbstractHandler<PlayerItemConsumeEvent, Void> {

    public PlayerItemConsumeHandler() {
        super(PlayerItemConsumeEvent.class, EventPriority.NORMAL);
    }

    @Override
    public boolean predicate(PlayerItemConsumeEvent event) {
        var current = event.getItem();
        return super.predicate(event)
                && current.hasItemMeta()
                && cont(current).has(AlchemistInventoryContext.resultKey);
    }

    @Override
    public void handle(PlayerItemConsumeEvent event) {
        var key = AlchemistInventoryContext.resultKey;
        var replacement = event.getItem();
        Integer usages = cont(replacement).get(key, PersistentDataType.INTEGER);
        if (usages != null && usages > 1) {
            int remainedUsages = usages - 1;
            replacement.editMeta(m -> {
                cont(m).set(key, PersistentDataType.INTEGER, remainedUsages);
                m.displayName(Utils.format(config().getDisplayName(), remainedUsages, null));
                m.lore(Utils.format(config().getLoreAfterBuy(), remainedUsages, null));
            });
            event.setReplacement(replacement);
        }
    }

    private PersistentDataContainer cont(@NotNull ItemStack stack) {
        if (!stack.hasItemMeta())
            throw new IllegalArgumentException("ItemStack not contains PersistentDataContainer");
        return cont(stack.getItemMeta());
    }

    private PersistentDataContainer cont(@NotNull ItemMeta meta) {
        return meta.getPersistentDataContainer();
    }
}
