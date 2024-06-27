package net.tixan.alchemist.configuration;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
public class ConfigBase implements Cloneable {
    protected short size = 9;
    protected String title = "Alchemist";
    protected ConfigItem[] fillers = {(new ConfigItem()).modify(e -> e.slot = 7)};
    protected ConfigItem startFiller = new ConfigItem(0, "GREEN_WOOL", "Add potions for craft", new String[0], 1);
    protected ConfigItem notEnoughMoneyFiller = new ConfigItem(0, "RED_WOOL", "Not enough money", new String[0], 1);
    protected int resultSlot = 8;
    protected String displayName = "Big potion!";
    protected String[] loreAfterBuy = new String[]{"Usages remained {times}"};
    protected String[] loreBeforeBuy = new String[]{"Usages remained {times}", "It costs {cost}"};
    protected int costPerLevel = 0;
    protected int potionDuration = 5 * 60 * 20;
    protected int potionAmplifier = 1;

    @Override
    public ConfigBase clone() {
        ConfigBase clone = new ConfigBase();
        clone.size = size;
        clone.title = new String(title.getBytes());
        return clone;
    }
}
