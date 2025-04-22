package me.artyom.vimeauth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthCommand implements CommandExecutor {

    private final VimeGroupSync plugin;

    public AuthCommand(VimeGroupSync plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только игрок может использовать эту команду.");
            return true;
        }

        if (args.length != 2 || !args[0].equalsIgnoreCase("auth")) {
            player.sendMessage("Использование: /api auth <токен>");
            return true;
        }

        String token = args[1];
        String ip = plugin.getRealIpMap().getOrDefault(player.getUniqueId(), "0.0.0.0");

        if (plugin.getDataConfig().contains(ip)) {
            String savedName = plugin.getDataConfig().getString(ip + ".username");
            if (!savedName.equalsIgnoreCase(player.getName())) {
                player.kickPlayer("Этот IP уже привязан к аккаунту: " + savedName);
                return true;
            } else {
                player.sendMessage("Ты уже авторизован.");
                return true;
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.vimeworld.com/misc/token/" + token);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                JsonObject json = JsonParser.parseReader(in).getAsJsonObject();
                JsonObject owner = json.getAsJsonObject("owner");
                String rank = owner.get("rank").getAsString();
                String username = owner.get("username").getAsString();
                plugin.getDataConfig().set(ip + ".rank", rank);
                plugin.getDataConfig().set(ip + ".username", username);
                plugin.saveData();
                plugin.getLuckPerms().getUserManager().loadUser(player.getUniqueId()).thenAcceptAsync(user -> {
                    user.data().add(InheritanceNode.builder(rank).build());
                    plugin.getLuckPerms().getUserManager().saveUser(user);
                });
                player.sendMessage("Успешно авторизован как " + username + ". Ранг: " + rank);
            } catch (Exception e) {
                player.sendMessage("Ошибка при проверке токена: " + e.getMessage());
            }
        });

        return true;
    }
}