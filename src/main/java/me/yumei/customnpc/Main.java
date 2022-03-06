package me.yumei.customnpc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("createnpc")) {
            if(!(sender instanceof Player)) {
                return true;
            }
            Player player = (Player) sender;
            if(args.length == 0) {
                NPC.createNPC(player, player.getName());
                player.sendMessage("NPC created");
                return true;
            }
            NPC.createNPC(player, args[0]);
            player.sendMessage("NPC created");
            return true;
        }
        return false;
    }
}
