package net.tixan.alchemist;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import net.milkbowl.vault.economy.Economy;
import net.tixan.alchemist.configuration.ConfigLoader;
import net.tixan.alchemist.economy.VaultEconomy;
import net.tixan.alchemist.inventory.InventoryFactory;
import net.tixan.alchemist.inventory.eventhandler.Handler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Level;

public final class Alchemist extends JavaPlugin {

    private Provider<InventoryFactory> factoryProvider;
    private Provider<ConfigLoader> configLoaderProvider;

    @Override
    public void onEnable() {
        if (economy() == null)
            getLogger().log(Level.WARNING, "Economy plugin not found");
        var injectorModule = new InjectorModule(this);
        Injector inject = Guice.createInjector(injectorModule);

        factoryProvider = inject.getProvider(InventoryFactory.class);
        configLoaderProvider = inject.getProvider(ConfigLoader.class);

        reloadConfig();

        var pluginManager = getServer().getPluginManager();
        for (Class<Handler<?>> abstractHandlerClass : injectorModule.getHandlerList()) {
            Handler handler = inject.getInstance(abstractHandlerClass);
            pluginManager.registerEvent(handler.getClazz(), handler, handler.getEventPriority(), handler, this);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            player.openInventory(factoryProvider.get().createInventory());
            return true;
        }
        return false;
    }

    net.tixan.alchemist.economy.Economy economy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return null;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return null;
        }
        return new VaultEconomy(rsp.getProvider());
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        getDataFolder().mkdirs();
        ConfigLoader loader = configLoaderProvider.get();
        loader.saveDefault(new File(getDataFolder(), "config.json"));
        loader.load();
    }
}
