package dev.anullihate.ertp;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.potion.Effect;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Main extends PluginBase {

    private Vector3 tempVector = new Vector3();
    private static final Random random = new Random(System.currentTimeMillis());
    private Map<UUID, Location> backloc = new HashMap();

    public static boolean rand() {
        return random.nextBoolean();
    }

    private static int rand(int min, int max) {
        return min == max ? max : min + random.nextInt(max - min);
    }

    private Position getHighestPosition(Position p) {
        int ex = p.getFloorX();
        int ze = p.getFloorZ();

        for(int y = 127; y >= 0; --y) {
            if (p.level.getBlock(this.tempVector.setComponents((double)ex, (double)y, (double)ze)).isSolid()) {
                return new Position((double)ex + 0.5D, p.level.getBlock(this.tempVector.setComponents((double)ex, (double)y, (double)ze)).getBoundingBox().getMaxY(), (double)ze + 0.5D, p.level);
            }
        }

        return null;
    }

    @Override
    public void onEnable() {
        //
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == this.getServer().getConsoleSender()) {
            sender.sendMessage("\u00A7cThis command only works in game");
            return true;
        }
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        Location location = player.getLocation();
        if (command.getName().equalsIgnoreCase("rtp")) {
            if (args.length < 1) {
                sender.sendMessage("\u00A7l\u00A7c--- Envy RTP HELP ---");
                sender.sendMessage("\u00A7crtp: \u00A73/rtp now");
                sender.sendMessage("\u00A7cback: \u00A73/rtp back");
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "now":
                    if (player.hasPermission("rtp.command")) {
                        this.backloc.put(uuid, location);
                        Level level = player.getLevel();
                        Location newLocation = new Location(
                                (double)rand(-7500, 7500),
                                -0.5d,
                                (double)rand(-7500, 7500),
                                level
                        );
                        player.teleport(newLocation);
                        player.addEffect(Effect.getEffect(15)
                        .setAmplifier(5000)
                        .setDuration(20)
                        .setVisible(false));
                        this.getServer().getScheduler().scheduleDelayedTask(this, () -> {
                            ((Player)sender).teleport(this.getHighestPosition(((Player)sender).getPosition()));
                        }, 20);

                        this.getServer().getScheduler().scheduleDelayedTask(this, () -> {
                            int block = player.getPosition().getSide(BlockFace.UP).getLevelBlock().getId();
                            if (block == 9) {
                                player.addEffect(Effect.getEffect(13).setDuration(30 * 20).setVisible(false));
                            }

                        }, 25);
                    }
                    return true;
                case "back":
                    if (player.hasPermission("rtp.command")) {
                        if (this.backloc.isEmpty()) {
                            player.sendMessage("\u00A7cNo last location found!" );
                        } else {
                            player.teleport(this.backloc.get(uuid));
                            this.backloc.remove(uuid);
                        }
                    }
                    return true;
                default:
                    sender.sendMessage("\u00A7l\u00A7c--- Envy RTP HELP ---");
                    sender.sendMessage("\u00A7crtp: \u00A73/rtp now");
                    sender.sendMessage("\u00A7cback: \u00A73/rtp back");
                    return true;
            }
        }
        return true;
    }
}
