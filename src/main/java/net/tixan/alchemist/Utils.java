package net.tixan.alchemist;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    @Getter
    private static final Map<Integer, Color> colorMap;
    @Inject
    private static MiniMessage miniMessage;

    static {
        Map<Integer, Color> preColorMap = new HashMap<>();
        PotionEffectType[] effectTypes = PotionEffectType.values();
        for (PotionEffectType effectType : effectTypes)
            preColorMap.put(effectType.hashCode(), effectType.getColor());
        colorMap = ImmutableMap.copyOf(preColorMap);
    }

    @Nullable
    public static ItemMeta meta(@Nullable ItemStack stack) {
        if (stack == null)
            return null;
        if (!stack.hasItemMeta())
            return null;
        return stack.getItemMeta();
    }

    public static List<Component> format(String[] getter, Integer times, Long costs) {
        return Arrays.stream(getter)
                .map(e -> miniMessage.deserialize(
                        e.replace("{times}", numberToString(times))
                                .replace("{cost}", numberToString(costs))))
                .toList();
    }

    public static Component format(String getter, Integer times, Long costs) {
        return miniMessage.deserialize(
                getter
                        .replace("{times}", numberToString(times))
                        .replace("{cost}", numberToString(costs)));
    }

    public static String numberToString(Number number) {
        return number != null ? number.toString() : "";
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.isEmpty();
    }
}
