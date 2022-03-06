package me.yumei.customnpc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if((NPC.getNPCs() == null) || (NPC.getNPCs().isEmpty())) {
            return;
        }
        NPC.sendNPCPacketsForNewPlayer(event.getPlayer());
    }
}
