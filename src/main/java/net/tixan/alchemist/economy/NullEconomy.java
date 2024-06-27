package net.tixan.alchemist.economy;

import com.google.inject.Inject;
import org.bukkit.OfflinePlayer;

import java.util.logging.Logger;

public class NullEconomy implements Economy {

    @Inject
    private Logger logger;

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean add(OfflinePlayer player, double count) {
        logger.info("Depositing " + count + " to player " + player.getName());
        return true;
    }

    @Override
    public boolean remove(OfflinePlayer player, double count) {
        logger.info("Withdrawing " + count + " from player " + player.getName());
        return true;
    }

    @Override
    public boolean has(OfflinePlayer player, double count) {
        logger.info("Checking " + player.getName() + "'s bank");
        return true;
    }

    @Override
    public double balance(OfflinePlayer player) {
        logger.info("Checking " + player.getName() + "'s balance");
        return 0;
    }
}
