package net.tixan.alchemist.economy;

import org.bukkit.OfflinePlayer;

public interface Economy {

    boolean isEnabled();

    boolean add(OfflinePlayer player, double count);

    boolean remove(OfflinePlayer player, double count);

    boolean has(OfflinePlayer player, double count);

    double balance(OfflinePlayer player);
}
