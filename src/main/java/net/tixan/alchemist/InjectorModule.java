package net.tixan.alchemist;

import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tixan.alchemist.configuration.ConfigLoader;
import net.tixan.alchemist.economy.Economy;
import net.tixan.alchemist.economy.NullEconomy;
import net.tixan.alchemist.inventory.InventoryFactory;
import net.tixan.alchemist.inventory.eventhandler.Handler;
import net.tixan.alchemist.inventory.eventhandler.InventoryClickHandler;
import net.tixan.alchemist.inventory.eventhandler.InventoryCloseHandler;
import net.tixan.alchemist.inventory.eventhandler.PlayerItemConsumeHandler;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class InjectorModule extends AbstractModule {

    private final Alchemist plugin;

    @Getter
    protected List<Class<Handler<?>>> handlerList = new ArrayList<>();

    @Override
    protected void configure() {
        bind(Server.class).toInstance(plugin.getServer());
        bind(JavaPlugin.class).toInstance(plugin);

        bind(InventoryFactory.class);

        handlerBind(InventoryClickHandler.class).asEagerSingleton();
        handlerBind(InventoryCloseHandler.class).asEagerSingleton();
        handlerBind(PlayerItemConsumeHandler.class).asEagerSingleton();

        bind(ConfigLoader.class).asEagerSingleton();
        bind(MiniMessage.class).toInstance(MiniMessage
                .builder()
                .postProcessor(component -> component.decoration(TextDecoration.ITALIC, false))
                .build());

        Economy economy = plugin.economy();
        if (economy != null)
            bind(Economy.class).toInstance(economy);
        else
            bind(Economy.class).toInstance(new NullEconomy());

        requestStaticInjection(Utils.class);
    }

    private <T extends Handler<?>> AnnotatedBindingBuilder<T> handlerBind(Class<T> tClass) {
        handlerList.add((Class<Handler<?>>) tClass);
        return bind(tClass);
    }
}
