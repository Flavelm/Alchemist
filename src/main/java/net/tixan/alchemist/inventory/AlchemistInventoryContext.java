package net.tixan.alchemist.inventory;

import com.google.inject.Inject;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tixan.alchemist.Utils;
import net.tixan.alchemist.configuration.Config;
import net.tixan.alchemist.economy.Economy;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AlchemistInventoryContext implements InventoryHolder {

    public static final NamespacedKey resultKey = new NamespacedKey("alchemist", "result");
    public static final NamespacedKey fillerKey = new NamespacedKey("alchemist", "filler");

    private final Inventory inventory;
    private final Config config;
    @Getter
    private final Set<Integer> craftSlots;
    @Inject
    private Economy economy;
    @Inject
    private MiniMessage miniMessage;
    @Inject
    private Server server;
    @Inject
    private JavaPlugin plugin;

    protected AlchemistInventoryContext(Function<InventoryHolder, Inventory> consumer, Config configForTransaction) {
        this.inventory = consumer.apply(this);
        Set<Integer> filledSlotsRange = configForTransaction.getFilledSlotSet();
        this.craftSlots = IntStream.range(0, this.inventory.getSize())
                .filter(i -> !filledSlotsRange.contains(i) && i != configForTransaction.getResultSlot())
                .boxed()
                .collect(Collectors.toUnmodifiableSet());
        this.config = configForTransaction;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public List<ItemStack> getCraftItems() {
        ItemStack[] invContent = this.inventory.getContents();
        List<ItemStack> craftItems = new ArrayList<>();
        for (int slot : this.craftSlots)
            craftItems.add(invContent[slot]);
        return craftItems;
    }

    public void updateTransaction(Player player) {
        List<PotionEffect> craftEffect = getCraftItems()
                .stream()
                .map(this::asPotionEffect)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();
        if (craftEffect.isEmpty()) {
            this.inventory.setItem(this.config.getResultSlot(), startFiller());
            return;
        }
        PotionEffect sample = craftEffect.getFirst();
        int sampleHash = sample.hashCode();
        int usagesCount = (int) craftEffect.stream().filter(potion -> {
            return sampleHash == potion.hashCode();
        }).count();
        if (usagesCount <= 1) {
            this.inventory.setItem(this.config.getResultSlot(), startFiller());
            return;
        }
        if (!economy.has(player, cost(usagesCount))) {
            this.inventory.setItem(this.config.getResultSlot(), notEnoughMoneyFiller());
            return;
        }
        ItemStack newPotion = new ItemStack(Material.POTION);
        newPotion.editMeta(m -> {
            m.getPersistentDataContainer().set(resultKey, PersistentDataType.INTEGER, usagesCount);
            m.displayName(Utils.format(config.getDisplayName(), usagesCount, cost(usagesCount)));
            m.lore(Utils.format(config.getLoreBeforeBuy(), usagesCount, cost(usagesCount)));
            ((PotionMeta) m).addCustomEffect(sample, true);
            ((PotionMeta) m).setColor(Utils.getColorMap().get(sample.getType().hashCode()));
        });
        this.inventory.setItem(this.config.getResultSlot(), newPotion);
    }

    public void endTransaction(Player player) {
        ItemStack s = getInventory().getItem(config.getResultSlot());
        if (s == null)
            throw new RuntimeException("Whats?");
        var meta = getMeta(config.getResultSlot());
        if (meta == null)
            return;
        var cont = meta.getPersistentDataContainer();
        if (!cont.has(resultKey))
            return;
        int usagesCount = cont.get(resultKey, PersistentDataType.INTEGER);
        long cost = this.cost(usagesCount);
        if (!economy.has(player, cost)) {
            this.inventory.setItem(this.config.getResultSlot(), notEnoughMoneyFiller());
            return;
        }
        economy.remove(player, cost);
        for (int craftSlot : craftSlots)
            this.inventory.setItem(craftSlot, null);
        s.editMeta(m -> {
            m.displayName(Utils.format(config.getDisplayName(), usagesCount, cost(usagesCount)));
            m.lore(Utils.format(config.getLoreAfterBuy(), usagesCount, cost(usagesCount)));
        });
        server.getScheduler().runTaskLater(plugin, () -> this.inventory.setItem(this.config.getResultSlot(), startFiller()), 1);
    }

    @Nullable
    private List<PotionEffect> asPotionEffect(ItemStack stack) {
        if (stack != null
                && stack.getType() == Material.POTION
                && stack.hasItemMeta()
                && stack.getItemMeta() instanceof PotionMeta potion) {
            List<PotionEffect> effects = new ArrayList<>();
            PotionData data = potion.getBasePotionData();
            if (data != null) {
                PotionEffectType potionEffectType = data.getType().getEffectType();
                if (potionEffectType != null) {
                    effects.add(potionEffectType.createEffect(config.getPotionDuration(), config.getPotionAmplifier()));
                }
            }
            if (potion.hasCustomEffects()) {
                effects.addAll(potion.getCustomEffects());
            }
            return effects;
        }
        return null;
    }

    @Nullable
    public ItemMeta getMeta(int slot) {
        var item = getInventory().getItem(slot);
        if (item == null)
            return null;
        if (!item.hasItemMeta())
            return null;
        return item.getItemMeta();
    }

    public boolean canBeInInventory(@Nullable ItemStack stack) {
        ItemMeta meta = Utils.meta(stack);
        if (!(meta instanceof PotionMeta p))
            return false;
        return !p.hasCustomEffects() || p.getCustomEffects().isEmpty();
    }

    public boolean isResult(@Nullable ItemStack stack) {
        if (Utils.meta(stack) instanceof PotionMeta p)
            return p.getPersistentDataContainer().has(resultKey);
        return false;
    }

    public boolean isFiller(@Nullable ItemStack stack) {
        var meta = Utils.meta(stack);
        return meta != null && meta.getPersistentDataContainer().has(fillerKey);
    }

    public int getResultSlot() {
        return config.getResultSlot();
    }

    public long cost(int count) {
        return (long) config.getCostPerLevel() * count;
    }

    public ItemStack startFiller() {
        return createFiller(this.config.getStartFiller().toItemStack(miniMessage));
    }

    public ItemStack notEnoughMoneyFiller() {
        return createFiller(this.config.getNotEnoughMoneyFiller().toItemStack(miniMessage));
    }

    public ItemStack createFiller(ItemStack stack) {
        stack.editMeta(m ->
                m.getPersistentDataContainer()
                        .set(AlchemistInventoryContext.fillerKey, PersistentDataType.BOOLEAN, true));
        return stack;
    }
}
