package me.yumei.customnpc;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import java.util.*;

public class NPC {

    private static List<EntityPlayer> NPCArray = new ArrayList<EntityPlayer>();

    public static void createNPC(Player player) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Objects.requireNonNull(Bukkit.getWorld(player.getWorld().getName()))).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "New NPC");

        EntityPlayer npc = new EntityPlayer(server, world, gameProfile);
        npc.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
                player.getLocation().getYaw(), player.getLocation().getPitch());
        sendNPCPacketsForExistingPlayers(npc);
    }

    public static void sendNPCPacketsForExistingPlayers(EntityPlayer npc) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
            connection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc));
            connection.a(new PacketPlayOutNamedEntitySpawn(npc));
            connection.a(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.aY * 256 / 360)));
        }
    }

    public static void sendNPCPacketsForNewPlayer(Player player) {
        for (EntityPlayer npc : NPCArray) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
            connection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc));
            connection.a(new PacketPlayOutNamedEntitySpawn(npc));
            connection.a(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.aY * 256 / 360)));
        }
    }

    public static List<EntityPlayer> getNPCs() {
        return NPCArray;
    }
}
