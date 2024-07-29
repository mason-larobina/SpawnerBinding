package me.laz.spawnerbinding;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public final class SpawnerBinding extends JavaPlugin {
    public static Logger LOGGER = null;
    private static SpawnerBinding INSTANCE = null;

    public static SpawnerBinding getInstance() {
        return Objects.requireNonNull(INSTANCE);
    }

    public SpawnerBinding() {
        super();
        INSTANCE = this;
        LOGGER = this.getLogger();
    }

    @Override
    public void onLoad() {
        SpawnerBindingCommand.registerCommands();
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new SpawnerListener(), this);
    }
}