package net.tixan.alchemist.economy;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import net.tixan.alchemist.configuration.ConfigLoader;
import org.bukkit.OfflinePlayer;

@RequiredArgsConstructor
public class VaultEconomy implements Economy {

    private final net.milkbowl.vault.economy.Economy economy;
    @Inject
    private ConfigLoader configLoader;

    @Override
    public boolean isEnabled() {
        return configLoader.get().getCostPerLevel() != 0;
    }

    @Override
    public boolean add(OfflinePlayer player, double count) {
        return economy.depositPlayer(player, count).transactionSuccess();
    }

    @Override
    public boolean remove(OfflinePlayer player, double count) {
        return economy.withdrawPlayer(player, count).transactionSuccess();
    }

    @Override
    public boolean has(OfflinePlayer player, double count) {
        return economy.hasAccount(player) && economy.has(player, count);
    }

    @Override
    public double balance(OfflinePlayer player) {
        if (economy.hasAccount(player))
            return economy.getBalance(player);
        return 0;
    }
}
