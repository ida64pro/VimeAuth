package me.artyom.vimeauth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VimeGroupSync extends JavaPlugin {

    private File dataFile;
    private YamlConfiguration dataConfig;
    private final Map<UUID, String> realIpMap = new HashMap<>();
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("api").setExecutor(new AuthCommand(this));
        Bukkit.getPluginManager().registerEvents(new AuthListener(this), this);
        luckPerms = LuckPermsProvider.get();
        loadData();
    }

    public void loadData() {
        dataFile = new File(getDataFolder(), "ips.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getDataConfig() {
        return dataConfig;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public Map<UUID, String> getRealIpMap() {
        return realIpMap;
    }
}