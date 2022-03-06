package me.yumei.customnpc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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

import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class NPC {

    private static List<EntityPlayer> NPCArray = new ArrayList<EntityPlayer>();

    public static void createNPC(Player player, String skinNick) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Objects.requireNonNull(Bukkit.getWorld(player.getWorld().getName()))).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "New NPC");

        EntityPlayer npc = new EntityPlayer(server, world, gameProfile);
        npc.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
                player.getLocation().getYaw(), player.getLocation().getPitch());

        String[] name = getSkin(player, skinNick);
        gameProfile.getProperties().put("textures", new Property("textures", name[0], name[1]));

        sendNPCPacketsForExistingPlayers(npc);
        NPCArray.add(npc);
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

    private static String[] getSkin(Player player, String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = JsonParser.parseString(String.valueOf(reader)).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject playerProperties = JsonParser.parseReader(reader2).getAsJsonObject().get("properties")
                    .getAsJsonArray().get(0).getAsJsonObject();
            String texture = playerProperties.get("value").getAsString();
            String signature = playerProperties.get("signature").getAsString();

            return new String[] { texture, signature };

        } catch (Exception e) {
            EntityPlayer p = ((CraftPlayer) player).getHandle();
            GameProfile profile = p.getBukkitEntity().getProfile();
            Property property = profile.getProperties().get("textures").iterator().next();

            String texture = property.getValue();
            String signature = property.getSignature();

            return new String[] { texture, signature };
        }
    }

    public static List<EntityPlayer> getNPCs() {
        return NPCArray;
    }
}
