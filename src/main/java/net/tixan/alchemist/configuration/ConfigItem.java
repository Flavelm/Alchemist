package net.tixan.alchemist.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public class ConfigItem {
    protected int slot = 0;
    protected String type = "RED_WOOL";
    protected String displayName = "";
    protected String[] lore = new String[0];
    protected int count = 1;

    public ConfigItem() {
    }

    public Material type() {
        Material returns = Material.getMaterial(type.toUpperCase(Locale.ROOT));
        if (returns == null)
            throw new RuntimeException("Placeholder not recognized");
        return returns;
    }

    public ItemStack toItemStack(MiniMessage message) {
        ItemStack stack = new ItemStack(this.type(), count);
        stack.editMeta(m -> {
            m.displayName(message.deserialize(this.displayName));
            m.lore(Arrays.stream(lore).map(message::deserialize).toList());
        });
        return stack;
    }

    public ConfigItem modify(Consumer<ConfigItem> consumer) {
        consumer.accept(this);
        return this;
    }
}
