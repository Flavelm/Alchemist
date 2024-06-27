package net.tixan.alchemist.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ConfigLoader {

    private final Path configurationPath;
    @Inject
    private Injector injector;
    private Config config;

    @Inject
    public ConfigLoader(JavaPlugin plugin) {
        File dir = plugin.getDataFolder();
        configurationPath = (new File(dir, "config.json")).toPath();
    }

    public Config get() {
        if (config == null)
            throw new RuntimeException("Load the configuration earlier!");
        return config;
    }

    public void load() {
        try {
            InputStream stream = Files.newInputStream(configurationPath, StandardOpenOption.READ);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            config = gson().fromJson(reader, Config.class);
            config.setFilledSlotSet();
            injector.injectMembers(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveDefault(File to) {
        if (to.exists())
            return;
        Config c = new Config();
        try {
            String def = gson().toJson(c);
            Files.writeString(configurationPath, def, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Gson gson() {
        return new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
                .setLenient()
                .disableHtmlEscaping()
                .create();
    }
}
